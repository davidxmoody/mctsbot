package mctsbot.gamestate;

import java.util.List;

import mctsbot.actions.Action;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.Deck;
import com.biotools.meerkat.GameInfo;
import com.biotools.meerkat.Hand;

public class GameState {
	
	private double pot;
	
	private Deck deck;
	
	private Hand table;
	
	private static Card c1,c2;
	
	private List<Player> activePlayers;
	
	private static int dealerSeat;
	
	
	private GameState() {
		
	}
	
	public Action getLastAction() {
		return null;
		
	}
	
	public GameState doAction(Action action) {
		return null;
	}
	
	/**
	 * @return a new GameState with 1 new shared card.
	 */
	public GameState dealRandomCard() {
		final GameState newGameState = new GameState();
		
		newGameState.pot = pot;
		newGameState.activePlayers = activePlayers;
		newGameState.table = new Hand(table);
		newGameState.table.addCard(deck.dealCard());
		newGameState.deck = new Deck();
		newGameState.deck.copy(deck);
		
		return newGameState;
	}
	
	public GameState initialise(GameInfo gi, Card c1, Card c2) {
		return null;
	}
	
	public GameState update(GameInfo gi) {
		return null;
	}

	
}
