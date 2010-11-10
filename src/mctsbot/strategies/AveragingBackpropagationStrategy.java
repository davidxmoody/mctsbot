package mctsbot.strategies;

import mctsbot.nodes.Node;

public class AveragingBackpropagationStrategy implements
		BackpropagationStrategy {

	@Override
	public void propagate(Node node, double expectedValue) {
		node.setExpectedValue(expectedValue);
		node.setVisitCount(node.getVisitCount()+1);
		
		while((node=node.getParent())!=null) {
			double numerator = 0.0;
			int denominator = 0;
			
			for(Node child: node.getChildren()) {
				numerator += child.getExpectedValue()*child.getVisitCount();
				denominator += child.getVisitCount();
			}
			node.setExpectedValue(numerator/denominator);			
			node.setVisitCount(node.getVisitCount()+1);
		}

	}

}
