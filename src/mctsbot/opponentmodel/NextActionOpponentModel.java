package mctsbot.opponentmodel;

import mctsbot.nodes.OpponentNode;

public interface NextActionOpponentModel {
	
	public double[] getActionProbabilities(OpponentNode opponentNode);

}
