package mctsbot.strategies;

import com.biotools.meerkat.HandEvaluator;

import mctsbot.actions.Action;
import mctsbot.gamestate.GameState;
import mctsbot.nodes.AllOpponentsFoldedNode;
import mctsbot.nodes.BotFoldedNode;
import mctsbot.nodes.ChanceNode;
import mctsbot.nodes.ChoiceNode;
import mctsbot.nodes.LeafNode;
import mctsbot.nodes.Node;
import mctsbot.nodes.OpponentNode;
import mctsbot.nodes.ShowdownNode;

public class AlwaysCallSimulationStrategy implements SimulationStrategy {

	@Override
	public double simulate(Node node) {
		
		if(node instanceof LeafNode) {
			
			if(node instanceof BotFoldedNode) {
				return -1*node.getParent().getGameState().getAmountInPot(
						node.getGameState().getBotSeat());
				
			} else if(node instanceof AllOpponentsFoldedNode) {
				return node.getGameState().getPot();
				
			} else if(node instanceof ShowdownNode) {
				return simulate(node.getGameState());
				
			} else {
				throw new RuntimeException("Unknown node type passed to simulate.");
			}
			
			
		} else if(node instanceof ChanceNode) {
			return simulate(node.getGameState());
			
		} else if((node instanceof ChoiceNode) || (node instanceof OpponentNode)) {
			return simulate(node.getGameState());
			
		} else {
			throw new RuntimeException("Unknown node type passed to simulate.");
		}
		
	}
	
	private double simulate(GameState gameState) {
		
		// Simulate until showdown.
		while(gameState.getStage()<GameState.SHOWDOWN) {
			
			// Make all players call until the end of the round.
			while(gameState.isNextPlayerToAct()) {
				gameState = gameState.doAction(Action.CALL);
			}
			
			// Go to next stage.
			if(gameState.getStage()<GameState.SHOWDOWN) {
				gameState = gameState.goToNextStage();
			}
		}
		
		// Deal random hands to each other player in the game and rank them.
		
		final double probOfWin = HandEvaluator.handRank(
				gameState.getC1(), gameState.getC2(), 
				gameState.getTable(), gameState.getNoOfActivePlayers()-1);
		
		final double expectedValue = probOfWin*gameState.getPot();
		
		System.out.println("probOfWin = " + probOfWin + " against " + (gameState.getNoOfActivePlayers()-1) + " opponents");
		System.out.println("expectedValue = " + expectedValue);
		
		return expectedValue;
	}

}



