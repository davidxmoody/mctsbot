package mctsbot.nodes;

import java.util.List;

import mctsbot.Config;
import mctsbot.gamestate.GameState;

public abstract class Node {
	
	private double expectedValue;
	private int visitCount;
	private final Node parent;
	
	protected final GameState gameState;
	protected List<Node> children;
	
	protected final Config config;
	
	public Node(Node parent, GameState gameState, Config config) {
		this.parent = parent;
		this.gameState = gameState;
		this.config = config;
		
		this.expectedValue = 0.0;
		this.visitCount = 0;
		
		this.children = null;
	}

	public void setExpectedValue(double expectedValue) {
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
	
	public Config getConfig() {
		return config;
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
	
	public void printDetails() {
		System.out.println("Node type = " + this.getClass().getSimpleName());
		System.out.println("Last action = " + gameState.getLastAction().getDescription());
		System.out.println("EV = " + expectedValue);
		System.out.println("VC = " + visitCount);
		System.out.println();
	}
	
	public void printChildrensDetails() {
		if(children==null) System.out.println("This node has no children.");
		else if(children.size()>10) System.out.println("This node has too many children to print.");
		else for(Node child: children) 
			child.printDetails();
		
		
	}
	
}





