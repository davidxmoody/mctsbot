package mctsbot.nodes;

import java.util.List;

import mctsbot.gamestate.GameState;
import mctsbot.strategies.BackpropagationStrategy;
import mctsbot.strategies.SelectionStrategy;
import mctsbot.strategies.SimulationStrategy;

public abstract class Node {
	
	protected double expectedValue;
	protected int visitCount;
	protected final Node parent;
	protected GameState gameState;
	protected List<Node> children = null;
	
	protected SelectionStrategy selectionStrategy;
	protected BackpropagationStrategy backpropagationStrategy;
	protected SimulationStrategy simulationStrategy;
	
	public Node(Node parent, GameState gameState) {
		this.parent = parent;
		this.gameState = gameState;
		
		this.expectedValue = 0.0;
		this.visitCount = 0;
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

	public Node getParent() {
		return parent;
	}

	public List<Node> getChildren() {
		return children;
	}
	
	public GameState getGameState() {
		return gameState;
	}

	public abstract void generateChildren();
	
	public Node select() {
		return null;
	}
	
	public void backpropagate(double expectedValue) {
		
	}
	
	public double simulate() {
		return 0.0;
	}
	
	

	
	
}





