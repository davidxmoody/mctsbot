package mctsbot.opponentmodel;

import mctsbot.nodes.OpponentNode;

public class StaticDistributionNextActionOpponentModel implements
		NextActionOpponentModel {
	
	private static double[] probs = {0.333333,0.333333,0.333334};

	public double[] getActionProbabilities(OpponentNode opponentNode) {
		return probs;
	}

}
