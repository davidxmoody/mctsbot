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
import tools.SBHROMWekaFormat;
import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.core.Instance;
import weka.core.Instances;

public class SimpleWekaHandRankOpponentModel implements HandRankOpponentModel {
	
	private static final String DEFAULT_ARFF_HEADER_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\template.arff";
	
	private static final String DEFAULT_ARFF_FILE_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\output.arff";
	
	private static final String DEFAULT_CLASSIFIER_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\mymodel.model";

	private static double HIGHER = 0.0;
	private static double LOWER = 1.0;
	
	private static final SBHROMWekaFormat hROMWekaFormat = new SBHROMWekaFormat();
	
	private Classifier classifier = null;
	private Instances template = null;
	
	
	public SimpleWekaHandRankOpponentModel() {
		
		try {
			
			// Load Template.
			template = new Instances(new BufferedReader(
					new FileReader(DEFAULT_ARFF_HEADER_LOCATION)));
			template.setClassIndex(template.numAttributes()-1);
			
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
	
	public boolean beatsOpponent(ShowdownNode showdownNode, Player opponent, int botHandRank) {
		try {
			
			final Instance instance = hROMWekaFormat.getInstance(showdownNode, opponent, botHandRank);
			
			final double result = classifier.classifyInstance(instance);
			
			if(result==HIGHER) {
				return false;
			} else if(result==LOWER) {
				return true;
			} else {
				throw new Exception("unknown result: " + result);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public void rebuildClassifier() throws Exception {
		
		final Instances data = new Instances(new BufferedReader(
				new FileReader(DEFAULT_ARFF_FILE_LOCATION)));
		data.setClassIndex(data.numAttributes()-1);
		
		classifier = new Logistic();
		classifier.buildClassifier(data);
		
		final ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(DEFAULT_CLASSIFIER_LOCATION));
		oos.writeObject(classifier);
		oos.flush();
		oos.close();
	}
	
	public void loadClassifier() throws Exception {
		final ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(DEFAULT_CLASSIFIER_LOCATION));
		classifier = (Classifier) ois.readObject();
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
		
		SimpleWekaHandRankOpponentModel hrom = new SimpleWekaHandRankOpponentModel();
		hrom.rebuildClassifier();
		hrom.saveClassifier();
		System.out.println("Success!");
	}

}






