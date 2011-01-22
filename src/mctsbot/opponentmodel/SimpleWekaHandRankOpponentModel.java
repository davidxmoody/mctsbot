package mctsbot.opponentmodel;

import java.io.BufferedReader;
import java.io.FileReader;

import mctsbot.gamestate.Player;
import mctsbot.nodes.ShowdownNode;
import tools.SBHROMWekaFormat;
import weka.classifiers.Classifier;
import weka.classifiers.trees.m5.M5P;
import weka.core.Instance;
import weka.core.Instances;

public class SimpleWekaHandRankOpponentModel implements HandRankOpponentModel {
	
	private static final SBHROMWekaFormat hROMWekaFormat = new SBHROMWekaFormat();
	private static final String FILE_LOCATION = null;
	
	private Classifier classifier;
	private Instances data;
	
	
	public SimpleWekaHandRankOpponentModel() {
		
		try {
			
			//TODO
			
			//final DataSource source = new DataSource(FILE_LOCATION);
			//final Instances data = source.getDataSet();
			
			
			data = new Instances(new BufferedReader(new FileReader(FILE_LOCATION)));
			
			
			
			data.setClassIndex(data.numAttributes()-1);
			
			
			classifier = new M5P();
			
			// Need to set classifier options here?
			
			classifier.buildClassifier(data);
			
			
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
	
	public void buildClassifier() {
		//TODO
	}
	

}






