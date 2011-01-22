package mctsbot.opponentmodel;

import mctsbot.gamestate.GameState;
import mctsbot.nodes.ShowdownNode;

import com.biotools.meerkat.Deck;
import com.biotools.meerkat.HandEvaluator;

public class RandomHandRankOpponentModel implements HandRankOpponentModel {

	public boolean beatsOpponent(ShowdownNode showdownNode, int oppSeat, int botHandRank) {
		
		final GameState gameState = showdownNode.getGameState();
		final Deck deck = gameState.createDeck();
		
		return HandEvaluator.rankHand(
					deck.extractRandomCard(), 
					deck.extractRandomCard(), 
					gameState.getTable());
	}

}
