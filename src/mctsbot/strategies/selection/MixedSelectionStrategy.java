package mctsbot.strategies.selection;

import mctsbot.nodes.Node;

public class MixedSelectionStrategy implements SelectionStrategy {
	
	private static final UCTSelectionStrategy UCTSS = new UCTSelectionStrategy();
	private static final RandomSelectionStrategy RandomSS = new RandomSelectionStrategy();

	public Node select(Node node) {
		
		Node returnNode = null;
		
		if(node.getVisitCount()>24) {
			returnNode = UCTSS.select(node);
		} else {
			returnNode = RandomSS.select(node);
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
