package mctsbot.nodes;

import java.util.LinkedList;

import mctsbot.gamestate.GameState;
import mctsbot.strategies.StrategyConfiguration;

public class ChanceNode extends Node {

	public ChanceNode(Node parent, GameState gameState, StrategyConfiguration config) {
		super(parent, gameState, config);
	}

	@Override
	public void generateChildren() {
		if(children!=null) return;
		children = new LinkedList<Node>();
	}
	
	public Node generateChild() {
		
		GameState newGameState = null;
		
		if(gameState.getStage()==GameState.PREFLOP) {
			newGameState = 
				gameState.dealRandomCard().dealRandomCard().dealRandomCard().goToNextStage();
		} else if(gameState.getStage()==GameState.FLOP || 
				  gameState.getStage()==GameState.TURN){
			newGameState = gameState.dealRandomCard().goToNextStage();
		} else {
			throw new RuntimeException();
		}
		
		Node newChild = null;
		
		if(newGameState.isBotNextPlayerToAct()) {
			newChild = new ChoiceNode(this, newGameState, config);
		} else {
			newChild = new OpponentNode(this, newGameState, config);
		}
		
		if(children==null) children = new LinkedList<Node>();
		children.add(newChild);
		
		return newChild;
	}


}
