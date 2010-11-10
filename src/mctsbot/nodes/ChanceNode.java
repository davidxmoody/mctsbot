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


}
