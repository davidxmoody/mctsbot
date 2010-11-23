package mctsbot.opponentmodel.handrank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import mctsbot.gamestate.GameState;
import mctsbot.nodes.ShowdownNode;
import weka.classifiers.trees.M5P;
import weka.core.Instance;
import weka.core.Instances;

public class SimpleWekaHandRankOpponentModel implements HandRankOpponentModel {
	
	private static final String FILE_LOCATION = "S:\\Workspace\\MCTSBot\\weka\\simplehrom.arff";
	
	private final M5P classifier;
	
	private Instance testInstance = null;
	
	
	public SimpleWekaHandRankOpponentModel() throws Exception {
		
		//final DataSource source = new DataSource(FILE_LOCATION);
		
		//final Instances data = source.getDataSet();
		
		final Instances data = new Instances(new BufferedReader(new FileReader(FILE_LOCATION)));
		
		data.setClassIndex(2);
		
		
		testInstance = data.firstInstance();
		
		
		
		classifier = new M5P();
		
		//TODO: set classifier options.
		
		classifier.buildClassifier(data);
		
		
	}
	
	
	private Instance getInstance(GameState gameState, int seat) {
		return null;
	}

	
	public int getRank(ShowdownNode showdownNode, int oppSeat) {
		try {
			
			final Instance instance = getInstance(showdownNode.getGameState(), oppSeat);
			
			final double result = classifier.classifyInstance(instance);
			
			
			
			
			
			
			return (int)result;
			
		} catch(Exception e) {
			throw new RuntimeException();
		}
	}
	
	private static File getDataFile() {
		File file = null;
		try {
			file = new File(FILE_LOCATION);
			
			if(file.createNewFile()) {
				// Create the default file.
				
				final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				
				writer.write("@RELATION SimpleHROppModel\r");
				writer.write("\r");
				writer.write("@ATTRIBUTE times_raised NUMERIC\r");
				writer.write("@ATTRIBUTE times_called NUMERIC\r");
				writer.write("@ATTRIBUTE hand_rank NUMERIC\r");
				writer.write("\r");
				writer.write("@DATA\r");
				
				writer.close();
				
			} // else it already existed.
			
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		return file;
	}
	
	
	public static void main(String[] args) throws Exception {
		
		getDataFile();
		
		final SimpleWekaHandRankOpponentModel swhrom = new SimpleWekaHandRankOpponentModel();
		
		System.out.println(swhrom.testInstance);
		
		System.out.println(swhrom.classifier.classifyInstance(swhrom.testInstance));
		
		
		
		
	}

}
