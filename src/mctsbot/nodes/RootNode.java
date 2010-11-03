package mctsbot.nodes;

import mctsbot.gamestate.GameState;


public class RootNode extends Node {

	public RootNode(GameState gameState) {
		super(null, gameState);
		
		
		
	}

	@Override
	public void generateChildren() {
		// TODO Auto-generated method stub

	}
	
	public Node selectRecursively() {
		return null;
	}

}
