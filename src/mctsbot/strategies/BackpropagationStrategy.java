package mctsbot.strategies;

import mctsbot.nodes.Node;

public interface BackpropagationStrategy {
	
	public Node propagate(Node node, double expectedValue);

}
