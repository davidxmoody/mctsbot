package mctsbot.nodes;

import mctsbot.actions.Action;
import mctsbot.gamestate.GameState;
import mctsbot.strategies.StrategyConfiguration;

public class ChoiceNode extends PlayerNode {
	
	public ChoiceNode(Node parent, GameState gameState, StrategyConfiguration config) {
		super(parent, gameState, config);
	}
	
	@Override
	public Node createRaiseNode() {
		final GameState newGameState = gameState.doAction(Action.RAISE);
		if(newGameState.isNextPlayerToAct()) {
			return new OpponentNode(this, newGameState, config);
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
			return new OpponentNode(this, newGameState, config);
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
		return new BotFoldedNode(this, newGameState, config);
	}


}



