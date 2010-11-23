package mctsbot.strategies;

import mctsbot.nodes.Node;

public class AveragingSimulationStrategy implements SimulationStrategy {
	
	private final SimulationStrategy simulationStrategy;
	private final int numberOfSimulations;
	
	public AveragingSimulationStrategy(
			SimulationStrategy simulationStrategy, 
			int numberOfSimulations) {
		this.simulationStrategy = simulationStrategy;
		this.numberOfSimulations = numberOfSimulations;
		if(numberOfSimulations<=0) 
			throw new RuntimeException("numberOfSimulations must be greater than or equal to 1.");
	}

	public double simulate(Node node) {
		double numerator = 0.0;
		final int denominator = numberOfSimulations;
		for(int i=0; i<numberOfSimulations; i++) 
			numerator += simulationStrategy.simulate(node);
		return numerator/denominator;
	}

}
