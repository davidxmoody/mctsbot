package mctsbot.opponentmodel;

import mctsbot.gamestate.Player;
import mctsbot.nodes.ShowdownNode;

import com.biotools.meerkat.Deck;
import com.biotools.meerkat.HandEvaluator;

public class RandomHandRankOpponentModel implements HandRankOpponentModel {

	public double probOfBeatingOpponent(ShowdownNode showdownNode, 
			Player opponent, int botHandRank) {
		
		final Deck deck = showdownNode.getGameState().createDeck();
		final int hr = HandEvaluator.rankHand(deck.extractRandomCard(), 
				deck.extractRandomCard(), showdownNode.getGameState().getTable());
		// This won't work when there is more than one opponent. 
		return hr>botHandRank?0:1;
	}

}
