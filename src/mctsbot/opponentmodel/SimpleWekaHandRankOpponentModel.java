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
	
	private static SBHROMWekaFormat hROMWekaFormat = null;
	
	private Classifier classifier = null;
	
	
	public SimpleWekaHandRankOpponentModel() {
		
		try {
			
			// Load Template.
			Instances template = new Instances(new BufferedReader(
					new FileReader(DEFAULT_ARFF_HEADER_LOCATION)));
			template.setClassIndex(template.numAttributes()-1);
			
			// Create Weka Format.
			hROMWekaFormat = new SBHROMWekaFormat(template);
			
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
	
	public double probOfBeatingOpponent(ShowdownNode showdownNode, Player opponent, int botHandRank) {
		try {
			
			final Instance instance = hROMWekaFormat.getInstance(showdownNode, opponent, botHandRank);
			
			final double result = classifier.classifyInstance(instance);
			
			return result;
			
		} catch(Exception e) {
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






