package mctsbot.strategies.backpropagation;

import mctsbot.nodes.Node;

public interface BackpropagationStrategy {
	
	public void propagate(Node node, double expectedValue);

}
