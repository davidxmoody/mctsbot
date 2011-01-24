package mctsbot.strategies.actionselection;

import java.util.List;

import mctsbot.actions.Action;
import mctsbot.nodes.Node;
import mctsbot.nodes.RootNode;

public class HighestEVActionSelectionStrategy implements
		ActionSelectionStrategy {

	public Action select(RootNode root) {
		
		/*System.out.println("RootNode" + 
				" has EV = " + root.getExpectedValue() + 
				" and VC = " + root.getVisitCount());*/

		
		final List<Node> children = root.getChildren();
		
		double currentMaxEV = children.get(0).getExpectedValue();
		Node currentBestChoice = children.get(0);
		for(Node child: children) {
			if(child.getExpectedValue()>currentMaxEV) {
				currentMaxEV = child.getExpectedValue();
				currentBestChoice = child;
			}
			
			System.out.println(child.getGameState().getLastAction().getDescription() + 
					" has EV = " + child.getExpectedValue() + 
					" and VC = " + child.getVisitCount());
			
			
		}
		
		final Action action = currentBestChoice.getGameState().getLastAction();
		
		System.out.println("Chosen to perform " + action.getDescription());
		
		return action;
	}

}
