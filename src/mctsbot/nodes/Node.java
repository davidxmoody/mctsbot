package mctsbot.nodes;

import java.util.List;

import mctsbot.gamestate.GameState;

import com.biotools.meerkat.Action;

public abstract class Node {
	protected double expectedValue = 0.0;
	protected int visitCount = 0;
	protected final Action lastAction;
	protected final Node parent;
	protected List<Node> children;
	protected GameState gameState;
	
	public Node(Node parent, Action lastAction) {
		this.parent = parent;
		this.lastAction = lastAction;
		this.children = null;
	}

	public void setExpectedValue(long expectedValue) {
		this.expectedValue = expectedValue;
	}

	public double getExpectedValue() {
		return expectedValue;
	}

	public void setVisitCount(int visitCount) {
		this.visitCount = visitCount;
	}

	public int getVisitCount() {
		return visitCount;
	}

	public Action getLastAction() {
		return lastAction;
	}

	public Node getParent() {
		return parent;
	}

	public List<Node> getChildren() {
		return children;
	}
	
	/*public boolean isLeaf() {
		if(children == null) generateChildren();
		return children.size()==0;
	}*/
	
	public abstract void generateChildren();
	
	

	
	
}





