package mctsbot.nodes;

import java.util.ArrayList;

import mctsbot.actions.Action;
import mctsbot.gamestate.GameState;
import mctsbot.strategies.StrategyConfiguration;

public class ChoiceNode extends Node {
	
	public ChoiceNode(Node parent, GameState gameState, StrategyConfiguration config) {
		super(parent, gameState, config);
	}

	@Override
	public void generateChildren() {
		if(children!=null) return;
		children = new ArrayList<Node>(3);
		
		// The bot raises:
		GameState newGameState = gameState.doAction(Action.RAISE);
		children.add(new OpponentNode(this, newGameState, config));
		
		
		// The bot calls:
		newGameState = gameState.doAction(Action.CALL);
		if(newGameState.isNextPlayerToAct()) {
			children.add(new OpponentNode(this, newGameState, config));
		} else {
			if(gameState.getStage()==GameState.RIVER) {
				children.add(new ShowdownNode(this, newGameState, config));
			} else {
				children.add(new ChanceNode(this, newGameState, config));
			}
		}
		
		
		// The bot folds:
		newGameState = gameState.doAction(Action.FOLD);
		children.add(new BotFoldedNode(this, newGameState, config));
		
	}


}
