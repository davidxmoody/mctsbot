package mctsbot.nodes;

import java.util.ArrayList;

import mctsbot.actions.Action;
import mctsbot.gamestate.GameState;

public class OpponentNode extends Node {

	public OpponentNode(Node parent, GameState gameState) {
		super(parent, gameState, null);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void generateChildren() {
		children = new ArrayList<Node>(3);

	}


}
