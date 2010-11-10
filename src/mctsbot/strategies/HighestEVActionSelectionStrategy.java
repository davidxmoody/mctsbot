package mctsbot.strategies;

import java.util.List;

import mctsbot.actions.Action;
import mctsbot.nodes.Node;
import mctsbot.nodes.RootNode;

public class HighestEVActionSelectionStrategy implements
		ActionSelectionStrategy {

	@Override
	public Action select(RootNode root) {
		final List<Node> children = root.getChildren();
		
		double currentMaxEV = children.get(0).getExpectedValue();
		Node currentBestChoice = children.get(0);
		for(Node child: children) {
			if(child.getExpectedValue()>currentMaxEV) {
				currentMaxEV = child.getExpectedValue();
				currentBestChoice = child;
			}
		}
		return currentBestChoice.getGameState().getLastAction();
	}

}
