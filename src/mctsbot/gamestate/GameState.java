package mctsbot.gamestate;

import java.util.LinkedList;
import java.util.List;

import mctsbot.actions.Action;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.GameInfo;
import com.biotools.meerkat.Hand;

public class GameState implements Cloneable {
	
	public static final int PREFLOP = 1;
	public static final int FLOP = 2;
	public static final int TURN = 3;
	public static final int RIVER = 4;
	
	private double pot;
	
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
	
	public static GameState initialise(GameInfo gi, Card c1, Card c2) {
		final GameState newGameState = new GameState();
		
		//TODO
		
		return newGameState;
	}
	
	public GameState update(GameInfo gi) {
		final GameState newGameState = this.clone();
		
		//TODO
		
		return newGameState;
	}
	
	public GameState doAction(int actionType) {
		final GameState newGameState = this.clone();
		
		//TODO
		
		return newGameState;
	}
	
	public GameState dealCard(Card card) {
		final GameState newGameState = this.clone();
		
		newGameState.table = new Hand(table);
		newGameState.table.addCard(card);
		
		return newGameState;
	}
	
	public Action getLastAction() {
		return lastAction;
	}
	
	public boolean isNextPlayerToAct() {
		return nextPlayerToAct!=-1;
	}
	
	public int getNextPlayerToAct() {
		return 0; //TODO
	}
	
	public int getNextActivePlayer() {
		return 0; //TODO
	}
	
	public int getNextActivePlayer(int seat) {
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

	public int getBotSeat() {
		return botSeat;
	}
	
	public int getDealerSeat() {
		return dealerSeat;
	}
	
	public int getNoOfActivePlayers() {
		return activePlayers.size();
	}
	
	public Card getC1() {
		return c1;
	}
	
	public Card getC2() {
		return c2;
	}
	
	public Hand getTable() {
		return table;
	}
	
	public GameState goToNextStage() {
		final GameState newGameState = this.clone();
		
		if(stage==FLOP) newGameState.betSize = betSize*2;
		newGameState.stage = stage+1;
		
		newGameState.activePlayers = new LinkedList<Player>();
		for(Player p: activePlayers) {
			newGameState.activePlayers.add(p.newRound());
		}
		
		newGameState.nextPlayerToAct = getNextActivePlayer(dealerSeat);
		
		newGameState.maxBetThisRound = 0.0;
		
		return newGameState;
	}
	
	public GameState clone() {
		final GameState newGameState = new GameState();
		
		newGameState.pot = pot;
		newGameState.table = table;
		newGameState.activePlayers = activePlayers;
		newGameState.nextPlayerToAct = nextPlayerToAct;
		newGameState.lastAction = lastAction;
		newGameState.betSize = betSize;
		newGameState.maxBetThisRound = maxBetThisRound;
		newGameState.stage = stage;
		
		return newGameState;
	}

	
}



