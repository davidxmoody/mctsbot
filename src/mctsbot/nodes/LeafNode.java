package mctsbot.nodes;

import mctsbot.gamestate.GameState;
import mctsbot.strategies.StrategyConfiguration;

public class LeafNode extends Node {

	public LeafNode(Node parent, GameState gameState, StrategyConfiguration config) {
		super(parent, gameState, config);
	}

	@Override
	public void generateChildren() {
		// Do nothing.
	}
	
	@Override
	public Node select() {
		return this;
	}

}
