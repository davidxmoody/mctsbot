package mctsbot.strategies.selection;

import java.util.List;
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
			
//			final int numChildren = node.getChildren().size();
//			final int selectedChildIndex = random.nextInt(numChildren);
//			return node.getChildren().get(selectedChildIndex);
			
			
			//TODO: put these changes into a separate class
			final List<Node> children = node.getChildren();
			final int stage = node.getGameState().getStage();
			
			final double randomDouble = random.nextDouble();
			
			if(randomDouble<PROB_RAISE[stage]) {
				return children.get(0);
				
			} else if(randomDouble<PROB_CALL[stage]+PROB_RAISE[stage]) {
				return children.get(1);
				
			} else {
				return children.get(2);
			}
			
			
			
		// Unknown node type?
		} else {
			//System.err.println("A simulation strategy was used on an unknown node type: "
			//		+ node.getClass().toString());
			return null;
		}
	}
	
	private static final int[] FOLD_WEIGHTS = {45, 7, 6, 19};
	private static final int[] CALL_WEIGHTS = {48, 55, 60, 58};
	private static final int[] RAISE_WEIGHTS = {7, 38, 34, 23};
	private static final int[] TOTAL_WEIGHTS = {
		FOLD_WEIGHTS[0]+CALL_WEIGHTS[0]+RAISE_WEIGHTS[0], 
		FOLD_WEIGHTS[1]+CALL_WEIGHTS[1]+RAISE_WEIGHTS[1], 
		FOLD_WEIGHTS[2]+CALL_WEIGHTS[2]+RAISE_WEIGHTS[2], 
		FOLD_WEIGHTS[3]+CALL_WEIGHTS[3]+RAISE_WEIGHTS[3]};
	
	@SuppressWarnings("unused")
	private static final double[] PROB_FOLD = {
		(double)FOLD_WEIGHTS[0]/TOTAL_WEIGHTS[0], 
		(double)FOLD_WEIGHTS[1]/TOTAL_WEIGHTS[1], 
		(double)FOLD_WEIGHTS[2]/TOTAL_WEIGHTS[2], 
		(double)FOLD_WEIGHTS[3]/TOTAL_WEIGHTS[3]};
	private static final double[] PROB_CALL = {
		(double)CALL_WEIGHTS[0]/TOTAL_WEIGHTS[0], 
		(double)CALL_WEIGHTS[1]/TOTAL_WEIGHTS[1], 
		(double)CALL_WEIGHTS[2]/TOTAL_WEIGHTS[2], 
		(double)CALL_WEIGHTS[3]/TOTAL_WEIGHTS[3]};
	private static final double[] PROB_RAISE = {
		(double)RAISE_WEIGHTS[0]/TOTAL_WEIGHTS[0], 
		(double)RAISE_WEIGHTS[1]/TOTAL_WEIGHTS[1], 
		(double)RAISE_WEIGHTS[2]/TOTAL_WEIGHTS[2], 
		(double)RAISE_WEIGHTS[3]/TOTAL_WEIGHTS[3]};
	
	
//	protected double getRaiseProb(int stage, boolean canCheck) {
//		if(stage<GameState.PREFLOP || stage>GameState.RIVER) throw new RuntimeException(
//				"Invalid stage passed to getRaiseProb: " + stage);
//		
//		return RAISE_WEIGHTS[stage]/
//			(RAISE_WEIGHTS[stage]+CALL_WEIGHTS[stage]+(canCheck?0:FOLD_WEIGHTS[stage]));
//	}
//	
//	protected double getCallProb(int stage, boolean canCheck) {
//		if(stage<GameState.PREFLOP || stage>GameState.RIVER) throw new RuntimeException(
//				"Invalid stage passed to getCallProb: " + stage);
//		
//		return CALL_WEIGHTS[stage]/
//			(RAISE_WEIGHTS[stage]+CALL_WEIGHTS[stage]+(canCheck?0:FOLD_WEIGHTS[stage]));
//	}
//	
//	protected double getFoldProb(int stage, boolean canCheck) {
//		if(stage<GameState.PREFLOP || stage>GameState.RIVER) throw new RuntimeException(
//				"Invalid stage passed to getFoldProb: " + stage);
//		
//		return (canCheck?0.0:(FOLD_WEIGHTS[stage]/
//			(RAISE_WEIGHTS[stage]+CALL_WEIGHTS[stage]+FOLD_WEIGHTS[stage])));
//	}

}
