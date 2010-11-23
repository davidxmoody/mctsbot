package mctsbot.nodes;

import mctsbot.Config;
import mctsbot.actions.Action;
import mctsbot.gamestate.GameState;

public class OpponentNode extends PlayerNode {

	public OpponentNode(Node parent, GameState gameState, Config config) {
		super(parent, gameState, config);
	}

	@Override
	public Node createRaiseNode() {
		final GameState newGameState = gameState.doAction(Action.RAISE);
		if(newGameState.isNextPlayerToAct()) {
			if(newGameState.isBotNextPlayerToAct()) {
				return new ChoiceNode(this, newGameState, config);
			} else {
				return new OpponentNode(this, newGameState, config);
			}
		} else {
			if(gameState.getStage()==GameState.RIVER) {
				return new ShowdownNode(this, newGameState, config);
			} else {
				return new ChanceNode(this, newGameState, config);
			}
		}
	}
	
	@Override
	public Node createCallNode() {
		final GameState newGameState = gameState.doAction(Action.CALL);
		if(newGameState.isNextPlayerToAct()) {
			if(newGameState.isBotNextPlayerToAct()) {
				return new ChoiceNode(this, newGameState, config);
			} else {
				return new OpponentNode(this, newGameState, config);
			}
		} else {
			if(gameState.getStage()==GameState.RIVER) {
				return new ShowdownNode(this, newGameState, config);
			} else {
				return new ChanceNode(this, newGameState, config);
			}
		}
	}
	
	@Override
	public Node createFoldNode() {
		final GameState newGameState = gameState.doAction(Action.FOLD);
		if(newGameState.isNextPlayerToAct()) {
			if(newGameState.isBotNextPlayerToAct()) {
				return new ChoiceNode(this, newGameState, config);
			} else {
				return new OpponentNode(this, newGameState, config);
			}
		} else {
			if(newGameState.getNoOfActivePlayers()==1) {
				return new AllOpponentsFoldedNode(this, newGameState, config);
			} else {
				if(gameState.getStage()==GameState.RIVER) {
					return new ShowdownNode(this, newGameState, config);
				} else {
					return new ChanceNode(this, newGameState, config);
				}
			}

		}
	}


}
