package mctsbot.nodes;

import java.util.List;

import mctsbot.gamestate.GameState;
import mctsbot.strategies.StrategyConfiguration;

public abstract class Node {
	
	protected double expectedValue;
	protected int visitCount;
	protected final Node parent;
	protected GameState gameState;
	protected List<Node> children = null;
	
	protected final StrategyConfiguration config;
	
	public Node(Node parent, GameState gameState, StrategyConfiguration config) {
		this.parent = parent;
		this.gameState = gameState;
		this.config = config;
		
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
	
	public boolean isExpanded() {
		return children!=null;
	}
	
	public Node select() {
		return config.getSelectionStrategy().select(this);
	}
	
	public void backpropagate(double expectedValue) {
		config.getBackpropagationStrategy().propagate(this, expectedValue);
	}
	
	public double simulate() {
		return config.getSimulationStrategy().simulate(this);
	}
	
	public abstract void generateChildren();

	
	
}





