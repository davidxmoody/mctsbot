package mctsbot.nodes;

import mctsbot.gamestate.GameState;
import mctsbot.strategies.StrategyConfiguration;


public class RootNode extends ChoiceNode {

	public RootNode(GameState gameState, StrategyConfiguration config) {
		super(null, gameState, config);
	}
	
	public Node selectRecursively() {
		Node selectedNode = this;
		
		while(selectedNode.isExpanded()) {
			selectedNode = selectedNode.select();
		}
		
		return selectedNode;
	}

}
