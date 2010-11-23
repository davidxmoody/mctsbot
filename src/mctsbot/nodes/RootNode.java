package mctsbot.nodes;

import mctsbot.Config;
import mctsbot.gamestate.GameState;


public class RootNode extends ChoiceNode {

	public RootNode(GameState gameState, Config config) {
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
