package mctsbot.nodes;

import mctsbot.gamestate.GameState;
import mctsbot.strategies.StrategyConfiguration;

public class BotFoldedNode extends LeafNode {

	public BotFoldedNode(Node parent, GameState gameState, StrategyConfiguration config) {
		super(parent, gameState, config);
	}

}
