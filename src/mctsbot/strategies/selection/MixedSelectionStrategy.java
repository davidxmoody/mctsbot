package mctsbot.strategies.selection;

import mctsbot.nodes.Node;

public class MixedSelectionStrategy implements SelectionStrategy {
	
	private final SelectionStrategy firstSelectionStrategy;
	private final SelectionStrategy secondSelectionStrategy;
	private final int threshold;
	
	public MixedSelectionStrategy(int threshold, 
			SelectionStrategy firstSelectionStrategy, 
			SelectionStrategy secondSelectionStrategy) {
		this.firstSelectionStrategy = firstSelectionStrategy;
		this.secondSelectionStrategy = secondSelectionStrategy;
		this.threshold = threshold;
	}
	
	public Node select(Node node) {
		
		Node returnNode = null;
		
		if(node.getVisitCount()<threshold) {
			returnNode = firstSelectionStrategy.select(node);
		} else {
			returnNode = secondSelectionStrategy.select(node);
		}
		
		Node maxNode = returnNode;
		double maxValue = returnNode.getExpectedValue();
		
		for(Node child: node.getChildren()) {
			if(child.getExpectedValue()>maxValue) {
				maxNode = child;
				maxValue = child.getExpectedValue();
			}
		}
		
		if(maxNode!=returnNode) {
			UCTSelectionStrategy.explorationTally++;
		} else {
			UCTSelectionStrategy.exploitationTally++;
		}
		
		return returnNode;
		
	}

}
