package mctsbot.strategies;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import mctsbot.nodes.ChoiceNode;
import mctsbot.nodes.Node;
import mctsbot.nodes.OpponentNode;

public class UCTSelectionStrategy implements SelectionStrategy {
	
	private static final double C = 2;
	
	private static final Random random = new Random();
	
	//TODO: remove dependency on other selection strategy and just copy code to this class.
	private static final RandomSelectionStrategy randomSelectionStrategy = 
		new RandomSelectionStrategy();

	@Override
	public Node select(Node node) {
		// UTC won't work on chance nodes or leaf nodes.
		if(!((node instanceof OpponentNode) || (node instanceof ChoiceNode))) {
			return randomSelectionStrategy.select(node);
		}
		
		// UTC will only work if all nodes have been explored once 
		// so first check to see if this is the case, if it is not 
		// then choose one of the nodes which have not been explored yet.
		
		boolean isUnvisitedChildren = false;
		for(Node child: node.getChildren()) {
			if(child.getVisitCount()<=0) {
				isUnvisitedChildren = true;
				break;
			}
		} 
		
		// If there are unvisited children then randomly choose one and return it.
		if(isUnvisitedChildren) {
			LinkedList<Node> unvisitedChildren = new LinkedList<Node>();
			for(Node child: node.getChildren()) {
				if(child.getVisitCount()<=0) {
					unvisitedChildren.add(child);
				}
			}
			return unvisitedChildren.get(random.nextInt(unvisitedChildren.size()));
		}
					
		
		// If all children have been visited then go to next step.
		
		return argmaxFormula(node.getChildren(), node);
	}
	
	
	private double formula(Node child, Node parent) {
		return child.getExpectedValue() + 
			C*Math.sqrt(Math.log(parent.getVisitCount())/child.getVisitCount());
	}
	
	
	private Node argmaxFormula(List<Node> children, Node parent) {
		double maxValue = formula(children.get(0), parent);
		Node maxNode = children.get(0);
		
		for(int i=1; i<children.size(); i++) {
			final double value = formula(children.get(i), parent);
			if(value>maxValue) {
				maxValue = value;
				maxNode = children.get(i);
			}
		}
		
		return maxNode;
	}

}



