package mctsbot.strategies.simulation;

import mctsbot.nodes.Node;

//TODO: remove this
public class MixedSimulationStrategy implements SimulationStrategy {

	private final SimulationStrategy firstSimStrategy;
	private final SimulationStrategy secondSimStrategy;
	private final int threshold;
	
	public MixedSimulationStrategy(int threshold, 
			SimulationStrategy firstSimStrategy, 
			SimulationStrategy secondSimStrategy) {
		this.firstSimStrategy = firstSimStrategy;
		this.secondSimStrategy = secondSimStrategy;
		this.threshold = threshold;
	}

	public double simulate(Node node) {
		if(node.getVisitCount()<threshold) {
			return firstSimStrategy.simulate(node);
		} else {
			return secondSimStrategy.simulate(node);
		}
	}


}
