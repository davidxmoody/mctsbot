package mctsbot.nodes;

import java.util.LinkedList;

import mctsbot.gamestate.GameState;
import mctsbot.strategies.StrategyConfiguration;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.Deck;

public class ChanceNode extends Node {
	
	Deck deck = null;

	public ChanceNode(Node parent, GameState gameState, StrategyConfiguration config) {
		super(parent, gameState, config);
	}

	@Override
	public void generateChildren() {
		if(children!=null) return;
		children = new LinkedList<Node>();
	}
	
	public Node generateChild() {
		// Create deck if necessary.
		if(deck==null) deck = gameState.createDeck();
		
		// Advance to the next game state.
		GameState newGameState = null;
		if(gameState.getStage()==GameState.PREFLOP) {
			// This is just an approximation
			final Card c1 = deck.pickRandomCard();
			Card c2 = deck.pickRandomCard();
			Card c3 = deck.pickRandomCard();
			while(c1.equals(c2)) c2 = deck.pickRandomCard();
			while(c1.equals(c3) || c2.equals(c3)) c3 = deck.pickRandomCard();
			// assert c1, c2 and c3 are all different cards.
			newGameState = gameState.dealCard(c1).dealCard(c2).dealCard(c3).goToNextStage();
			
		} else if(gameState.getStage()==GameState.FLOP || 
				  gameState.getStage()==GameState.TURN){
			if(deck.cardsLeft()==0) return null;
			newGameState = gameState.dealCard(deck.extractRandomCard()).goToNextStage();
			
		} else {
			throw new RuntimeException();
		}
		
		// Create the new child from the new game state and return it.
		Node newChild = null;
		if(newGameState.isBotNextPlayerToAct()) {
			newChild = new ChoiceNode(this, newGameState, config);
		} else {
			newChild = new OpponentNode(this, newGameState, config);
		}
		
		// Add new child to list of children and return the new child.
		if(children==null) children = new LinkedList<Node>();
		children.add(newChild);
		return newChild;
	}


}
