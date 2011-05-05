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
import mctsbot.nodes.ShowdownNode;
import weka.core.Instance;
import weka.core.Instances;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.Hand;
import com.biotools.meerkat.HandEvaluator;

public class EverythingWekaFormat implements WekaFormat {

	// TODO: Remember to update this.
	public static final int NUM_ATTRIBUTES = 32;
	
	public static final int NUM_ACTIONS_TO_WRITE = 4;
	
	private static final String RELATION_TITLE = "SimpleBotOpponentModel";
	
	private Instances template = null;
	
	
	public EverythingWekaFormat(Instances template) {
		this.template = template;
	}
	
	public void writeHeader(BufferedWriter out) throws Exception {
		out.write("@RELATION " + RELATION_TITLE + "\r");
		out.write("\r");
		
		out.write("@ATTRIBUTE blind {none,small,big}\r");
		
		out.write("@ATTRIBUTE preflop_checked {yes,no}\r");
		out.write("@ATTRIBUTE preflop_action_1 {r,c,f}\r");
		out.write("@ATTRIBUTE preflop_action_2 {r,c,f}\r");
		out.write("@ATTRIBUTE preflop_action_3 {r,c,f}\r");
		out.write("@ATTRIBUTE preflop_action_4 {r,c,f}\r");

		out.write("@ATTRIBUTE flop_checked {yes,no}\r");
		out.write("@ATTRIBUTE flop_action_1 {r,c,f}\r");
		out.write("@ATTRIBUTE flop_action_2 {r,c,f}\r");
		out.write("@ATTRIBUTE flop_action_3 {r,c,f}\r");
		out.write("@ATTRIBUTE flop_action_4 {r,c,f}\r");
		
		out.write("@ATTRIBUTE turn_checked {yes,no}\r");
		out.write("@ATTRIBUTE turn_action_1 {r,c,f}\r");
		out.write("@ATTRIBUTE turn_action_2 {r,c,f}\r");
		out.write("@ATTRIBUTE turn_action_3 {r,c,f}\r");
		out.write("@ATTRIBUTE turn_action_4 {r,c,f}\r");
		
		out.write("@ATTRIBUTE river_checked {yes,no}\r");
		out.write("@ATTRIBUTE river_action_1 {r,c,f}\r");
		out.write("@ATTRIBUTE river_action_2 {r,c,f}\r");
		out.write("@ATTRIBUTE river_action_3 {r,c,f}\r");
		out.write("@ATTRIBUTE river_action_4 {r,c,f}\r");
		
		
		out.write("@ATTRIBUTE flop_card_1_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
		out.write("@ATTRIBUTE flop_card_2_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
		out.write("@ATTRIBUTE flop_card_3_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
		out.write("@ATTRIBUTE flop_num_suited {1,2,3}\r");
//		out.write("@ATTRIBUTE flop_num_needed_for_straight{2,3,4}\r");
//		out.write("@ATTRIBUTE flop_pair_on_table {1,2,3}\r");
		
		out.write("@ATTRIBUTE turn_card_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
		out.write("@ATTRIBUTE turn_num_suited {1,2,3,4,22}\r");
//		out.write("@ATTRIBUTE flop_num_needed_for_straight {1,2,3}\r");
		
		out.write("@ATTRIBUTE river_card_rank {2,3,4,5,6,7,8,9,T,J,Q,K,A}\r");
		out.write("@ATTRIBUTE river_num_suited {0,3,4,5}\r");
		
		
		out.write("@ATTRIBUTE table_strength NUMERIC\r");
		
		out.write("@ATTRIBUTE other_players_hand_strength NUMERIC\r");
		
		out.write("@ATTRIBUTE game_result {win,lose,draw}\r");
		
		out.write("\r");
		out.write("@DATA\r");
		out.flush();
	}
	
	public int getAttributeIndex(int stageIndex, int actionIndex) {
		return 2 + actionIndex + 5*stageIndex;
	}
	
	public void write(GameRecord gameRecord, String name, BufferedWriter out) throws Exception {
		final PlayerRecord player;
		try {
			player = gameRecord.getPlayer(name);
		} catch(Exception e) {
			return;
		}
		
		final FileOutputType inst = new FileOutputType();
		
		final Hand table = gameRecord.getTable();
		
		// Blind.
		setBlindValue(inst,player.getActions(GameState.PREFLOP));
		
		// Actions.
		setActionValues(inst, player.getActions(GameState.PREFLOP));
		setActionValues(inst, player.getActions(GameState.FLOP));
		setActionValues(inst, player.getActions(GameState.TURN));
		setActionValues(inst, player.getActions(GameState.RIVER));
		
		// Table Cards.
		setTableCardValues(inst, table, gameRecord.getStageReached());
		
		// Return if not at showdown.
		if(gameRecord.getStageReached()!=GameState.SHOWDOWN) {
			for(int i=0; i<3; i++) inst.setNextValueUnknown();
			inst.write(out);
			return;
		}
		
		// Table Strength.
		inst.setNextValue(
				HandStrengthConverter.rankToStrength(gameRecord.getTableRank()));
		
		// Other Player's Hand Strength.
		final int playersHandRank = player.getHandRank();
		int otherPlayersHandRank = -1;
		try {
			otherPlayersHandRank = gameRecord.getPlayer("MCTSBot").getHandRank();
		} catch(Exception e) {
			for(PlayerRecord otherPlayer: gameRecord.getPlayers()) {
				if(otherPlayer!=player) {
					otherPlayersHandRank = otherPlayer.getHandRank();
					break;
				}
			}
		}
		if(otherPlayersHandRank<0) 
			throw new RuntimeException("other players hand rank is -1");
		
		inst.setNextValue(HandStrengthConverter.rankToStrength(otherPlayersHandRank));
		
		// Game Result.
		if(playersHandRank>otherPlayersHandRank) {
			inst.setNextValue("lose");
		} else if(playersHandRank<otherPlayersHandRank) {
			inst.setNextValue("win");
		} else {
			inst.setNextValue("draw");
		}
		
		// Write to file.
		inst.write(out);
	}
	
//	public Instance getInstance(Node node) {
//		if(node instanceof OpponentNode) return getInstanceOpponent((OpponentNode)node);
//		else if(node instanceof ShowdownNode) return getInstanceShowdown((ShowdownNode)node);
//		else throw new RuntimeException(
//			"getInstance called for invalid node type: " + node.getClass().getSimpleName());
//	}
	
	public Instance getInstanceShowdown(ShowdownNode showdownNode, Player opponent, int botHandRank) {
		final InstanceOutputType inst = new InstanceOutputType(NUM_ATTRIBUTES-1);
		
		final Hand table = showdownNode.getGameState().getTable();
		
		// Blind.
		setBlindValue(inst,opponent.getActions(GameState.PREFLOP));
		
		// Actions.
		setActionValues(inst, opponent.getActions(GameState.PREFLOP));
		setActionValues(inst, opponent.getActions(GameState.FLOP));
		setActionValues(inst, opponent.getActions(GameState.TURN));
		setActionValues(inst, opponent.getActions(GameState.RIVER));
		
		// Table Cards.
		setTableCardValues(inst, table, showdownNode.getGameState().getStage());
		
		// Table Strength.
		inst.setNextValue(HandStrengthConverter.rankToStrength(HandEvaluator.rankHand(table)));
		
		// Bot Hand Strength.
		inst.setNextValue(HandStrengthConverter.rankToStrength(botHandRank));
		
		// Return The Instance.
		return inst.getInstance();
	}
	
	public Instance getInstanceOpponent(OpponentNode opponentNode) {
		
		final Hand table = opponentNode.getGameState().getTable();
		final Player opponent = opponentNode.getGameState().getNextPlayerToAct();
		
		final InstanceOutputType inst = new InstanceOutputType(
				getAttributeIndex(opponentNode.getGameState().getStage(), //TODO: fix this
						opponent.getActions(opponentNode.getGameState().getStage()).size()));
		
		// Blind.
		setBlindValue(inst,opponent.getActions(GameState.PREFLOP));
		
		// Actions.
		setActionValues(inst, opponent.getActions(GameState.PREFLOP));
		setActionValues(inst, opponent.getActions(GameState.FLOP));
		setActionValues(inst, opponent.getActions(GameState.TURN));
		setActionValues(inst, opponent.getActions(GameState.RIVER));
		
		// Table Cards.
		setTableCardValues(inst, table, opponentNode.getGameState().getStage());
		
		// Table Strength (if applicable).
		if(opponentNode.getGameState().getStage()>=GameState.RIVER) {
			inst.setNextValue(HandStrengthConverter.rankToStrength(HandEvaluator.rankHand(table)));	
		} else {
			inst.setNextValueUnknown();
		}
		
		// Bot Hand Strength.
		inst.setNextValueUnknown();
		
		// Game Result.
		inst.setNextValueUnknown();
		
		// Return The Instance.
		final Instance wekaInstance = inst.getInstance();
//		wekaInstance.setMissing((1+opponentNode.getGameState().getStage())*NUM_ACTIONS_TO_WRITE);
		
//		System.err.println(wekaInstance.classIndex());
		
		return wekaInstance;
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
	
	private void checked(OutputType inst, List<Action> actions) {
		if(actions==null) {
			inst.setNextValueUnknown();
			return;
		}
		
		Action action = actions.get(0);
		if(action instanceof SmallBlindAction 
				|| action instanceof BigBlindAction) {
			try {
				action = actions.get(1);
			} catch(Exception e) {
				inst.setNextValueUnknown();
				return;
			}
		}
		
		if(action instanceof CallAction && action.getAmount()==0) {
			inst.setNextValue("yes");
		} else {
			inst.setNextValue("no");
		}
		
	}
	
	private void setActionValues(OutputType inst, List<Action> actions) {
		checked(inst, actions);
		
		if(actions==null) {
			for(int i=0; i<NUM_ACTIONS_TO_WRITE; i++) inst.setNextValueUnknown();
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
	
	
	private void setTableCardValues(OutputType inst, Hand table, int stage) {
		// Preflop.
		if(stage==GameState.PREFLOP) {
			for(int i=0; i<8; i++) inst.setNextValueUnknown();
			return;
		}
		
		// Flop.
		int[] ranks = new int[5];
		int[] suits = new int[5];
		
		int[] suitCounts = new int[4];
		
		int[] cards = table.getCardArray();
		
		for(int i=0; i<5; i++) {
			ranks[i] = Card.getRank(cards[i+1]);
			suits[i] = Card.getSuit(cards[i+1]);
		}
		
		int[] flopRanks = new int[3];
		for(int i=0; i<3; i++) flopRanks[i] = ranks[i];
		Arrays.sort(flopRanks);
		
		// Set the first three ranks
		for(int i=0; i<3; i++) inst.setNextValue("" + Card.getRankChar(flopRanks[i]));
		
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
		
		if(stage==GameState.FLOP) {
			for(int i=0; i<4; i++) inst.setNextValueUnknown();
			return;
		}
		
		// Turn.
		inst.setNextValue("" + Card.getRankChar(ranks[3]));
		
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
		
		if(stage==GameState.TURN) {
			for(int i=0; i<2; i++) inst.setNextValueUnknown();
			return;
		}
		
		// River.
		inst.setNextValue("" + Card.getRankChar(ranks[4]));
		
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
		
		public InstanceOutputType(int classIndex) {
			inst = new Instance(NUM_ATTRIBUTES);
			template.setClassIndex(classIndex);
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
			try {
				inst.setMissing(i++);
			} catch(Exception e) {
				System.err.println("============================================");
				System.err.println(inst);
				throw new RuntimeException(e);
			}
		}
		
		public Instance getInstance() {
			return inst;
		}
		
	}
	
	
	
}
