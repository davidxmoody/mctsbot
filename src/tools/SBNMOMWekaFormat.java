package tools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import mctsbot.actions.Action;
import mctsbot.actions.BigBlindAction;
import mctsbot.actions.CallAction;
import mctsbot.actions.FoldAction;
import mctsbot.actions.RaiseAction;
import mctsbot.actions.SmallBlindAction;
import mctsbot.gamestate.GameState;
import mctsbot.gamestate.Player;
import mctsbot.nodes.OpponentNode;
import weka.core.Instance;
import weka.core.Instances;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.Hand;

public class SBNMOMWekaFormat implements WekaFormat {

	// TODO: Remember to update this.
	public static final int NUM_ATTRIBUTES = 13;
	
	public static final int NUM_ACTIONS_TO_WRITE = 3;
	
	private static final String RELATION_TITLE = "SimpleBotNMOM";
	
	private Instances template = null;
	
	
	public SBNMOMWekaFormat(Instances template) {
		this.template = template;
	}
	
	public void writeHeader(BufferedWriter out) throws Exception {
		out.write("@RELATION " + RELATION_TITLE + "\r");
		out.write("\r");
		
		out.write("@ATTRIBUTE blind {none,small,big}\r");
		
		out.write("@ATTRIBUTE preflop_action_1 {r,c,f}\r");
		out.write("@ATTRIBUTE preflop_action_2 {r,c,f}\r");
		out.write("@ATTRIBUTE preflop_action_3 {r,c,f}\r");

		out.write("@ATTRIBUTE flop_action_1 {r,c,f}\r");
		out.write("@ATTRIBUTE flop_action_2 {r,c,f}\r");
		out.write("@ATTRIBUTE flop_action_3 {r,c,f}\r");
		
		out.write("@ATTRIBUTE turn_action_1 {r,c,f}\r");
		out.write("@ATTRIBUTE turn_action_2 {r,c,f}\r");
		out.write("@ATTRIBUTE turn_action_3 {r,c,f}\r");
		
		out.write("@ATTRIBUTE river_action_1 {r,c,f}\r");
		out.write("@ATTRIBUTE river_action_2 {r,c,f}\r");
		out.write("@ATTRIBUTE river_action_3 {r,c,f}\r");
		
//		out.write("@ATTRIBUTE c_card_1_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
//		out.write("@ATTRIBUTE c_card_2_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
//		out.write("@ATTRIBUTE c_card_3_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
//		out.write("@ATTRIBUTE c_card_4_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
//		out.write("@ATTRIBUTE c_card_5_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
//		
//		out.write("@ATTRIBUTE num_suited_flop {1,2,3}\r");
//		out.write("@ATTRIBUTE num_suited_turn {1,2,3,4,22}\r");
//		out.write("@ATTRIBUTE num_suited_river {0,3,4,5}\r");
//		
//		out.write("@ATTRIBUTE table_strength NUMERIC\r");
		
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
		
		final FileOutputType inst = new FileOutputType();
		
//		final Hand table = gameRecord.getTable();
		
		// Blind.
		setBlindValue(inst,player.getActions(GameState.PREFLOP));
		
		// Actions.
		setActionValues(inst, player.getActions(GameState.PREFLOP));
		setActionValues(inst, player.getActions(GameState.FLOP));
		setActionValues(inst, player.getActions(GameState.TURN));
		setActionValues(inst, player.getActions(GameState.RIVER));
		
		// Table Cards.
//		setTableCardValues(inst, table);
		
		// Table Strength.
//		inst.setNextValue(
//				HandStrengthConverter.rankToStrength(gameRecord.getTableRank()));
		
		// Write to file.
		inst.write(out);
	}
	
	public Instance getInstance(OpponentNode opponentNode) {
		final InstanceOutputType inst = new InstanceOutputType();
		
//		final Hand table = opponentNode.getGameState().getTable();
		final Player opponent = opponentNode.getGameState().getNextPlayerToAct();
		
		// Blind.
		setBlindValue(inst,opponent.getActions(GameState.PREFLOP));
		
		// Actions.
		setActionValues(inst, opponent.getActions(GameState.PREFLOP));
		setActionValues(inst, opponent.getActions(GameState.FLOP));
		setActionValues(inst, opponent.getActions(GameState.TURN));
		setActionValues(inst, opponent.getActions(GameState.RIVER));
		
		// Table Cards.
//		setTableCardValues(inst, table);
		
		// Return The Instance.
		return inst.getInstance();
	}
	
	
	
	private void setBlindValue(OutputType inst, List<Action> actions) {
		if(actions==null || actions.size()==0) {
			inst.setNextValue("none");
			return;
		}
		
		final Action action = actions.get(0);
		if(action instanceof SmallBlindAction) {
			inst.setNextValue("small");
		} else if(action instanceof BigBlindAction) {
			inst.setNextValue("big");
		} else {
			inst.setNextValue("none");
		}
		
	}
	
	private void setActionValues(OutputType inst, List<Action> actions) {
		if(actions==null) {
			for(int i=0; i<3; i++) inst.setNextValueUnknown();
			return;
		}
		
		int numActionsWritten = 0;
		for(Action action: actions) {
			if(action instanceof RaiseAction) {
				inst.setNextValue("r");
				numActionsWritten++;
			} else if(action instanceof CallAction) {
				inst.setNextValue("c");
				numActionsWritten++;
			} else if(action instanceof FoldAction) {
				inst.setNextValue("f");
				numActionsWritten++;
			} else if(action instanceof SmallBlindAction) {
				// Do nothing.
			} else if(action instanceof BigBlindAction) {
				// Do nothing
			} else {
				throw new RuntimeException("unknown action type: " + 
						action.getClass().getSimpleName());
			}
		}
		while(numActionsWritten<NUM_ACTIONS_TO_WRITE) {
			inst.setNextValueUnknown();
			numActionsWritten++;
		}
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
