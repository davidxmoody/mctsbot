package mctsbot.strategies;

import java.util.Random;

import mctsbot.gamestate.GameState;
import mctsbot.nodes.ChanceNode;
import mctsbot.nodes.ChoiceNode;
import mctsbot.nodes.LeafNode;
import mctsbot.nodes.Node;
import mctsbot.nodes.OpponentNode;

public class RandomSelectionStrategy implements SelectionStrategy {
	
	private static final Random random = new Random();

	public Node select(Node node) {
		
		// Should never be called on a leaf node.
		if(node instanceof LeafNode) {
			System.err.println("A selection strategy was used on a LeafNode.");
			return null;
			
			
		// ChanceNode
		} else if(node instanceof ChanceNode) {
			
			final int numChildren = node.getChildren().size();
			final int gameStage = node.getGameState().getStage();
			final int possCombinations = (gameStage==GameState.PREFLOP) ? 50*49*48 : 
				(gameStage==GameState.FLOP) ? 47 : 46 ;
				
			final int selectedChildIndex = random.nextInt(possCombinations);
			
			if(selectedChildIndex<numChildren) {
				return node.getChildren().get(selectedChildIndex);
			} else {
				return ((ChanceNode) node).generateChild();
			}
			
			
		// ChoiceNode/OpponentNode
		} else if((node instanceof OpponentNode) || (node instanceof ChoiceNode)) {
			
			final int numChildren = node.getChildren().size();
			final int selectedChildIndex = random.nextInt(numChildren);
			return node.getChildren().get(selectedChildIndex);
			
			
		// Unknown node type?
		} else {
			//System.err.println("A simulation strategy was used on an unknown node type: "
			//		+ node.getClass().toString());
			return null;
		}
		
		
		
		
		
		
		
		
		
	}

}
