package mctsbot.nodes;

import mctsbot.Config;
import mctsbot.actions.Action;
import mctsbot.gamestate.GameState;

public class ChoiceNode extends PlayerNode {
	
	public ChoiceNode(Node parent, GameState gameState, Config config) {
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
		if(gameState.getMaxBetThisRound()==0 || gameState.getMaxBetThisRound()==
				gameState.getNextPlayerToAct().getAmountInPotInCurrentRound()) {
			try {
				return children.get(1);
			} catch(Exception e) {
				return createCallNode();
			}
		}
		
		final GameState newGameState = gameState.doAction(Action.FOLD);
		return new BotFoldedNode(this, newGameState, config);
	}


}



