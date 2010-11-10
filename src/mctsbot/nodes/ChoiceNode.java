package mctsbot.nodes;

import java.util.ArrayList;

import mctsbot.actions.ActionFactory;
import mctsbot.gamestate.GameState;
import mctsbot.strategies.StrategyConfiguration;

public class ChoiceNode extends Node {
	public ChoiceNode(Node parent, GameState gameState, StrategyConfiguration config) {
		super(parent, gameState, config);
	}

	@Override
	public void generateChildren() {
		children = new ArrayList<Node>(3);
		
		// The bot raises:
		children.add(new OpponentNode(this,gameState.doAction(ActionFactory.getAction(ActionFactory.RAISE_ACTION, 0)), config));
		
		// The bot calls:
		//TODO: work out which type of node needs to be added.
		
		
		// The bot folds:
		children.add(new LeafNode(this,gameState.doAction(ActionFactory.getAction(ActionFactory.FOLD_ACTION, 0)), config));
		
	}


}
