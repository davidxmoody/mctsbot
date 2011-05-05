package mctsbot.nodes;

import java.util.Random;

import mctsbot.Config;
import mctsbot.actions.Action;
import mctsbot.gamestate.GameState;

public class OpponentNode extends PlayerNode {
	
	private static final Random random = new Random();
	
	private double[] distribution = null;

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
		if(gameState.getMaxBetThisRound()==0 || gameState.getMaxBetThisRound()==
				gameState.getNextPlayerToAct().getAmountInPotInCurrentRound()) {
			try {
				return children.get(1);
			} catch(Exception e) {
				return createCallNode();
			}
		}
		
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


//	public void setDistribution(double[] distribution) {
//		this.distribution = distribution;
//	}
//
//
//	public double[] getDistribution() {
//		return distribution;
//	}
	
	public Node selectChild() {
		if(distribution==null) distribution = config.getNextActionOpponentModel().getActionProbabilities(this);
		final double randomDouble = random.nextDouble();
		if(randomDouble<distribution[0]) {
			return children.get(0);
		} else if(randomDouble<distribution[0]+distribution[1]) {
			return children.get(1);
		} else {
			return children.get(2);
		}
	}


}
