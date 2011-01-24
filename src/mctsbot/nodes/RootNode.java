package mctsbot.nodes;

import mctsbot.Config;
import mctsbot.gamestate.GameState;


public class RootNode extends ChoiceNode {
	
	//private static final Random random = new Random();

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
	
	/*public Node select() {
		if(random.nextBoolean()) {
			return children.get(0);
		} else {
			return children.get(1);
		}
	}*/


}
