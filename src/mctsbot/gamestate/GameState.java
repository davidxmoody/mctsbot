package mctsbot.gamestate;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import mctsbot.actions.Action;
import mctsbot.actions.BigBlindAction;
import mctsbot.actions.CallAction;
import mctsbot.actions.FoldAction;
import mctsbot.actions.RaiseAction;
import mctsbot.actions.SmallBlindAction;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.Deck;
import com.biotools.meerkat.GameInfo;
import com.biotools.meerkat.Hand;
import com.biotools.meerkat.PlayerInfo;

public class GameState implements Cloneable {
	
	public static final int PREFLOP = 1;
	public static final int FLOP = 2;
	public static final int TURN = 3;
	public static final int RIVER = 4;
	public static final int SHOWDOWN = 5;
	
	private static final int MAX_ALLOWED_BET_MULTIPLE = 4;
	
	private static final Random random = new Random();
	
	private static Card c1,c2;
	private static int botSeat;
	
	private double pot;
	private Hand table;
	
	//TODO: Check that seat map is updated correctly
	//private Map<Integer, Player> seatMap;
	private List<Player> activePlayers;
	
	private static int dealerSeat;
	
	//TODO make sure this will update correctly
	//TODO make private again
	public int nextPlayerToAct;
	
	private Action lastAction;
	
	private double betSize;
	private static double smallBlindSize;
	private static double bigBlindSize;
	
	private double maxBetThisRound;
	
	private int stage;
	
	private int committedPlayers;
	
	
	//TODO: remove unnecessary methods
	
	
	private GameState() { }
	
	
	public static GameState demo(Card c1, Card c2, int botSeat, int dealerSeat, int players) {
		
		if(players<2 || c1==null || c2==null) throw new RuntimeException();
		
		GameState newGameState = new GameState();
		
		GameState.smallBlindSize = 0.5;
		GameState.bigBlindSize = 1.0;
		GameState.dealerSeat = dealerSeat;
		
		newGameState.pot = 0.0;
		newGameState.betSize = 1.0;
		newGameState.lastAction = null;
		newGameState.maxBetThisRound = 0;
		newGameState.stage = PREFLOP;
		newGameState.table = new Hand();
		newGameState.nextPlayerToAct = 0;
		newGameState.activePlayers = new LinkedList<Player>();
		newGameState.committedPlayers = 0;
		//newGameState.seatMap = new TreeMap<Integer, Player>();
		
		for(int i=0; i<players; i++) {
			newGameState.activePlayers.add(new Player(1000, i));
		}
		
		newGameState = newGameState.holeCards(c1, c2, botSeat);
		
		int smallBlindSeat = dealerSeat;
		int bigBlindSeat = newGameState.getNextActivePlayerSeat(dealerSeat);
		
		if(players>2) {
			smallBlindSeat = newGameState.getNextActivePlayerSeat(dealerSeat);
			bigBlindSeat = 	newGameState.getNextActivePlayerSeat(
							newGameState.getNextActivePlayerSeat(dealerSeat));
		}
		newGameState = newGameState.doSmallBlind(smallBlindSeat);
		newGameState = newGameState.doBigBlind(bigBlindSeat);
		
		if(botSeat!=newGameState.getNextActivePlayerSeat(bigBlindSeat)) {
			while(!newGameState.isBotNextPlayerToAct()) {
				newGameState = newGameState.doAction(Action.CALL);
			}
		}

		return newGameState;
	}
	
	
	
	
	public static GameState randomDemo() {
		final Card randomCard1 = new Card(random.nextInt(52));
		Card randomCard2 = null;
		while((randomCard2=new Card(random.nextInt(52))).equals(randomCard1)) {}
		return demo(randomCard1, randomCard2, 0, random.nextInt(2), 2);
	}
	
	
	
	
	
	
	public static GameState initialise(GameInfo gi) {
		final GameState newGameState = new GameState();
		
		GameState.smallBlindSize = gi.getSmallBlindSize();
		GameState.bigBlindSize = gi.getBigBlindSize();
		GameState.dealerSeat = gi.getButtonSeat();
		
		newGameState.pot = 0.0;
		newGameState.betSize = gi.getCurrentBetSize();
		newGameState.lastAction = null;
		newGameState.maxBetThisRound = 0;
		newGameState.stage = PREFLOP;
		newGameState.table = new Hand();
		newGameState.nextPlayerToAct = gi.getSmallBlindSeat();
		newGameState.activePlayers = new LinkedList<Player>();
		newGameState.committedPlayers = 0;
		//newGameState.seatMap = new TreeMap<Integer, Player>();
		
		
		int firstSeat = gi.nextActivePlayer(dealerSeat);
		int s = firstSeat;
		do {
			PlayerInfo pi = gi.getPlayer(s);
			Player newPlayer = new Player(
					pi.getBankRoll(), s);
			newGameState.activePlayers.add(newPlayer);
			//newGameState.seatMap.put(s, newPlayer);
		} while((s=gi.nextActivePlayer(s))!=firstSeat);
		
		return newGameState;
	}
	
