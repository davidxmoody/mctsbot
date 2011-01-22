package mctsbot.opponentmodel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
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
		"S:\\Workspace\\MCTSBot\\weka\\mymodel.model";;
	
	
	private static final SBHROMWekaFormat hROMWekaFormat = new SBHROMWekaFormat();
	
	private Classifier classifier;
	private Instances data;
	
	
	public SimpleWekaHandRankOpponentModel() {
		
		try {
			/*
			//TODO
			
			//final DataSource source = new DataSource(FILE_LOCATION);
			//final Instances data = source.getDataSet();
			
			
			data = new Instances(new BufferedReader(new FileReader(FILE_LOCATION)));
			
			
			
			data.setClassIndex(data.numAttributes()-1);
			
			
			classifier = new M5P();
			
			// Need to set classifier options here?
			
			classifier.buildClassifier(data);
			
			*/
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		
	}
	
	public boolean beatsOpponent(ShowdownNode showdownNode, Player opponent, int botHandRank) {
		try {
			
			final Instance instance = hROMWekaFormat.getInstance(showdownNode, opponent, botHandRank);
			
			final double result = classifier.classifyInstance(instance);
			
			return false;//TODO
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void buildClassifier() throws Exception {
		
		data = new Instances(new BufferedReader(new FileReader(DEFAULT_ARFF_FILE_LOCATION)));
		data.setClassIndex(data.numAttributes()-1);
		
		classifier = new Logistic();
		classifier.buildClassifier(data);
		
		final ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(DEFAULT_CLASSIFIER_LOCATION));
		oos.writeObject(classifier);
		oos.flush();
		oos.close();
	}
	
	public static void main(String[] args) throws Exception {
		
//		SimpleWekaHandRankOpponentModel hrom = new SimpleWekaHandRankOpponentModel();
//		
//		hrom.buildClassifier();
		
		final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DEFAULT_CLASSIFIER_LOCATION));
		
		Classifier classifier = (Classifier) ois.readObject();
		
		Instances data = new Instances(new BufferedReader(new FileReader(DEFAULT_ARFF_FILE_LOCATION)));
		data.setClassIndex(data.numAttributes()-1);
		
		for(int i=100; i<120; i++) {
			final Instance inst = data.instance(i);
			System.out.println(inst);
			System.out.println(classifier.classifyInstance(inst));
		}
		
		
		
		
		
		
		
		
		
	}

}






