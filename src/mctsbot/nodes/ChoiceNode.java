package mctsbot.nodes;

import java.util.ArrayList;

import mctsbot.actions.ActionFactory;
import mctsbot.gamestate.GameState;

public class ChoiceNode extends Node {
	public ChoiceNode(Node parent, GameState gameState) {
		super(parent, gameState, null);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void generateChildren() {
		children = new ArrayList<Node>(3);
		
		// The bot raises:
		children.add(new OpponentNode(this,gameState.doAction(ActionFactory.getAction(ActionFactory.RAISE_ACTION, 0))));
		
		// The bot calls:
		//TODO: work out which type of node needs to be added.
		
		
		// The bot folds:
		children.add(new LeafNode(this,gameState.doAction(ActionFactory.getAction(ActionFactory.FOLD_ACTION, 0))));
		
	}


}
