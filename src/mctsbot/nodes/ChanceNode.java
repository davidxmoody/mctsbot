package mctsbot.nodes;

import java.util.LinkedList;

import mctsbot.gamestate.GameState;
import mctsbot.strategies.StrategyConfiguration;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.Deck;

public class ChanceNode extends Node {
	
	private Deck deck;

	public ChanceNode(Node parent, GameState gameState, StrategyConfiguration config) {
		super(parent, gameState, config);
	}

	@Override
	public void generateChildren() {
		System.out.println("generate on a chance node is being called");
		
		if(children!=null) return;
		children = new LinkedList<Node>();
	}
	
	
	//TODO clean this up.
	public Node generateChild() {
		System.out.println("generateChild on a chance node is being called");
		
		if(deck==null) {
			deck = new Deck();
			deck.extractCard(gameState.getC1());
			deck.extractCard(gameState.getC2());
			deck.extractHand(gameState.getTable());
		}
		
		generateChildren();
		
		Node newChild = null;
		
		if(gameState.getStage()==GameState.PREFLOP) {
			// This is just an approximation
			//TODO: implement this properly.
			final Card c1 = deck.pickRandomCard();
			Card c2 = deck.pickRandomCard();
			Card c3 = deck.pickRandomCard();
			while(c1.equals(c2)) c2 = deck.pickRandomCard();
			while(c1.equals(c3) || c2.equals(c3)) c3 = deck.pickRandomCard();
			// assert c1, c2 and c3 are all different cards.
			
			GameState newGameState = 
				gameState.dealCard(c1).dealCard(c2).dealCard(c3).goToNextStage();
			
			if(newGameState.isBotNextPlayerToAct()) {
				newChild = new ChoiceNode(this, newGameState, config);
			} else {
				newChild = new OpponentNode(this, newGameState, config);
			}
			
			
		} else {
			if(deck.cardsLeft()==0) return null;
			
			GameState newGameState = gameState.dealCard(deck.dealCard()).goToNextStage();
			if(newGameState.isBotNextPlayerToAct()) {
				newChild = new ChoiceNode(this, newGameState, config);
			} else {
				newChild = new OpponentNode(this, newGameState, config);
			}
		}
		
		children.add(newChild);
		return newChild;
	}


}
