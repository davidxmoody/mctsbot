package mctsbot.strategies.backpropagation;

import mctsbot.nodes.Node;

public class AveragingVarBackpropagationStrategy implements
		BackpropagationStrategy {

	public void propagate(Node node, double expectedValue) {
		node.update(expectedValue);
		
		while((node=node.getParent())!=null) {
			node.update(expectedValue);
		}
	}

}
