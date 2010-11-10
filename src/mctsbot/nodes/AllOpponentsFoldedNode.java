package mctsbot.nodes;

import mctsbot.gamestate.GameState;
import mctsbot.strategies.StrategyConfiguration;

public class AllOpponentsFoldedNode extends LeafNode {

	public AllOpponentsFoldedNode(Node parent, GameState gameState, StrategyConfiguration config) {
		super(parent, gameState, config);
	}

}
