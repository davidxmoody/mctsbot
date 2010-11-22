package mctsbot.strategies;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import mctsbot.gamestate.GameState;
import mctsbot.nodes.AllOpponentsFoldedNode;
import mctsbot.nodes.BotFoldedNode;
import mctsbot.nodes.ChanceNode;
import mctsbot.nodes.LeafNode;
import mctsbot.nodes.Node;
import mctsbot.nodes.PlayerNode;
import mctsbot.nodes.ShowdownNode;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.HandEvaluator;

public class StaticDistributionSimulationStrategy implements SimulationStrategy {
	
	private static final int[] FOLD_WEIGHTS = {0, 65, 6, 8, 4};
	private static final int[] CALL_WEIGHTS = {0, 25, 46, 47, 55};
	private static final int[] RAISE_WEIGHTS = {0, 10, 48, 45, 42};
	
	
	private static final Random random = new Random();

	public double simulate(Node node) {
		
		if(node instanceof LeafNode) {
			
			if(node instanceof BotFoldedNode) {
				return node.getParent().getGameState().getBotMoney();
				
			} else if(node instanceof AllOpponentsFoldedNode) {
				return node.getGameState().getBotMoney()+node.getGameState().getPot();
				
			} else if(node instanceof ShowdownNode) {
				return simulateShowdown((ShowdownNode)node);
				
			} else {
				throw new RuntimeException("Unknown node type passed to simulate.");
			}
			
			
		} else if(node instanceof ChanceNode) {
			return simulate(((ChanceNode)node).generateChild());
			
		} else if(node instanceof PlayerNode) {
			
			// Not actually correct when dealing with the person who put in the big blind.
			//TODO: fix this
			final boolean canCheck = node.getGameState().getMaxBetThisRound()==0.0;
			final int stage = node.getGameState().getStage();
			
			final double randomDouble = random.nextDouble();
			final double raiseProb = getRaiseProb(stage, canCheck);
			final double callProb = getCallProb(stage, canCheck);
			
			if(randomDouble<raiseProb) {
				return simulate(((PlayerNode)node).createRaiseNode());
				
			} else if(randomDouble<raiseProb+callProb) {
				return simulate(((PlayerNode)node).createCallNode());
				
			} else {
				return simulate(((PlayerNode)node).createFoldNode());
			}

			
		} else {
			throw new RuntimeException("Unknown node type passed to simulate.");
		}
		
	}
	
	/**
	 * Simulates a showdown event where all opponents are dealt random 
	 * cards and the winner is calculated.
	 * 
	 * @param showdownNode the node to start the simulation from.
	 * @return the amount of money the bot has after the showdown.
	 */
	private double simulateShowdown(ShowdownNode showdownNode) {
		final GameState gameState = showdownNode.getGameState();
		
		//TODO: return this to normal
		int botHandRank = 0;
		try {
		
		// Calculate the bot's hand rank.
		botHandRank = HandEvaluator.rankHand(
				gameState.getC1(), gameState.getC2(), gameState.getTable());
		
		
		} catch(Exception e) {
			gameState.printDetails();
			throw new RuntimeException();
		}
		
		
		
		// Work out which cards the opponents can't have.
		final LinkedList<Card> takenCards = new LinkedList<Card>();
		takenCards.add(gameState.getC1());
		takenCards.add(gameState.getC2());
		for(int i=0; i<gameState.getTable().size(); i++) {
			takenCards.add(gameState.getTable().getCard(i));
		}
		
		// Work out how many opponents there are.
		final int noOfOpponents = gameState.getNoOfActivePlayers()-1;
		
		// For each opponent, deal them two random, non-taken cards and work 
		// out their hand rank. If it is the maximum so far then record it.
		int maxOpponentHandRank = 0;
		for(int i=0; i<noOfOpponents; i++) {
			final Card oppC1 = getRandomOppCard(takenCards);
			final Card oppC2 = getRandomOppCard(takenCards, oppC1);
			
			//TODO: remove this
			for(int j=0; j<gameState.getTable().size(); j++) {
				if(oppC1.equals(gameState.getTable().getCard(j))) 
					throw new RuntimeException();
				if(oppC2.equals(gameState.getTable().getCard(j))) 
					throw new RuntimeException();
				if(oppC1.equals(oppC2)) 
					throw new RuntimeException();
			}
			
			if(gameState.getTable().size()>5) throw new RuntimeException();
			//System.out.println("Table Size123 =========== " + gameState.getTable().size());
			
			int tableSize = gameState.getTable().size();
			
			int opponentHandRank = 0;
			
			try {
			opponentHandRank = HandEvaluator.rankHand(
					oppC1, oppC2, gameState.getTable());
			} catch(Exception e) {
				gameState.printDetails();
				
				if(tableSize!=gameState.getTable().size())
					System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHHHHHHH!");
				
				System.out.println("Table Size =========== " + gameState.getTable().size());
				throw new RuntimeException();
			}
			
			if(opponentHandRank>maxOpponentHandRank) maxOpponentHandRank = opponentHandRank;
		}
		
		// Calculate the expected value of the game state depending 
		// on whether the bot wins or loses in the random showdown.
		double expectedValue = gameState.getBotMoney();
		if(botHandRank>=maxOpponentHandRank) 
			expectedValue += gameState.getPot();
		
		return expectedValue;
	}
	
	private Card getRandomOppCard(Collection<Card> takenCards) {
		final Card oppCard = new Card(random.nextInt(52));
		for(Card takenCard: takenCards) 
			if(oppCard.equals(takenCard)) 
				return getRandomOppCard(takenCards);
		return oppCard;
	}
	
	private Card getRandomOppCard(Collection<Card> takenCards, Card oppC1) {
		final Card oppCard = new Card(random.nextInt(52));
		if(oppCard.equals(oppC1)) 
			return getRandomOppCard(takenCards, oppC1);
		for(Card takenCard: takenCards) 
			if(oppCard.equals(takenCard)) 
				return getRandomOppCard(takenCards, oppC1);
		return oppCard;
	}

	
	protected double getRaiseProb(int stage, boolean canCheck) {
		if(stage<1 || stage>4) throw new RuntimeException(
				"Invalid stage passed to a get prob method: " + stage);
		
		return RAISE_WEIGHTS[stage]/
			(RAISE_WEIGHTS[stage]+CALL_WEIGHTS[stage]+(canCheck?0:FOLD_WEIGHTS[stage]));
	}
	
	protected double getCallProb(int stage, boolean canCheck) {
		if(stage<1 || stage>4) throw new RuntimeException(
				"Invalid stage passed to a get prob method: " + stage);
		
		return CALL_WEIGHTS[stage]/
			(RAISE_WEIGHTS[stage]+CALL_WEIGHTS[stage]+(canCheck?0:FOLD_WEIGHTS[stage]));
	}
	
	protected double getFoldProb(int stage, boolean canCheck) {
		if(stage<1 || stage>4) throw new RuntimeException(
				"Invalid stage passed to a get prob method: " + stage);
		
		return (canCheck?0.0:(FOLD_WEIGHTS[stage]/
			(RAISE_WEIGHTS[stage]+CALL_WEIGHTS[stage]+FOLD_WEIGHTS[stage])));
	}

}


