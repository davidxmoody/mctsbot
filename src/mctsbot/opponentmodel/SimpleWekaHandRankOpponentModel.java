package mctsbot.opponentmodel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import mctsbot.gamestate.Player;
import mctsbot.nodes.ShowdownNode;
import tools.EverythingWekaFormat;
import weka.classifiers.DistributionClassifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;

public class SimpleWekaHandRankOpponentModel implements HandRankOpponentModel {
	
	private static final String DEFAULT_ARFF_HEADER_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\template.arff";
	
	private static final String DEFAULT_ARFF_FILE_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\output.arff";
	
	private static final String DEFAULT_CLASSIFIER_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\mymodel.model";
	
	private static EverythingWekaFormat hROMWekaFormat = null;
	
	private DistributionClassifier classifier = null;
	
	
	public SimpleWekaHandRankOpponentModel() {
		
		try {
			
			// Load Template.
			Instances template = new Instances(new BufferedReader(
					new FileReader(DEFAULT_ARFF_HEADER_LOCATION)));
			template.setClassIndex(template.numAttributes()-1);
			
			// Create Weka Format.
			hROMWekaFormat = new EverythingWekaFormat(template);
			
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
	
	private SimpleWekaHandRankOpponentModel(int i) { }
	
	public double probOfBeatingOpponent(ShowdownNode showdownNode, Player opponent, int botHandRank) {
		
		Instance instance = null;
		try {
			
			instance = hROMWekaFormat.getInstanceShowdown(showdownNode, opponent, botHandRank);
			
			final double[] probabilities = classifier.distributionForInstance(instance);
			
//			for(int i=0; i<probabilities.length; i++){
//				System.out.print(probabilities[i] + "  ");
//			}
//			System.out.println();
			
			final double result = probabilities[0];
			
			return result;
			
		} catch(Exception e) {
			System.out.println(instance);
			throw new RuntimeException(e);
		}
	}
	
	public void rebuildClassifier() throws Exception {
		
		final Instances data = new Instances(new BufferedReader(
				new FileReader(DEFAULT_ARFF_FILE_LOCATION)));
		data.setClassIndex(data.numAttributes()-1);
		
		classifier = new NaiveBayes();
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
		SimpleWekaHandRankOpponentModel hrom = new SimpleWekaHandRankOpponentModel(0);
		hrom.rebuildClassifier();
		System.out.println("Successfully rebuilt classifier in " + 
				(System.currentTimeMillis()-startTime) + "ms.");
		System.out.println("Classifier type = " + hrom.classifier.getClass().getSimpleName());
	}

}






