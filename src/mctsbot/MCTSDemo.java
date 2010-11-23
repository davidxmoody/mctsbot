package mctsbot;

import java.io.IOException;

import mctsbot.actions.Action;
import mctsbot.gamestate.GameState;
import mctsbot.nodes.LeafNode;
import mctsbot.nodes.Node;
import mctsbot.nodes.RootNode;
import mctsbot.opponentmodel.handrank.RandomHandRankOpponentModel;
import mctsbot.strategies.actionselection.HighestEVActionSelectionStrategy;
import mctsbot.strategies.backpropagation.AveragingBackpropagationStrategy;
import mctsbot.strategies.selection.UCTSelectionStrategy;
import mctsbot.strategies.simulation.StaticDistributionSimulationStrategy;

public class MCTSDemo  {
	
	public static void main(String[] args) throws IOException {
		
		final MCTSBot mctsbot = new MCTSBot();
		
		final Config config = new Config(
				new HighestEVActionSelectionStrategy(), 
				new UCTSelectionStrategy(), 
				new StaticDistributionSimulationStrategy(), 
				new AveragingBackpropagationStrategy(), 
				new RandomHandRankOpponentModel(), 
				null );
		
		GameState gameState = GameState.randomDemo();
		
		while(gameState.isNextPlayerToAct()) 
			gameState = gameState.doAction(Action.CALL);
		
		gameState = gameState.dealRandomCard().dealRandomCard().dealRandomCard().goToNextStage();
		
		while(!gameState.isBotNextPlayerToAct()) 
			gameState = gameState.doAction(Action.CALL);
		

		final RootNode root = new RootNode(gameState, config);
		
		mctsbot.performIterations(root, 100);
		
		System.out.println();
		
		gameState.printDetails();
		
		
		
		Node currentNode = root;
		
		while(true) {
			System.out.println("Current Node is a " + currentNode.getClass().getSimpleName() + 
					" and has " + currentNode.getChildren().size() + " children.");
			System.out.println();
			currentNode.printChildrensDetails();
			
			int input = System.in.read();
			
			switch (input) {
			// w
			case 119:
				if(currentNode instanceof RootNode) continue;
				currentNode = currentNode.getParent();
				break;
				
			// a
			case 97:
				if(currentNode instanceof LeafNode) continue;
				currentNode = currentNode.getChildren().get(0);
				break;
				
			// s
			case 115:
				if(currentNode instanceof LeafNode) continue;
				currentNode = currentNode.getChildren().get(1);
				break;
				
			// d
			case 100:
				if(currentNode instanceof LeafNode) continue;
				currentNode = currentNode.getChildren().get(2);
				break;
				
			// q
			case 113:
				System.out.println("Quitting.");
				return;

			default:
				//System.out.println("input was: " + input);
				break;
			}
		}
		
		
	}

}
