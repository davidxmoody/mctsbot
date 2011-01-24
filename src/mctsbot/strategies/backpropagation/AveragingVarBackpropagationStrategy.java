package mctsbot.strategies.backpropagation;

import java.util.ArrayList;

import mctsbot.nodes.Node;

public class AveragingVarBackpropagationStrategy implements
		BackpropagationStrategy {

	public void propagate(Node node, double expectedValue) {
		node.setExpectedValue(expectedValue);
		node.setVisitCount(node.getVisitCount()+1);
		node.simulationResults.add(expectedValue);
		node.variance = 0.0;
		
		while((node=node.getParent())!=null) {
			
			node.simulationResults.add(expectedValue);
			node.setVisitCount(node.getVisitCount()+1);
			
			final double mean = calculateMean(node.simulationResults);
			node.setExpectedValue(mean);
			node.variance = calculateVariance(node.simulationResults, mean);
			
			
			
		}

	}
	
	private double calculateVariance(ArrayList<Double> numbers, double mean) {
		double total = 0.0;
		for(double x: numbers) 
			total += (x-mean)*(x-mean);
		return total/numbers.size();
	}
	
	private double calculateMean(ArrayList<Double> numbers) {
		double total = 0.0;
		for(double x: numbers) 
			total += x;
		return total/numbers.size();
	}

}