	public GameState holeCards(Card c1, Card c2, int seat) {
		GameState.c1 = c1;
		GameState.c2 = c2;
		GameState.botSeat = seat;
		
		return this;
	}
	
	public GameState doSmallBlind(int seat) {
		final GameState newGameState = this.clone();
		
		final SmallBlindAction smallBlindAction = new SmallBlindAction(smallBlindSize);
		
		final Player player = getPlayer(seat);
				
		newGameState.activePlayers = new LinkedList<Player>(activePlayers);
		newGameState.activePlayers.remove(player);
		newGameState.activePlayers.add(player.doCallOrRaiseAction(smallBlindAction, 0));
		
		newGameState.nextPlayerToAct = newGameState.getNextActivePlayerSeat(seat);
		
		newGameState.lastAction = smallBlindAction;
		
		newGameState.maxBetThisRound = smallBlindAction.getAmount();
		
		newGameState.pot = newGameState.pot + smallBlindAction.getAmount();
		
		return newGameState;
	}
	
	public GameState doBigBlind(int seat) {
		final GameState newGameState = this.clone();
		
		final BigBlindAction bigBlindAction = new BigBlindAction(bigBlindSize);
		
		final Player player = getPlayer(seat);
				
		newGameState.activePlayers = new LinkedList<Player>(activePlayers);
		newGameState.activePlayers.remove(player);
		newGameState.activePlayers.add(player.doCallOrRaiseAction(bigBlindAction, 0));
		
		newGameState.nextPlayerToAct = newGameState.getNextActivePlayerSeat(seat);
		
		newGameState.lastAction = bigBlindAction;
		
		newGameState.maxBetThisRound = bigBlindAction.getAmount();
		
		newGameState.pot = newGameState.pot + bigBlindAction.getAmount();
		
		return newGameState;
	}
	
	public GameState doAction(int actionType) {
		
		final GameState newGameState = this.clone();
		
		final Player nextPlayer = getPlayer(nextPlayerToAct);
		//System.out.print("nextPlayer is " + ((nextPlayer==null)?"":" not") + " null");
		//System.out.print(", actionType = " + actionType);
		//System.out.println(" and nextPlayerToAct = " + nextPlayerToAct);
		
		
		if(actionType==Action.RAISE) {
			
			RaiseAction raiseAction = null;
			
			// Check to see if raising is allowed.
			if(MAX_ALLOWED_BET_MULTIPLE*betSize<maxBetThisRound+betSize) {
				if(MAX_ALLOWED_BET_MULTIPLE*betSize==maxBetThisRound) {
					return doAction(Action.CALL);
				} else {
					raiseAction = new RaiseAction(
							maxBetThisRound-nextPlayer.getAmountInPotInCurrentRound());
				}
			} else {
				raiseAction = new RaiseAction(
						maxBetThisRound+betSize-nextPlayer.getAmountInPotInCurrentRound());
			}
			
			newGameState.activePlayers = new LinkedList<Player>(activePlayers);
			newGameState.activePlayers.remove(nextPlayer);
			newGameState.activePlayers.add(nextPlayer.doCallOrRaiseAction(raiseAction, stage));
			
			newGameState.committedPlayers = 1;
			
			
			newGameState.nextPlayerToAct = 
				(newGameState.isNextPlayerToAct()) ? 
						newGameState.getNextActivePlayerSeat(nextPlayerToAct) : -1;
			
			newGameState.pot += raiseAction.getAmount();
			newGameState.maxBetThisRound += betSize;
			
			newGameState.lastAction = raiseAction;
			
		// Call.
		} else if(actionType==Action.CALL) {
			
			final CallAction callAction = 
				new CallAction(maxBetThisRound-nextPlayer.getAmountInPotInCurrentRound());
			
			newGameState.activePlayers = new LinkedList<Player>(activePlayers);
			newGameState.activePlayers.remove(nextPlayer);
			newGameState.activePlayers.add(nextPlayer.doCallOrRaiseAction(callAction, stage));
			
			newGameState.committedPlayers++;
			
			newGameState.nextPlayerToAct = 
				(newGameState.isNextPlayerToAct()) ? 
						newGameState.getNextActivePlayerSeat(nextPlayerToAct) : -1;
			
			newGameState.pot += callAction.getAmount();
			
			newGameState.lastAction = callAction;
			
			
		// Fold.
		} else if(actionType==Action.FOLD) {
			
			newGameState.activePlayers = new LinkedList<Player>(activePlayers);
			newGameState.activePlayers.remove(nextPlayer);
			
			newGameState.nextPlayerToAct = 
				(newGameState.isNextPlayerToAct()) ? 
						newGameState.getNextActivePlayerSeat(nextPlayerToAct) : -1;
						
			newGameState.lastAction = new FoldAction();
			
			
		// Invalid actionType?
		} else {
			throw new RuntimeException("Invalid actionType passed to doAction: " + actionType);
		}
		
		return newGameState;
		
	}
	
