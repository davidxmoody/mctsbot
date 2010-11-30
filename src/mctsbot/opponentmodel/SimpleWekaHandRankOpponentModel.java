package mctsbot.opponentmodel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

import mctsbot.actions.Action;
import mctsbot.actions.RaiseAction;
import mctsbot.gamestate.GameState;
import mctsbot.gamestate.Player;
import mctsbot.nodes.ShowdownNode;
import weka.classifiers.Classifier;
import weka.classifiers.trees.m5.M5P;
import weka.core.Instance;
import weka.core.Instances;

import com.biotools.meerkat.Card;

public class SimpleWekaHandRankOpponentModel implements HandRankOpponentModel {
	
	private static final String FILE_LOCATION = "S:\\Workspace\\MCTSBot\\weka\\simplehrom.arff";
	
	private Classifier classifier;
	private Instances data;
	
	
	public SimpleWekaHandRankOpponentModel() {
		
		try {
			
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
	
	
	private Instance getInstance(GameState gameState, int seat) {
		
		final Player player = gameState.getPlayer(seat);
		
		final Instance instance = new Instance(13);
		int i = 0;
		
		// Actions.
		for(int j=GameState.PREFLOP; j<=GameState.RIVER; j++) {
			int raiseCount = 0;
			for(Action action: player.getActions(j))
				if(action instanceof RaiseAction) raiseCount++;
			
			if(raiseCount<1) instance.setValue(i++, "c");
			else if(raiseCount==1) instance.setValue(i++, "r");
			else if(raiseCount>1) instance.setValue(i++, "rr");
		}
		
		// Table cards.
		int[] ranks = new int[5];
		int[] suits = new int[5];
		
		int[] cards = gameState.getTable().getCardArray();
		
		for(int j=0; j<5; j++) {
			ranks[j] = Card.getRank(cards[j+1]);
			suits[j] = Card.getSuit(cards[j+1]);
		}
		
		// Write card ranks
		for(int j=0; j<5; j++) {
			instance.setValue(i++, Card.getRankChar(ranks[j]));
		}
		
		// Write suits.
		int[] suitCounts = new int[4];
		
		// Flop.
		suitCounts[suits[0]]++;
		suitCounts[suits[1]]++;
		suitCounts[suits[2]]++;
		
		String numSuitedFlop = "1,";
		for(int j=0; j<4; j++) {
			if(suitCounts[j]==3) {
				numSuitedFlop = "3,";
				break;
			} else if(suitCounts[j]==2) {
				numSuitedFlop = "2,";
				break;
			} 
		}
		instance.setValue(i++, numSuitedFlop);
		
		// Turn.
		suitCounts[suits[3]]++;
		
		String numSuitedTurn = "1,";
		for(int j=0; j<4; j++) {
			if(suitCounts[j]==4) {
				numSuitedTurn = "4,";
				break;
			} else if(suitCounts[j]==3) {
				numSuitedTurn = "3,";
				break;
			} else if(suitCounts[j]==2) {
				for(int k=0; k<4; k++) {
					if(k!=j && suitCounts[k]==2) {
						numSuitedTurn = "22,";
					} else if(suitCounts[k]==1) {
						numSuitedTurn = "2,";
					}
				}
			} 
		}
		instance.setValue(i++, numSuitedTurn);
		
		// River.
		suitCounts[suits[4]]++;
		
		String numSuitedRiver = "0,";
		for(int j=0; j<4; j++) {
			if(suitCounts[j]==5) {
				numSuitedRiver = "5,";
				break;
			} else if(suitCounts[j]==4) {
				numSuitedRiver = "4,";
				break;
			} else if(suitCounts[j]==3) {
				numSuitedRiver = "3,";
				break;
			}
		}
		instance.setValue(i++, numSuitedRiver);
		
		
		
		
		
		return instance;
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
		
		System.out.println("main is running");
		
		
		final Random random = new Random();
		
		getDataFile();
		
		SimpleWekaHandRankOpponentModel swhrom = new SimpleWekaHandRankOpponentModel();
		
		Instance inst = new Instance(13);
		int i=0;
		
		inst.setDataset(swhrom.data);
		
		inst.setValue(i++, "c");
		inst.setValue(i++, "c");
		inst.setValue(i++, "c");
		inst.setValue(i++, "c");
		inst.setValue(i++, "5");
		inst.setValue(i++, "5");
		inst.setValue(i++, "T");
		inst.setValue(i++, "2");
		inst.setValue(i++, "7");
		inst.setValue(i++, "1");
		inst.setValue(i++, "1");
		inst.setValue(i++, "0");
		
		final double result = swhrom.classifier.classifyInstance(inst);
		
		System.out.println(inst);
		System.out.println((int)result);
		System.out.println();
		
		
		Instance inst2 = swhrom.data.instance(random.nextInt(swhrom.data.numInstances()));
		System.out.println(inst2);
		System.out.println((int)swhrom.classifier.classifyInstance(inst2));
		System.out.println();
		
		Instance inst3 = swhrom.data.instance(random.nextInt(swhrom.data.numInstances()));
		System.out.println(inst3);
		System.out.println((int)swhrom.classifier.classifyInstance(inst3));
		System.out.println();
		
		Instance inst4 = swhrom.data.instance(random.nextInt(swhrom.data.numInstances()));
		System.out.println(inst4);
		System.out.println((int)swhrom.classifier.classifyInstance(inst4));
		System.out.println();
	}

}






