package mctsbot.nodes;

import java.util.ArrayList;


import com.biotools.meerkat.Action;

public class ChoiceNode extends Node {

	public ChoiceNode(Node parent, Action lastAction) {
		super(parent, lastAction);
	}

	@Override
	public void generateChildren() {
		children = new ArrayList<Node>(3);
		

	}


}
