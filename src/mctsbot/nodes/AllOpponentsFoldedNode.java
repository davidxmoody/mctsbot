package mctsbot.nodes;

import mctsbot.Config;
import mctsbot.gamestate.GameState;

public class AllOpponentsFoldedNode extends LeafNode {

	public AllOpponentsFoldedNode(Node parent, GameState gameState, Config config) {
		super(parent, gameState, config);
	}

}
