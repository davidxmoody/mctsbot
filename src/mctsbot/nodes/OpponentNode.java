package mctsbot.nodes;

import java.util.ArrayList;

import mctsbot.gamestate.GameState;
import mctsbot.strategies.StrategyConfiguration;

public class OpponentNode extends Node {

	public OpponentNode(Node parent, GameState gameState, StrategyConfiguration config) {
		super(parent, gameState, config);
	}

	@Override
	public void generateChildren() {
		children = new ArrayList<Node>(3);

	}


}
