package mctsbot.nodes;

import java.util.List;

import mctsbot.Config;
import mctsbot.gamestate.GameState;

public abstract class Node {
	
	private double m = 0;
	private double s = 0;
	private int n = 0;
	
	private final Node parent;
	//TODO: change to private
	protected final GameState gameState;
	protected List<Node> children = null;
	protected final Config config;
	
	public Node(Node parent, GameState gameState, Config config) {
		this.parent = parent;
		this.gameState = gameState;
		this.config = config;
	}
	/**
	 * I found out how to do this from: 
	 * http://www.springerlink.com/content/yqr61334242077k0/fulltext.pdf
	 */
	public void update(double x) {
		n++;
		if(n==1) {
			m = x;
			s = 0;
		} else {
			m = m + (x-m)/n;
			s = s + (n/(n-1))*(m-x)*(m-x);
		}
		
	}

	//TODO: remove this
	public void setExpectedValue(double expectedValue) {
		this.m = expectedValue;
	}

	public double getExpectedValue() {
		return m;
	}

	public double getStdDev() {
		return Math.sqrt(s/(n-1));
	}
	
	public void incrementVisitCount() {
		n++;
	}
	
	public int getVisitCount() {
		return n;
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





