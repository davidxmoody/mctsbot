package mctsbot.gamestate;

import java.util.LinkedList;
import java.util.List;

import mctsbot.actions.Action;
import mctsbot.actions.CallAction;
import mctsbot.actions.RaiseAction;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.GameInfo;
import com.biotools.meerkat.Hand;
import com.biotools.meerkat.PlayerInfo;

public class GameState implements Cloneable {
	
	public static final int PREFLOP = 1;
	public static final int FLOP = 2;
	public static final int TURN = 3;
	public static final int RIVER = 4;
	
	private static final int MAX_BET_MULTIPLE_ALLOWED = 4;
	
	private double pot;
	
	private Hand table;
	
	private static Card c1,c2;
	private static int botSeat;
	
	private List<Player> activePlayers;
	
	private static int dealerSeat;
	
	//TODO make sure this will update correctly
	private int nextPlayerToAct;
	
	private Action lastAction;
	
	private double betSize;
	
	private double maxBetThisRound;
	
	private int stage;
	
	
	//TODO: remove unnecessary methods
	
	
	private GameState() { }
	
	// Used for testing 
	// TODO: remove this
	public static GameState demo(Card c1, Card c2) {
		
		GameState.c1 = c1;
		GameState.c2 = c2;
		GameState.botSeat = 0;
		GameState.dealerSeat = 0;
		
		GameState newGameState = new GameState();
		
		newGameState.pot = 0.0;
		newGameState.betSize = 1.0;
		newGameState.lastAction = null;
		newGameState.maxBetThisRound = 0;
		newGameState.stage = PREFLOP;
		newGameState.table = new Hand();
		newGameState.nextPlayerToAct = 1;
		newGameState.activePlayers = new LinkedList<Player>();
		
		// The Bot.
		newGameState.activePlayers.add(new Player(1000, 0, 0, new LinkedList<Action>(), 0));
		
		// Opponent #1
		newGameState.activePlayers.add(new Player(1000, 0, 0, new LinkedList<Action>(), 1));
		
		// Opponent #2
		newGameState.activePlayers.add(new Player(1000, 0, 0, new LinkedList<Action>(), 2));
		
		return newGameState;
	}
	
	public static GameState initialise(GameInfo gi, Card c1, Card c2, int seat) {
		GameState.c1 = c1;
		GameState.c2 = c2;
		GameState.botSeat = seat;
		//GameState.dealerSeat = 
		
		final GameState newGameState = new GameState();
		
		newGameState.pot = 0.0;
		newGameState.betSize = gi.getCurrentBetSize();
		newGameState.lastAction = null;
		newGameState.maxBetThisRound = 0;
		newGameState.stage = PREFLOP;
		newGameState.table = new Hand();
		newGameState.nextPlayerToAct = gi.nextActivePlayer(gi.getButtonSeat());
		newGameState.activePlayers = new LinkedList<Player>();
		
		
		
		
		int s = seat;
		do {
			PlayerInfo pi = gi.getPlayer(s);
			newGameState.activePlayers.add(new Player(
					pi.getBankRoll(), 0.0, 0.0, new LinkedList<Action>(), s));
		} while((s=gi.nextActivePlayer(s))!=seat);
		
		
		
		return newGameState;
	}
	
	
	public GameState update(GameInfo gi) {
		final GameState newGameState = this.clone();
		
		//TODO is this needed?
		
		return newGameState;
	}
	
	
	public GameState doAction(int actionType) {
		final GameState newGameState = this.clone();
		
		// TODO: check input.
		
		final Player nextPlayer = getPlayer(nextPlayerToAct);
		
		// Raise.
		if(actionType==Action.RAISE) {
			
			// Check to see if raising is allowed.
			if(MAX_BET_MULTIPLE_ALLOWED*betSize<maxBetThisRound+betSize) {
				return doAction(Action.CALL);
			}
			
			final RaiseAction raiseAction = 
				new RaiseAction(maxBetThisRound+betSize-nextPlayer.getAmountInPotInCurrentRound());
			
			newGameState.activePlayers = new LinkedList<Player>(activePlayers);
			newGameState.activePlayers.remove(nextPlayer);
			newGameState.activePlayers.add(nextPlayer.doCallOrRaiseAction(raiseAction));
			
			newGameState.nextPlayerToAct = getNextActivePlayerSeat(nextPlayer.getSeat());
			
			newGameState.pot += raiseAction.getAmount();
			newGameState.maxBetThisRound += betSize;
			
		// Call.
		} else if(actionType==Action.CALL) {
			
			final CallAction callAction = 
				new CallAction(maxBetThisRound-nextPlayer.getAmountInPotInCurrentRound());
			
			newGameState.activePlayers = new LinkedList<Player>(activePlayers);
			newGameState.activePlayers.remove(nextPlayer);
			newGameState.activePlayers.add(nextPlayer.doCallOrRaiseAction(callAction));
			
			final Player nextNextPlayer = getNextActivePlayer(nextPlayer.getSeat());
			newGameState.nextPlayerToAct = 
				(nextNextPlayer.getAmountInPotInCurrentRound()==maxBetThisRound)? 
						-1 : nextNextPlayer.getSeat() ;
			
			newGameState.pot += callAction.getAmount();
			
			
		// Fold.
		} else if(actionType==Action.FOLD) {
			
			newGameState.activePlayers = new LinkedList<Player>(activePlayers);
			newGameState.activePlayers.remove(nextPlayer);
			// TODO: Add folded player to inactive player list?
			
			final Player nextNextPlayer = getNextActivePlayer(nextPlayer.getSeat());
			newGameState.nextPlayerToAct = 
				(nextNextPlayer.getAmountInPotInCurrentRound()==maxBetThisRound)? 
						-1 : nextNextPlayer.getSeat() ;
			
			
		// Invalid actionType?
		} else {
			System.err.println("Invalid actionType passed to doAction: " + actionType);
			return null;
		}
		
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
	
	public boolean isBotNextPlayerToAct() {
		return botSeat==nextPlayerToAct;
	}
	
	
	//TODO: make this more efficient.
	public int getNextActivePlayerSeat(int seat) {
		if(activePlayers.size()==1) return activePlayers.get(0).getSeat();
		if(activePlayers.size()<=0) return -1;
		
		int nextSeat = seat;
		for(Player p: activePlayers) {
			final int pSeat = p.getSeat();
			if(nextSeat>seat) {
				if(pSeat>seat && pSeat<nextSeat) {
					nextSeat = pSeat;
				}
			} else
				if(pSeat<=nextSeat || pSeat>seat) {
					nextSeat = pSeat;
				} 
			}
		return nextSeat;
	}
	
	
	//TODO: make this more efficient.
	public Player getNextActivePlayer(int seat) {
		if(activePlayers.size()==1) return getPlayer(seat);
		if(activePlayers.size()<=0) return null;
		
		Player nextPlayer = null;
		int nextSeat = seat;
		for(Player p: activePlayers) {
			final int pSeat = p.getSeat();
			if(nextSeat>seat) {
				if(pSeat>seat && pSeat<nextSeat) {
					nextSeat = pSeat;
					nextPlayer = p;
				}
			} else
				if(pSeat<=nextSeat || pSeat>seat) {
					nextSeat = pSeat;
					nextPlayer = p;
				} 
			}
		return nextPlayer;
	}
		
	
	//TODO: make this more efficient.
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
		
		newGameState.nextPlayerToAct = getNextActivePlayerSeat(dealerSeat);
		
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



