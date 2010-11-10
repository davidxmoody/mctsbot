package mctsbot.nodes;

import java.util.ArrayList;

import mctsbot.actions.Action;
import mctsbot.gamestate.GameState;
import mctsbot.strategies.StrategyConfiguration;

public class OpponentNode extends Node {

	public OpponentNode(Node parent, GameState gameState, StrategyConfiguration config) {
		super(parent, gameState, config);
	}

	@Override
	public void generateChildren() {
		if(children!=null) return;
		children = new ArrayList<Node>(3);
		
		// The opponent raises:
		GameState newGameState = gameState.doAction(Action.RAISE);
		if(newGameState.isBotNextPlayerToAct()) {
			children.add(new ChoiceNode(this, newGameState, config));
		} else {
			children.add(new OpponentNode(this, newGameState, config));
		}
		
		
		// The opponent calls:
		newGameState = gameState.doAction(Action.CALL);
		if(newGameState.isNextPlayerToAct()) {
			if(newGameState.isBotNextPlayerToAct()) {
				children.add(new ChoiceNode(this, newGameState, config));
			} else {
				children.add(new OpponentNode(this, newGameState, config));
			}
		} else {
			if(gameState.getStage()==GameState.RIVER) {
				children.add(new ShowdownNode(this, newGameState, config));
			} else {
				children.add(new ChanceNode(this, newGameState, config));
			}
		}
		
		
		// The opponent folds:
		newGameState = gameState.doAction(Action.FOLD);
		if(newGameState.isNextPlayerToAct()) {
			if(newGameState.isBotNextPlayerToAct()) {
				children.add(new ChoiceNode(this, newGameState, config));
			} else {
				children.add(new OpponentNode(this, newGameState, config));
			}
		} else {
			if(newGameState.getNoOfActivePlayers()==1) {
				children.add(new AllOpponentsFoldedNode(this, newGameState, config));
			} else {
				if(gameState.getStage()==GameState.RIVER) {
					children.add(new ShowdownNode(this, newGameState, config));
				} else {
					children.add(new ChanceNode(this, newGameState, config));
				}
			}

		}

	}


}
