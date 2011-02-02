package tools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import mctsbot.actions.Action;
import mctsbot.actions.CallAction;
import mctsbot.actions.RaiseAction;
import mctsbot.gamestate.GameState;
import mctsbot.gamestate.Player;
import mctsbot.nodes.OpponentNode;
import weka.core.Instance;
import weka.core.Instances;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.Hand;

public class SBNMOMWekaFormat implements WekaFormat {
	
	/*
	private static final int HIGHEST_HIGH_CARD = 1276;
	private static final int HIGHEST_PAIR = 4136;
	private static final int HIGHEST_TWO_PAIRS = 4994;
	private static final int HIGHEST_THREE_OF_A_KIND = 5852;
	private static final int HIGHEST_STRAIGHT = 5862;
	private static final int HIGHEST_FLUSH = 7139;
	private static final int HIGHEST_FULL_HOUSE = 7295;
	private static final int HIGHEST_FOUR_OF_A_KIND = 7451;
	private static final int HIGHEST_STRAIGHT_FLUSH = 7461;
	
	private static final String HIGH_CARD = "high_card";
	private static final String ONE_PAIR = "one_pair";
	private static final String TWO_PAIRS = "two_pairs";
	private static final String THREE_OF_A_KIND = "three_of_a_kind";
	private static final String STRAIGHT = "straight";
	private static final String FLUSH = "flush";
	private static final String FULL_HOUSE = "full_house";
	private static final String FOUR_OF_A_KIND = "four_of_a_kind";
	private static final String STRAIGHT_FLUSH = "straight_flush";
	*/
	
	// TODO: Remember to update this.
	private static final int NUM_ATTRIBUTES = 31;
	
	private static final String RELATION_TITLE = "SimpleBotNMOM";
	
	private Instances template = null;
	
	
	public SBNMOMWekaFormat(Instances template) {
		this.template = template;
	}
	
