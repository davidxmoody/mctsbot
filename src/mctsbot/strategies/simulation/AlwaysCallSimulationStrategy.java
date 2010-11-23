package mctsbot.strategies.simulation;

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

import com.biotools.meerkat.Deck;
import com.biotools.meerkat.HandEvaluator;

public class AlwaysCallSimulationStrategy implements SimulationStrategy {

	public double simulate(Node node) {
		
		if(node instanceof LeafNode) {
			
			if(node instanceof BotFoldedNode) {
				return node.getParent().getGameState().getBotMoney();
				
			} else if(node instanceof AllOpponentsFoldedNode) {
				return node.getGameState().getBotMoney()+node.getGameState().getPot();
				
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
		
		//System.out.println("simulate about to start");
		
		
		
		Deck deck = new Deck();
		deck.extractCard(gameState.getC1());
		deck.extractCard(gameState.getC2());
		deck.extractHand(gameState.getTable());
		
		// Simulate until showdown.
		while(gameState.getStage()<=GameState.RIVER) {
			
			//gameState.printDetails();
			
			// Make all players call until the end of the round.
			while(gameState.isNextPlayerToAct()) {
				gameState = gameState.doAction(Action.CALL);
			}
			
			switch (gameState.getStage()) {
			case GameState.PREFLOP:
				gameState = gameState.dealCard(deck.extractRandomCard());
				gameState = gameState.dealCard(deck.extractRandomCard());
				gameState = gameState.dealCard(deck.extractRandomCard());
				break;
			case GameState.FLOP:
			case GameState.TURN:
				gameState = gameState.dealCard(deck.extractRandomCard());
				break;
			}
			
			// Go to next stage.
			gameState = gameState.goToNextStage();
			
		}
		
		// Calculate the hand rank of the bot.
		
		//System.out.println("rankHand about to be called on: ");
		//gameState.printDetails();

		final int botHandRank = HandEvaluator.rankHand(
				gameState.getC1(), gameState.getC2(), gameState.getTable());
		
		//System.out.println("rankHand successful, botHandRank = " + botHandRank);
		
		// Deal random hands to each other player in the game and rank them.
		
		final int noOfOpponents = gameState.getNoOfActivePlayers()-1;

		int maxOpponentHandRank = 0;
		
		for(int i=0; i<noOfOpponents; i++) {
			
			//System.out.println("rankHand about to be called, for opponent, on: ");
			
			final int opponentHandRank = HandEvaluator.rankHand(
					deck.extractRandomCard(), 
					deck.extractRandomCard(), 
					gameState.getTable());
			
			//System.out.println("rankHand successful, opponentHandRank = " + opponentHandRank);
			
			if(opponentHandRank>maxOpponentHandRank) maxOpponentHandRank = opponentHandRank;
		}
		
		//double expectedValue = gameState.getAmountInPot(gameState.getBotSeat());
		double expectedValue = gameState.getBotMoney();
		
		if(botHandRank>=maxOpponentHandRank) {
			expectedValue += gameState.getPot();
		}
		
		
		
		//System.out.println("expectedValue = " + expectedValue);
		
		
		return expectedValue;
	}

}



