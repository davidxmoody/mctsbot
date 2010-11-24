package mctsbot.opponentmodel.handrank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import mctsbot.gamestate.GameState;
import mctsbot.gamestate.Player;
import mctsbot.nodes.ShowdownNode;
import weka.classifiers.trees.M5P;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import com.biotools.meerkat.Holdem;

public class SimpleWekaHandRankOpponentModel implements HandRankOpponentModel {
	
	private static final String FILE_LOCATION = "S:\\Workspace\\MCTSBot\\weka\\simplehrom.arff";
	
	private final M5P classifier;
	
	
	public SimpleWekaHandRankOpponentModel() throws Exception {
		
		//final DataSource source = new DataSource(FILE_LOCATION);
		
		//final Instances data = source.getDataSet();
		
		final Instances data = new Instances(new BufferedReader(new FileReader(FILE_LOCATION)));
		
		data.setClassIndex(data.numAttributes()-1);
		
		
		classifier = new M5P();
		
		//TODO: set classifier options.
		
		classifier.buildClassifier(data);
		
		
		
		
		
		
	}
	
	
	private Instance getInstance(GameState gameState, int seat) {
		
		final Player player = gameState.getPlayer(seat);
		
		final Instance instance = new DenseInstance(17);
		instance.setValue(0, "");
		
		
		
		
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
				
				final BufferedWriter w = new BufferedWriter(new FileWriter(file));
				
				w.write("@RELATION SimpleHROppModel\r");
				w.write("\r");
				w.write("@ATTRIBUTE preflop_actions {c,r,rr}\r");
				w.write("@ATTRIBUTE flop_actions {c,r,rr}\r");
				w.write("@ATTRIBUTE turn_actions {c,r,rr}\r");
				w.write("@ATTRIBUTE river_actions {c,r,rr}\r");
				w.write("@ATTRIBUTE c_card_1_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
				w.write("@ATTRIBUTE c_card_2_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
				w.write("@ATTRIBUTE c_card_3_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
				w.write("@ATTRIBUTE c_card_4_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
				w.write("@ATTRIBUTE c_card_5_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
				w.write("@ATTRIBUTE num_suited_flop {1,2,3}\r");
				w.write("@ATTRIBUTE num_suited_turn {1,2,3,4,22}\r");
				w.write("@ATTRIBUTE num_suited_river {0,3,4,5}\r");
				w.write("@ATTRIBUTE hand_rank NUMERIC\r");
				w.write("\r");
				w.write("@DATA\r");
				
				w.close();
				
			} // else it already existed.
			
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		return file;
	}
	
	
	public static void main(String[] args) throws Exception {
		
		getDataFile();
		
		System.out.println(Holdem.PREFLOP);
		System.out.println(Holdem.FLOP);
		System.out.println(Holdem.TURN);
		System.out.println(Holdem.RIVER);
		
		
		
		
	}

}
