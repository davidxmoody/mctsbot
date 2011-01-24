package mctsbot.nodes;

import java.util.ArrayList;
import java.util.List;

import mctsbot.Config;
import mctsbot.gamestate.GameState;

public abstract class Node {
	
	private double expectedValue;
	//TODO: change back to private
	public double variance;
	private int visitCount;
	private final Node parent;
	
	//TODO: find a more efficient way to do this.
	public ArrayList<Double> simulationResults = new ArrayList<Double>();
	
	protected final GameState gameState;
	protected List<Node> children;
	
	protected final Config config;
	
	public Node(Node parent, GameState gameState, Config config) {
		this.parent = parent;
		this.gameState = gameState;
		this.config = config;
		
		this.expectedValue = 0.0;
		this.variance = 0.0;
		this.visitCount = 0;
		
		this.children = null;
	}

	public void setExpectedValue(double expectedValue) {
		this.expectedValue = expectedValue;
	}

	public double getExpectedValue() {
		return expectedValue;
	}
	
	public double getVariance() {
		return variance;
	}
	
	public double getStdDev() {
		return Math.sqrt(getVariance());
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
	
	public void printDetails(String indent) {
//		System.out.println(indent + "Node type = " + this.getClass().getSimpleName());
//		System.out.println(indent + "Last action = " + gameState.getLastAction().getDescription());
//		System.out.println(indent + "EV = " + getExpectedValue());
//		System.out.println(indent + "StdDev = " + getStdDev());
//		System.out.println(indent + "VC = " + getVisitCount());
//		System.out.println();
		
		System.out.println(indent + gameState.getLastAction().getDescription() + " -> " + this.getClass().getSimpleName());
		System.out.println(indent + "EV = " + getExpectedValue());
		System.out.println(indent + "StdDev = " + getStdDev());
		System.out.println(indent + "VC = " + getVisitCount());
		System.out.println(indent + "sqrt(log(VCp)/VCc) = " + 
				(parent==null?"?":Math.sqrt(Math.log(parent.getVisitCount())/this.getVisitCount())));
		System.out.println();
	}
	
	public void printChildrensDetails(String indent) {
		if(children==null) System.out.println(indent + "This node has no children.");
		else if(children.size()>10) System.out.println(indent + "This node has too many children to print.");
		else for(Node child: children) 
			child.printDetails(indent);
	}
	
}





