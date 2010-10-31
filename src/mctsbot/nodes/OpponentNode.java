package mctsbot.nodes;

import java.util.ArrayList;


import com.biotools.meerkat.Action;

public class OpponentNode extends Node {

	public OpponentNode(Node parent, Action lastAction) {
		super(parent, lastAction);
	}

	@Override
	public void generateChildren() {
		children = new ArrayList<Node>(3);

	}

}
