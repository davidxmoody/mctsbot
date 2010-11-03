package mctsbot.nodes;

import java.util.List;

import mctsbot.actions.Action;
import mctsbot.gamestate.GameState;
import mctsbot.strategies.BackpropagationStrategy;
import mctsbot.strategies.SelectionStrategy;
import mctsbot.strategies.SimulationStrategy;

public abstract class Node {
	
	protected double expectedValue = 0.0;
	protected int visitCount = 0;
	protected final Action lastAction;
	protected final Node parent;
	protected List<Node> children;
	protected GameState gameState;
	
	protected double probability;
	
	protected SelectionStrategy selectionStrategy;
	protected BackpropagationStrategy backpropagationStrategy;
	protected SimulationStrategy simulationStrategy;
	
	public Node(Node parent, Action lastAction, double probability) {
		this.parent = parent;
		this.lastAction = lastAction;
		this.children = null;
		this.probability = probability;
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
	
	public void setProbability(double probability) {
		this.probability= probability ;
	}
	
	public double getProbability() {
		return probability;
	}
	
	public abstract void generateChildren();
	
	public Node select() {
		return null;
	}
	
	public void backpropagate() {
		
	}
	
	

	
	
}





