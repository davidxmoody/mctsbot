package mctsbot.strategies.backpropagation;

import mctsbot.nodes.ChoiceNode;
import mctsbot.nodes.Node;

public class MaxBackpropagationStrategy implements BackpropagationStrategy {
	
	public void propagate(Node node, double expectedValue) {
		node.setExpectedValue(expectedValue);
		node.setVisitCount(node.getVisitCount()+1);
		
		while((node=node.getParent())!=null) {
			
			if(node instanceof ChoiceNode) {
				double maxEV = node.getChildren().get(0).getExpectedValue();
				for(Node child: node.getChildren()) {
					if(child.getExpectedValue()>maxEV) {
						maxEV = child.getExpectedValue();
					}
				}
				node.setExpectedValue(maxEV);
				node.setVisitCount(node.getVisitCount()+1);
				
			} else {
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

}
