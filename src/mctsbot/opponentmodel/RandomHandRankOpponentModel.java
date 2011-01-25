package mctsbot.opponentmodel;

import java.util.Random;

import mctsbot.gamestate.Player;
import mctsbot.nodes.ShowdownNode;

public class RandomHandRankOpponentModel implements HandRankOpponentModel {

	private static final Random random = new Random();

	public double probOfBeatingOpponent(ShowdownNode showdownNode, Player opponent, int botHandRank) {
		return random.nextDouble();
	}

}
