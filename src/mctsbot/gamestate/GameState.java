package mctsbot.gamestate;

import java.util.List;

import mctsbot.actions.Action;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.Deck;
import com.biotools.meerkat.GameInfo;
import com.biotools.meerkat.Hand;

public class GameState {
	
	public static final int PREFLOP = 1;
	public static final int FLOP = 2;
	public static final int TURN = 3;
	public static final int RIVER = 4;
	
	private double pot;
	
	private Deck deck;
	
	private Hand table;
	
	private static Card c1,c2;
	private static int botSeat;
	
	private List<Player> activePlayers;
	
	private static int dealerSeat;
	
	private int nextPlayerToAct;
	
	private Action lastAction;
	
	private double betSize;
	
	private double maxBetThisRound;
	
	private int stage;
	
	
	private GameState() {
		
	}
	
	public GameState initialise(GameInfo gi, Card c1, Card c2) {
		//TODO
		return null;
	}
	
	public GameState update(GameInfo gi) {
		//TODO
		return null;
	}
	
	public Action getLastAction() {
		return lastAction;
	}
	
	public GameState doAction(int actionType) {
		return null;
	}
	
	/**
	 * @return a new GameState with 1 new shared card.
	 */
	/*public GameState dealRandomCard() {
		final GameState newGameState = new GameState();
		
		newGameState.pot = pot;
		newGameState.activePlayers = activePlayers;
		newGameState.table = new Hand(table);
		newGameState.table.addCard(deck.dealCard());
		newGameState.deck = new Deck();
		newGameState.deck.copy(deck);
		
		return newGameState;
	}*/
	
	public boolean isNextPlayerToAct() {
		return nextPlayerToAct!=-1;
	}
	
	public int getNextPlayerToAct() {
		return 0; //TODO
	}
	
	public int getNextActivePlayer() {
		return 0; //TODO
	}
	
	public Player getPlayer(int seat) {
		for(Player p:activePlayers) {
			if(p.getSeat()==seat) return p;
		}
		return null;
	}
	
	public double getBetSize() {
		return betSize;
	}
	
	public double getAmountToCall(int seat) {
		try{
			return maxBetThisRound-getPlayer(seat).getAmountInPotInCurrentRound();
		} catch(NullPointerException e) {
			return 0.0;
		}
	}
	
	public int getStage() {
		return stage;
	}

	public static int getBotSeat() {
		return botSeat;
	}
	
	public int getNoOfActivePlayers() {
		return activePlayers.size();
	}
	
	

	
}
