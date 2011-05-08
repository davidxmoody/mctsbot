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
import mctsbot.nodes.OpponentNode;
import tools.EverythingWekaFormat;
import weka.classifiers.DistributionClassifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;

public class SimpleWekaNextActionOpponentModel implements NextActionOpponentModel {
	
	private static final String DEFAULT_ARFF_HEADER_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\template.arff";
	
	private static final String DEFAULT_ARFF_FILE_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\output.arff";
	
	private static final String DEFAULT_CLASSIFIER_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\nextactionmodel.model";
	
	private static final int NUM_CLASSIFIERS_PER_STAGE = 
		EverythingWekaFormat.NUM_ACTIONS_TO_WRITE;
	
	private static EverythingWekaFormat nMOMWekaFormat = null;
	
	private DistributionClassifier[][] classifiers = null;
	
	
	public SimpleWekaNextActionOpponentModel() {
		
		try {
			
			// Load Template.
			Instances template = new Instances(new BufferedReader(
					new FileReader(DEFAULT_ARFF_HEADER_LOCATION)));
			template.setClassIndex(template.numAttributes()-1);
			
			// Create Weka Format.
			nMOMWekaFormat = new EverythingWekaFormat(template);
			
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
	
	
	public double[] getActionProbabilities(OpponentNode opponentNode) {
		
		Instance inst = null;
		
		try {
		
			inst = nMOMWekaFormat.getInstanceOpponent(opponentNode);
			
			return getClassifier(opponentNode).distributionForInstance(inst);
		
		} catch(Exception e) {
			System.err.println("***************************************");
			e.printStackTrace();
			if(inst!=null) System.err.println(inst + "  i=" + inst.classIndex());
//			throw new RuntimeException(e);
			
			//TODO: fix this bug
			return new double[] {0.333333, 0.333333, 0.333334};
		}
	}
	
	private DistributionClassifier getClassifier(OpponentNode opponentNode) {
		final int stageIndex = opponentNode.getGameState().getStage();
		final List<Action> actions = opponentNode.getGameState().getNextPlayerToAct().getActions(stageIndex);
		final int actionIndex = actions==null?0:(actions.size()>NUM_CLASSIFIERS_PER_STAGE-1?NUM_CLASSIFIERS_PER_STAGE-1:actions.size());
		return classifiers[stageIndex][actionIndex];
	}
	
	public void rebuildClassifier() throws Exception {
		classifiers = new DistributionClassifier[4][NUM_CLASSIFIERS_PER_STAGE];
		
		final Instances data = new Instances(new BufferedReader(
				new FileReader(DEFAULT_ARFF_FILE_LOCATION)));
		
//		for(int stageIndex=3; stageIndex>=0; stageIndex--) {
//			for(int actionIndex=NUM_CLASSIFIERS_PER_STAGE-1; actionIndex>=0; actionIndex--) {
//				data.setClassIndex(1+stageIndex*NUM_CLASSIFIERS_PER_STAGE+actionIndex);
//				if(stageIndex!=3 || actionIndex!=NUM_CLASSIFIERS_PER_STAGE-1) 
//					data.deleteAttributeAt(1+stageIndex*NUM_CLASSIFIERS_PER_STAGE+actionIndex+1);
//				classifiers[stageIndex][actionIndex] = makeClassifier();
//				classifiers[stageIndex][actionIndex].buildClassifier(data);
////				System.out.println("classifier[" + stageIndex + "][" + actionIndex + "] has been built");
//			}
//		}
		
//		int stageIndex = 0;
//		int actionIndex = 0;
//		final int numAttributes = EverythingWekaFormat.NUM_ATTRIBUTES;
		
		// Game result, other players hand strength and table strength.
//		data.deleteAttributeAt(numAttributes-1);
//		data.deleteAttributeAt(numAttributes-2);
//		data.deleteAttributeAt(numAttributes-3);
		
//		classifiers[stageIndex][actionIndex] = makeClassifier();
//		classifiers[stageIndex][actionIndex].buildClassifier(data);
		
		int attributeIndex = 0;
		
		for(int stageIndex=0; stageIndex<=3; stageIndex++) {
			
			attributeIndex++;
			
			for(int actionIndex=0; actionIndex<NUM_CLASSIFIERS_PER_STAGE; actionIndex++) {
				
				attributeIndex++;
				data.setClassIndex(attributeIndex);
				
//				if(stageIndex!=3 || actionIndex!=NUM_CLASSIFIERS_PER_STAGE-1) 
//					data.deleteAttributeAt(1+stageIndex*NUM_CLASSIFIERS_PER_STAGE+actionIndex+1);
				
				classifiers[stageIndex][actionIndex] = makeClassifier();
				classifiers[stageIndex][actionIndex].buildClassifier(data);
				
//				System.out.println("stageIndex = " + stageIndex + 
//									", actionIndex = " + actionIndex + 
//									", attIndex = " + attributeIndex);
				
//				System.out.println("classifier[" + stageIndex + "][" + actionIndex + "] has been built");
				
			}
		}		
		
		saveClassifier();
	}
	
	private DistributionClassifier makeClassifier() {
		return new NaiveBayes();
	}
	
	public void loadClassifier() throws Exception {
		final ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(DEFAULT_CLASSIFIER_LOCATION));
		classifiers = (DistributionClassifier[][]) ois.readObject();
		ois.close();
	}
	
	public void saveClassifier() throws IOException {
		if(classifiers==null) return;
		
		final ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(DEFAULT_CLASSIFIER_LOCATION));
		oos.writeObject(classifiers);
		oos.flush();
		oos.close();
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("Building classifier...");
		final long startTime = System.currentTimeMillis();
		SimpleWekaNextActionOpponentModel nmom = new SimpleWekaNextActionOpponentModel(0);
		nmom.rebuildClassifier();
		System.out.println("Successfully rebuilt classifier in " + 
				(System.currentTimeMillis()-startTime) + "ms.");
		System.out.println("Classifier type = " + nmom.classifiers[0][0].getClass().getSimpleName());
		System.out.println();
		
		for(int i=0; i<4; i++) {
			for(int j=0; j<NUM_CLASSIFIERS_PER_STAGE; j++) {
				System.out.println("classifier[" + i + "][" + j + "] *********************");
				System.out.println(nmom.classifiers[i][j]);
			}
		}
		
		
		
		
		
	}
	


}






