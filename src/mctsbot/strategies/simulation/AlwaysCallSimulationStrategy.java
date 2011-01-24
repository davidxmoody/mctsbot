package mctsbot.strategies.simulation;

import java.util.Random;

import mctsbot.gamestate.GameState;
import mctsbot.gamestate.Player;
import mctsbot.nodes.AllOpponentsFoldedNode;
import mctsbot.nodes.BotFoldedNode;
import mctsbot.nodes.ChanceNode;
import mctsbot.nodes.LeafNode;
import mctsbot.nodes.Node;
import mctsbot.nodes.PlayerNode;
import mctsbot.nodes.ShowdownNode;
import mctsbot.opponentmodel.HandRankOpponentModel;

import com.biotools.meerkat.HandEvaluator;

public class AlwaysCallSimulationStrategy implements SimulationStrategy {
	
	private static final int[] FOLD_WEIGHTS = {1, 1, 1, 1};
	private static final int[] CALL_WEIGHTS = {100, 100, 100, 100};
	private static final int[] RAISE_WEIGHTS = {1, 1, 1, 1};
	
	
	private static final Random random = new Random();

	public double simulate(Node node) {
		
		if(node instanceof LeafNode) {
			
			if(node instanceof BotFoldedNode) {
				return node.getParent().getGameState().getBotMoney();
				
			} else if(node instanceof AllOpponentsFoldedNode) {
				return node.getGameState().getBotMoney()+node.getGameState().getPot();
				
			} else if(node instanceof ShowdownNode) {
				return simulateShowdown((ShowdownNode)node);

			} else {
				throw new RuntimeException("Unknown node type passed to simulate.");
			}
			
			
		} else if(node instanceof ChanceNode) {
			return simulate(((ChanceNode)node).generateChild());
			
		} else if(node instanceof PlayerNode) {
			
			// Not actually correct when dealing with the person who put in the big blind.
			//TODO: fix this
			final boolean canCheck = node.getGameState().getMaxBetThisRound()==0.0;
			final int stage = node.getGameState().getStage();
			
			final double randomDouble = random.nextDouble();
			final double raiseProb = getRaiseProb(stage, canCheck);
			final double callProb = getCallProb(stage, canCheck);
			
			if(randomDouble<raiseProb) {
				return simulate(((PlayerNode)node).createRaiseNode());
				
			} else if(randomDouble<raiseProb+callProb) {
				return simulate(((PlayerNode)node).createCallNode());
				
			} else {
				return simulate(((PlayerNode)node).createFoldNode());
			}

			
		} else {
			throw new RuntimeException("Unknown node type passed to simulate.");
		}
		
	}
	
	
	/**
	 * Simulates a showdown event where all opponents are dealt random 
	 * cards and the winner is calculated.
	 * 
	 * @param showdownNode the node to start the simulation from.
	 * @return the amount of money the bot has after the showdown.
	 */
	private double simulateShowdown(ShowdownNode showdownNode) {
		final GameState gameState = showdownNode.getGameState();
		final HandRankOpponentModel handRankOpponentModel = 
			showdownNode.getConfig().getHandRankOpponentModel();
		
		// Calculate the bot's hand rank.
		final int botHandRank = HandEvaluator.rankHand(
				gameState.getC1(), gameState.getC2(), gameState.getTable());
		
		// For each opponent, use the hand rank opponent model to work out whether or not
		// they can beat the bot's hand. If any opponent can, then the bot loses, else 
		// the bot wins.
		// TODO: check to see if/how much this biases the results
		boolean botWins = true;
		for(Player opponent: gameState.getActivePlayers()) {
			if(opponent.getSeat()==gameState.getBotSeat()) continue;
			if(!handRankOpponentModel.beatsOpponent(showdownNode, opponent, botHandRank)) {
				botWins = false;
				break;
			}
		}
		
		// Calculate the expected value of the game state depending 
		// on whether the bot wins or loses in the showdown.
		double expectedValue = gameState.getBotMoney();
		if(botWins)	expectedValue += gameState.getPot();
		
		return expectedValue;
	}
	
	
	
	protected double getRaiseProb(int stage, boolean canCheck) {
		if(stage<GameState.PREFLOP || stage>GameState.RIVER) throw new RuntimeException(
				"Invalid stage passed to getRaiseProb: " + stage);
		
		return RAISE_WEIGHTS[stage]/
			(RAISE_WEIGHTS[stage]+CALL_WEIGHTS[stage]+(canCheck?0:FOLD_WEIGHTS[stage]));
	}
	
	protected double getCallProb(int stage, boolean canCheck) {
		if(stage<GameState.PREFLOP || stage>GameState.RIVER) throw new RuntimeException(
				"Invalid stage passed to getCallProb: " + stage);
		
		return CALL_WEIGHTS[stage]/
			(RAISE_WEIGHTS[stage]+CALL_WEIGHTS[stage]+(canCheck?0:FOLD_WEIGHTS[stage]));
	}
	
	protected double getFoldProb(int stage, boolean canCheck) {
		if(stage<GameState.PREFLOP || stage>GameState.RIVER) throw new RuntimeException(
				"Invalid stage passed to getFoldProb: " + stage);
		
		return (canCheck?0.0:(FOLD_WEIGHTS[stage]/
			(RAISE_WEIGHTS[stage]+CALL_WEIGHTS[stage]+FOLD_WEIGHTS[stage])));
	}

}



