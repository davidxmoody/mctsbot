package mctsbot.strategies;

import mctsbot.actions.Action;
import mctsbot.nodes.RootNode;

public interface ActionSelectionStrategy {
	
	public Action select(RootNode root);

}
