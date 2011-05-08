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
	
	private static final int[] FOLD_WEIGHTS = {0, 0, 0, 0};
	private static final int[] CALL_WEIGHTS = {100, 100, 100, 100};
	private static final int[] RAISE_WEIGHTS = {0, 0, 0, 0};
	
	private static final Random random = new Random();
	
	
	public static int num = 0;
	public static double mean = 0;
	
	// TODO: simplify this class

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
	
	
	private double simulateShowdown(ShowdownNode showdownNode) {
		final GameState gameState = showdownNode.getGameState();
		final HandRankOpponentModel hrom = 
			showdownNode.getConfig().getHandRankOpponentModel();
		
		// Calculate the bot's hand rank.
		final int botHandRank = HandEvaluator.rankHand(
				gameState.getC1(), gameState.getC2(), gameState.getTable());
		
		// Estimate the probability that the bot will win the game.
		//TODO: check to see if this works OK when there are more than 
		double cumalativeWinProbability = 1.0;
		for(Player opponent: gameState.getActivePlayers()) {
			if(opponent.getSeat()==gameState.getBotSeat()) continue;
			cumalativeWinProbability *= hrom.probOfBeatingOpponent(
					showdownNode, opponent, botHandRank);
		}
		
//		System.out.println(cumalativeWinProbability);
		
		// Calculate the expected value of the game state.
		double expectedValue = gameState.getBotMoney();
		
//		if(cumalativeWinProbability>0.7) {
//			expectedValue += gameState.getPot();
//		} else if(cumalativeWinProbability>0.5) {
//			expectedValue += gameState.getPot()*0.5;
//		} 
		
		boolean won = random.nextDouble()<cumalativeWinProbability;
		if(won) expectedValue += gameState.getPot();
		
		
//		expectedValue += gameState.getPot()*(cumalativeWinProbability>0.6?1:0);
		
//		mean = (mean*num + cumalativeWinProbability)/(num+1);
//		num++;
		
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



