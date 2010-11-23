package mctsbot.nodes;

import mctsbot.Config;
import mctsbot.gamestate.GameState;

public class LeafNode extends Node {

	public LeafNode(Node parent, GameState gameState, Config config) {
		super(parent, gameState, config);
	}

	@Override
	public void generateChildren() {
		//System.out.println("generate on a leaf node is being called");
		// Do nothing.
	}
	
	@Override
	public Node select() {
		return this;
	}

}
