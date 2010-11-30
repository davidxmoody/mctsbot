package mctsbot.opponentmodel;

import mctsbot.gamestate.GameState;
import mctsbot.nodes.ShowdownNode;

import com.biotools.meerkat.Deck;
import com.biotools.meerkat.HandEvaluator;

public class RandomHandRankOpponentModel implements HandRankOpponentModel {

	public int getRank(ShowdownNode showdownNode, int oppSeat) {
		
		final GameState gameState = showdownNode.getGameState();
		final Deck deck = gameState.createDeck();
		
		return HandEvaluator.rankHand(
					deck.extractRandomCard(), 
					deck.extractRandomCard(), 
					gameState.getTable());
	}

}
