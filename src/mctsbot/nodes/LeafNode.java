package mctsbot.nodes;

import com.biotools.meerkat.Action;

public class LeafNode extends Node {

	public LeafNode(Node parent, Action lastAction) {
		super(parent, lastAction);
	}

	@Override
	public void generateChildren() { }

}