	public void writeHeader(BufferedWriter out) throws Exception {
		out.write("@RELATION " + RELATION_TITLE + "\r");
		out.write("\r");
		
		out.write("@ATTRIBUTE preflop_action_1 {r,c,f,sb,bb,_}\r");
		out.write("@ATTRIBUTE preflop_action_2 {r,c,f,_}\r");
		out.write("@ATTRIBUTE preflop_action_3 {r,c,f,_}\r");
		out.write("@ATTRIBUTE preflop_action_4 {r,c,f,_}\r");
		out.write("@ATTRIBUTE preflop_action_5 {r,c,f,_}\r");

		out.write("@ATTRIBUTE flop_action_1 {r,c,f,_}\r");
		out.write("@ATTRIBUTE flop_action_2 {r,c,f,_}\r");
		out.write("@ATTRIBUTE flop_action_3 {r,c,f,_}\r");
		out.write("@ATTRIBUTE flop_action_4 {r,c,f,_}\r");
		out.write("@ATTRIBUTE flop_action_5 {r,c,f,_}\r");
		
		out.write("@ATTRIBUTE turn_action_1 {r,c,f,_}\r");
		out.write("@ATTRIBUTE turn_action_2 {r,c,f,_}\r");
		out.write("@ATTRIBUTE turn_action_3 {r,c,f,_}\r");
		out.write("@ATTRIBUTE turn_action_4 {r,c,f,_}\r");
		out.write("@ATTRIBUTE turn_action_5 {r,c,f,_}\r");
		
		out.write("@ATTRIBUTE river_action_1 {r,c,f,_}\r");
		out.write("@ATTRIBUTE river_action_2 {r,c,f,_}\r");
		out.write("@ATTRIBUTE river_action_3 {r,c,f,_}\r");
		out.write("@ATTRIBUTE river_action_4 {r,c,f,_}\r");
		out.write("@ATTRIBUTE river_action_5 {r,c,f,_}\r");
		
		out.write("@ATTRIBUTE c_card_1_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
		out.write("@ATTRIBUTE c_card_2_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
		out.write("@ATTRIBUTE c_card_3_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
		out.write("@ATTRIBUTE c_card_4_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
		out.write("@ATTRIBUTE c_card_5_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
		
		out.write("@ATTRIBUTE num_suited_flop {1,2,3}\r");
		out.write("@ATTRIBUTE num_suited_turn {1,2,3,4,22}\r");
		out.write("@ATTRIBUTE num_suited_river {0,3,4,5}\r");
		
		out.write("@ATTRIBUTE table_strength NUMERIC\r");
		
		out.write("\r");
		out.write("@DATA\r");
		out.flush();
	}
	
	public void write(GameRecord gameRecord, String name, BufferedWriter out) throws Exception {
		final PlayerRecord player;
		try {
			player = gameRecord.getPlayer(name);
		} catch(Exception e) {
			return;
		}
		
		for(int i=GameState.PREFLOP; i<=GameState.RIVER; i++) {
			if(player.getActions(i)==null) break;
			for(int j=0; j<player.getActions(i).size(); j++) {
				writeOne(gameRecord, player, i, j, out);
			}
		}
	}
	
	private void writeOne(GameRecord gameRecord, PlayerRecord player, 
			int stage, int actionNumber, BufferedWriter out) throws Exception {

		final FileOutputType fileOutputType = new FileOutputType();
		
//		final Hand table = gameRecord.getTable();
		
		// Actions.
		switch (stage) {
		case GameState.PREFLOP:
			setActionValues(fileOutputType, new LinkedList<Action>(player.getActions(GameState.PREFLOP)));
			break;

		default:
			break;
		}
		
		
		
		
		
		setActionValues(fileOutputType, player.getActions(GameState.PREFLOP));
		setActionValues(fileOutputType, player.getActions(GameState.FLOP));
		setActionValues(fileOutputType, player.getActions(GameState.TURN));
		setActionValues(fileOutputType, player.getActions(GameState.RIVER));
		
		// Table Cards.
//		setTableCardValues(fileOutputType, table);
		
		// Stage.
		fileOutputType.setNextValue((stage==GameState.PREFLOP)?"preflop":
			(stage==GameState.FLOP)?"flop":(stage==GameState.TURN)?"turn":"river");

		// Write To File.
		fileOutputType.write(out);
	}
	
	//TODO: update this
	public Instance getInstance(OpponentNode opponentNode) {
		final InstanceOutputType instanceOutputType = new InstanceOutputType();
		
		final Hand table = opponentNode.getGameState().getTable();
		final Player opponent = opponentNode.getGameState().getNextPlayerToAct();
		
		// Actions.
		setActionValues(instanceOutputType, opponent.getActions(GameState.PREFLOP));
		setActionValues(instanceOutputType, opponent.getActions(GameState.FLOP));
		setActionValues(instanceOutputType, opponent.getActions(GameState.TURN));
		setActionValues(instanceOutputType, opponent.getActions(GameState.RIVER));
		
		// Table Cards.
		setTableCardValues(instanceOutputType, table);
		
		// Return The Instance.
		return instanceOutputType.getInstance();
	}
	
	
	private void setActionValues(OutputType inst, List<Action> actions) {
//		if(actions==null) {
//			for(int i=0; i<5; i++) inst.setNextValueUnknown();
//			return;
//		}
//		
//		Action action;
//		for(int i=0; i<5; i++) {
//			try {
//				action = actions.get(i);
//				if(action instanceof RaiseAction) inst.setNextValue("r");
//				else if(action instanceof CallAction) inst.setNextValue("c");
//				else if(action instanceof FoldAction) inst.setNextValue("f");
//				else if(action instanceof SmallBlindAction) inst.setNextValue("sb");
//				else if(action instanceof BigBlindAction) inst.setNextValue("bb");
//				else inst.setNextValueUnknown();
//			} catch(IndexOutOfBoundsException e) {
//				inst.setNextValue("_");
//			}
//		}
		
		if(actions==null) {
			inst.setNextValueUnknown();
			inst.setNextValueUnknown();
			return;
		}
		
		int raiseCount = 0;
		int callCount = 0;
		for(Action action: actions) {
			if(action instanceof RaiseAction) raiseCount++;
			else if(action instanceof CallAction) callCount++;
		}
		inst.setNextValue(raiseCount);
		inst.setNextValue(callCount);
	}
	
	
	private void setTableCardValues(OutputType inst, Hand table) {
		int[] ranks = new int[5];
		int[] suits = new int[5];
		
		int[] suitCounts = new int[4];
		
		int[] cards = table.getCardArray();
		
		for(int i=0; i<5; i++) {
			ranks[i] = Card.getRank(cards[i+1]);
			suits[i] = Card.getSuit(cards[i+1]);
		}
		
		// Sort the ranks first
		Arrays.sort(ranks);
		
		// Set the ranks
		for(int i=0; i<5; i++) inst.setNextValue("" + Card.getRankChar(ranks[i]));
		
		
		// Flop.
		suitCounts[suits[0]]++;
		suitCounts[suits[1]]++;
		suitCounts[suits[2]]++;
		
		String numSuitedFlop = "1";
		for(int j=0; j<4; j++) {
			if(suitCounts[j]==3) {
				numSuitedFlop = "3";
				break;
			} else if(suitCounts[j]==2) {
				numSuitedFlop = "2";
				break;
			} 
		}
		inst.setNextValue(numSuitedFlop);
		
		// Turn.
		suitCounts[suits[3]]++;
		
		String numSuitedTurn = "1";
		for(int j=0; j<4; j++) {
			if(suitCounts[j]==4) {
				numSuitedTurn = "4";
				break;
			} else if(suitCounts[j]==3) {
				numSuitedTurn = "3";
				break;
			} else if(suitCounts[j]==2) {
				for(int k=0; k<4; k++) {
					if(k!=j && suitCounts[k]==2) {
						numSuitedTurn = "22";
					} else if(suitCounts[k]==1) {
						numSuitedTurn = "2";
					}
				}
			} 
		}
		inst.setNextValue(numSuitedTurn);
		
		// River.
		suitCounts[suits[4]]++;
		
		String numSuitedRiver = "0";
		for(int j=0; j<4; j++) {
			if(suitCounts[j]==5) {
				numSuitedRiver = "5";
				break;
			} else if(suitCounts[j]==4) {
				numSuitedRiver = "4";
				break;
			} else if(suitCounts[j]==3) {
				numSuitedRiver = "3";
				break;
			}
		}
		inst.setNextValue(numSuitedRiver);
	}
	
	
	
	private interface OutputType {
		void setNextValue(double value);
		void setNextValue(String value);
		void setNextValueUnknown();
	}
	
	private class FileOutputType implements OutputType {
		
		private StringBuilder sb = new StringBuilder();
		private boolean empty = true;

		public void setNextValue(double value) {
			setNextValue("" + value);
		}

		public void setNextValue(String value) {
			sb.append((empty?"":",") + value);
			empty = false;
		}
		
		public void setNextValueUnknown() {
			setNextValue("?");
		}
		
		public void write(BufferedWriter out) {
			try {
				out.write(sb.toString() + "\r");
				out.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private class InstanceOutputType implements OutputType {
		
		private Instance inst = null;
		private int i = 0;
		
		public InstanceOutputType() {
			inst = new Instance(NUM_ATTRIBUTES);
			inst.setDataset(template);
		}

		public void setNextValue(double value) {
//			System.err.println(value);
			inst.setValue(i++, value);
		}

		public void setNextValue(String value) {
//			System.err.println(value);
			inst.setValue(i++, value);
		}
		
		public void setNextValueUnknown() {
			inst.setMissing(i++);
		}
		
		public Instance getInstance() {
			return inst;
		}
		
	}
	
	
	
}
