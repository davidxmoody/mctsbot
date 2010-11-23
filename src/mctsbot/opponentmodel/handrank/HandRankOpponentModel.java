package mctsbot.opponentmodel.handrank;

import mctsbot.nodes.ShowdownNode;

public interface HandRankOpponentModel {
	
	public int getRank(ShowdownNode showdownNode, int oppSeat);

}
