package mctsbot.strategies.selection;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import mctsbot.nodes.ChoiceNode;
import mctsbot.nodes.Node;

public class UCTSelectionStrategy implements SelectionStrategy {
	
	public static double explorationTally = 0;
	public static double exploitationTally = 0;
	
	//TODO: find a good value for C.
	public static final double C = 1.5;
	
	private static final Random random = new Random();
	
	private static final RandomSelectionStrategy randomSelectionStrategy = 
		new RandomSelectionStrategy();

	
	
	
	public Node select(Node node) {
		// UTC won't work on chance nodes or leaf nodes.
		if(!(node instanceof ChoiceNode)) {
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
		
		final Node returnNode = argmaxFormula(node.getChildren(), node);
		
		
		
		Node maxNode = returnNode;
		double maxValue = returnNode.getExpectedValue();
		
		for(Node child: node.getChildren()) {
			if(child.getExpectedValue()>maxValue) {
				maxNode = child;
				maxValue = child.getExpectedValue();
			}
		}
		
		if(maxNode!=returnNode) {
			explorationTally++;
		} else {
			exploitationTally++;
		}
		
		return returnNode;
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
			} else if(value==maxValue) {
				
				if(random.nextInt(2)==0) {
					maxNode = children.get(i);
					maxValue = children.get(i).getExpectedValue();
				}
			}
		}
		
		return maxNode;
	}


}