	public double getBotMoney() {
		return getPlayer(botSeat).getMoney();
	}
	
	
	
	
	
	public GameState dealCard(Card card) {
		final GameState newGameState = this.clone();
		
		newGameState.table = new Hand(table);
		newGameState.table.addCard(card);
		
		return newGameState;
	}
	
	
	public GameState dealRandomCard() {
		final Card randomCard = new Card(random.nextInt(52));
		if(randomCard.equals(c1)) return dealRandomCard();
		if(randomCard.equals(c2)) return dealRandomCard();
		for(int i=0; i<table.size(); i++) 
			if(randomCard.equals(table.getCard(i))) return dealRandomCard();
		
		return dealCard(randomCard);
	}
	
	
	
	public Deck createDeck() {
		final Deck deck = new Deck();
		deck.extractCard(c1);
		deck.extractCard(c2);
		deck.extractHand(table);
		return deck;
	}

	
	
	
	
	
	
	
	
	public GameState setTable(Hand table) {
		final GameState newGameState = this.clone();
		
		newGameState.table = table;
		
		return newGameState;
	}
	
	public Action getLastAction() {
		return lastAction;
	}
	
	public boolean isNextPlayerToAct() {
		if(activePlayers.size()<=1) return false;
		
		/*for(Player p: activePlayers) {
			System.out.println("Player: " + p.getSeat() + ", " + p.getMoney());
		}
		System.out.println("");*/
		
		return (committedPlayers<activePlayers.size());
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
		
	
	
	public Player getPlayer(int seat) {
		for(Player p:activePlayers) {
			if(p.getSeat()==seat) return p;
		}
		return null;
		//return seatMap.get(seat);
	}
	
	public double getBetSize() {
		return betSize;
	}
	
	public double getAmountToCall(int seat) {
		return maxBetThisRound-getPlayer(seat).getAmountInPotInCurrentRound();
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
	
	public double getAmountInPot(int seat) {
		return getPlayer(seat).getAmountInPot();
	}
	
	public double getPot() {
		return pot;
	}
	
	public double getMaxBetThisRound() {
		return maxBetThisRound;
	}
	
	public boolean isBotActive() {
		return getPlayer(botSeat)!=null;
	}
	
	public GameState goToNextStage() {
		
		/*System.out.println("goToNextStage called, current stage = " + 
				((stage==PREFLOP)?"PREFLOP":(stage==FLOP)?"FLOP":
				(stage==TURN)?"TURN":(stage==RIVER)?"RIVER":
				(stage==SHOWDOWN)?"SHOWDOWN":"UNKNOWN") + 
				", next stage = " + ((stage+1==PREFLOP)?"PREFLOP":
				(stage+1==FLOP)?"FLOP":(stage+1==TURN)?"TURN":
				(stage+1==RIVER)?"RIVER":(stage+1==SHOWDOWN)?"SHOWDOWN":"UNKNOWN"));*/
		
		final GameState newGameState = this.clone();
		
		if(stage==FLOP) newGameState.betSize = betSize*2;
		
		if(!(stage==PREFLOP || stage==FLOP || stage==TURN || stage==RIVER)) {
			System.out.println("goToNextStage called error caused because stage = " + stage);
			throw new RuntimeException("goToNextStage called while not in " + 
					"PreFlop, Flop, Turn or River.");
		}
					
		
		newGameState.stage = stage+1;
		
		newGameState.activePlayers = new LinkedList<Player>();
		for(Player p: activePlayers) {
			newGameState.activePlayers.add(p.newRound());
		}
		
		newGameState.nextPlayerToAct = getNextActivePlayerSeat(dealerSeat);
		
		newGameState.maxBetThisRound = 0.0;
		
		newGameState.committedPlayers = 
			(newGameState.stage==SHOWDOWN)?newGameState.activePlayers.size():0;
		
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
		newGameState.committedPlayers = committedPlayers;
		//newGameState.seatMap = seatMap;
		
		return newGameState;
	}
	
	public void printDetails() {
		System.out.println("c1 = " + c1);
		System.out.println("c2 = " + c2);
		for(int i=0; i<table.size(); i++) 
			System.out.println("t" + i + " = " + table.getCard(i));
		System.out.println("table size = " + table.size());
		System.out.println("pot = " + pot);
		System.out.println("num active players = " + activePlayers.size());
		System.out.println("num committed players = " + committedPlayers);
		System.out.print("stage = " + stage + ", ");
		System.out.println(
				(stage==PREFLOP)?"PREFLOP":
					(stage==FLOP)?"FLOP":
						(stage==TURN)?"TURN":
							(stage==RIVER)?"RIVER":
								(stage==SHOWDOWN)?"SHOWDOWN":
									"UNKNOWN: " + stage);
		System.out.println("");
	}

	
}



