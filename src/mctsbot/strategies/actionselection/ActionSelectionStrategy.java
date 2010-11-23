package mctsbot.strategies.actionselection;

import mctsbot.actions.Action;
import mctsbot.nodes.RootNode;

public interface ActionSelectionStrategy {
	
	public Action select(RootNode root);

}
