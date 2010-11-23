package mctsbot.strategies.simulation;

import mctsbot.actions.Action;

public class DynamicDistributionSimulationStrategy extends
		StaticDistributionSimulationStrategy {
	
	public static int[] foldTally = {0, 10, 2, 2, 3};
	public static int[] callTally = {0, 10, 10, 10, 10};
	public static int[] raiseTally = {0, 2, 10, 10, 10};

	@Override
	protected double getRaiseProb(int stage, boolean canCheck) {
		if(stage<1 || stage>4) throw new RuntimeException(
				"Invalid stage passed to a get prob method: " + stage);
		
		return raiseTally[stage]/
			(raiseTally[stage]+callTally[stage]+(canCheck?0:foldTally[stage]));
	}
	
	@Override
	protected double getCallProb(int stage, boolean canCheck) {
		if(stage<1 || stage>4) throw new RuntimeException(
				"Invalid stage passed to a get prob method: " + stage);
		
		return callTally[stage]/
			(raiseTally[stage]+callTally[stage]+(canCheck?0:foldTally[stage]));
	}
	
	@Override
	protected double getFoldProb(int stage, boolean canCheck) {
		if(stage<1 || stage>4) throw new RuntimeException(
				"Invalid stage passed to a get prob method: " + stage);
		
		return (canCheck?0.0:(foldTally[stage]/
			(raiseTally[stage]+callTally[stage]+foldTally[stage])));
	}
	
	
	
	public static void actionHappened(int stage, int action) {
		if(stage<1 || stage>4) return;
		
		switch (action) {
		case Action.RAISE:
			raiseTally[stage]++;
			break;
		case Action.CALL:
			callTally[stage]++;
			break;
		case Action.FOLD:
			foldTally[stage]++;
			break;

		default:
			break;
		}
	}
	
	
	
}
