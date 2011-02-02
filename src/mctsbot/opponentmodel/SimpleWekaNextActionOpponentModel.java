package mctsbot.opponentmodel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import mctsbot.actions.Action;
import mctsbot.actions.CallAction;
import mctsbot.actions.FoldAction;
import mctsbot.actions.RaiseAction;
import mctsbot.nodes.Node;
import mctsbot.nodes.OpponentNode;
import tools.SBNMOMWekaFormat;
import weka.classifiers.Classifier;
import weka.classifiers.DistributionClassifier;
import weka.classifiers.rules.JRip;
import weka.core.Instance;
import weka.core.Instances;

public class SimpleWekaNextActionOpponentModel implements NextActionOpponentModel {
	
	private static final String DEFAULT_ARFF_HEADER_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\nextActionTemplate.arff";
	
	private static final String DEFAULT_ARFF_FILE_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\output.arff";
	
	private static final String DEFAULT_CLASSIFIER_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\nextactionmodel.model";
	
	private static SBNMOMWekaFormat nMOMWekaFormat = null;
	
	private DistributionClassifier classifier = null;
	
	
	public SimpleWekaNextActionOpponentModel() {
		
		try {
			
			// Load Template.
			Instances template = new Instances(new BufferedReader(
					new FileReader(DEFAULT_ARFF_HEADER_LOCATION)));
			template.setClassIndex(template.numAttributes()-1);
			
			// Create Weka Format.
			nMOMWekaFormat = new SBNMOMWekaFormat(template);
			
			// Load Classifier if possible, if not then try to rebuild classifier.
			try {
				loadClassifier();
			} catch(Exception e) {
				e.printStackTrace();
				rebuildClassifier();
			}
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		
	}
	
	private SimpleWekaNextActionOpponentModel(int i) { }
	
	//TODO: check this.
	public double[] getActionProbabilities(OpponentNode opponentNode) {
		try {
		List<Node> children = opponentNode.getChildren();
		final int stage = opponentNode.getGameState().getStage();
		
		final Instance inst = nMOMWekaFormat.getInstance(opponentNode);
		
		double[] result = new double[children.size()];
		
		for(int i=0; i<children.size(); i++) {
			final Action action = children.get(i).getGameState().getLastAction();
			if(action instanceof RaiseAction) {
				result[i] = getClassifier(stage, Action.RAISE).classifyInstance(inst);
			} else if(action instanceof CallAction) {
				result[i] = getClassifier(stage, Action.CALL).classifyInstance(inst);
			} else if(action instanceof FoldAction) {
				result[i] = getClassifier(stage, Action.FOLD).classifyInstance(inst);
			} else {
				throw new RuntimeException("invalid action type: " + 
						action.getClass().getSimpleName());
			}
		}
		
		// Normalise
		double total = 0.0; 
		for(int i=0; i<result.length; i++) total += result[i];
		for(int i=0; i<result.length; i++) result[i] /= total;
		
		return result;
		
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private Classifier getClassifier(int stage, int actionType) {
		//TODO:
		return null;
	}
	
	/*public double probOfBeatingOpponent(ShowdownNode showdownNode, Player opponent, int botHandRank) {
		Instance instance = null;
		try {
			
			instance = nMOMWekaFormat.getInstance(showdownNode, opponent, botHandRank);
			
			final double result = classifier.classifyInstance(instance);
			
			return result;
			
		} catch(Exception e) {
			System.out.println(instance);
			throw new RuntimeException(e);
		}
	}*/
	
	public void rebuildClassifier() throws Exception {
		
		final Instances data = new Instances(new BufferedReader(
				new FileReader(DEFAULT_ARFF_FILE_LOCATION)));
		data.setClassIndex(data.numAttributes()-1);
		
		classifier = new JRip();
		classifier.buildClassifier(data);
		
		saveClassifier();
	}
	
	public void loadClassifier() throws Exception {
		final ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(DEFAULT_CLASSIFIER_LOCATION));
		classifier = (DistributionClassifier) ois.readObject();
		ois.close();
	}
	
	public void saveClassifier() throws IOException {
		if(classifier==null) return;
		
		final ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(DEFAULT_CLASSIFIER_LOCATION));
		oos.writeObject(classifier);
		oos.flush();
		oos.close();
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("Building classifier...");
		final long startTime = System.currentTimeMillis();
		SimpleWekaNextActionOpponentModel nmom = new SimpleWekaNextActionOpponentModel(0);
		nmom.rebuildClassifier();
		System.out.println("Successfully rebuilt classifier in " + 
				(System.currentTimeMillis()-startTime) + " seconds.");
		System.out.println("Classifier type = " + nmom.classifier.getClass().getSimpleName());
	}
	


}






