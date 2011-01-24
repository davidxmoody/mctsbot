package mctsbot.strategies.simulation;

import java.util.Arrays;

import mctsbot.nodes.Node;

public class MedianSimulationStrategy implements SimulationStrategy {
	
	private final SimulationStrategy simulationStrategy;
	private final int numberOfSimulations;
	
	public MedianSimulationStrategy(
			SimulationStrategy simulationStrategy, 
			int numberOfSimulations) {
		this.simulationStrategy = simulationStrategy;
		this.numberOfSimulations = numberOfSimulations;
		if(numberOfSimulations<=0) 
			throw new RuntimeException("numberOfSimulations must be greater than or equal to 1.");
	}

	public double simulate(Node node) {
		double[] results = new double[numberOfSimulations];
		for(int i=0; i<numberOfSimulations; i++) 
			results[i] = simulationStrategy.simulate(node);
		return median(results);
	}

	private double median(double[] numbers) {
		Arrays.sort(numbers);
		if((numbers.length%2)==0) {
			return (numbers[numbers.length/2]+numbers[(numbers.length/2)-1])/2;
		} else {
			return numbers[numbers.length/2];
		}
	}

}
