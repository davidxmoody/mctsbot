package mctsbot.nodes;

import java.util.ArrayList;

import mctsbot.actions.Action;
import mctsbot.actions.ActionFactory;

public class ChoiceNode extends Node {

	public ChoiceNode(Node parent, Action lastAction) {
		super(parent, lastAction);
	}

	@Override
	public void generateChildren() {
		children = new ArrayList<Node>(3);
		
		// The bot raises:
		
		// The bot calls:
		
		// The bot folds:
		children.add(new LeafNode(this, ActionFactory.getAction(ActionFactory.FOLD_ACTION, 0)));
		
	}


}
