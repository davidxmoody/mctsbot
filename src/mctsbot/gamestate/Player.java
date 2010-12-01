package mctsbot.gamestate;

import java.util.LinkedList;
import java.util.List;

import mctsbot.actions.Action;
import mctsbot.actions.FoldAction;

public class Player implements Cloneable {
	
	private double money;
	private List<Action> preflopActions;
	private List<Action> flopActions;
	private List<Action> turnActions;
	private List<Action> riverActions;
	private int seat;
	
	private double amountInPot;
	private double amountInPotInCurrentRound;
	
	protected Player() { }
	
	protected Player(double money, int seat) {
		this.money = money;
		this.seat = seat;
		this.amountInPot = 0;
		this.amountInPotInCurrentRound = 0;
		this.preflopActions = null;
		this.flopActions = null;
		this.turnActions = null;
		this.riverActions = null;
	}
	
	public List<Action> getActions(int stage) {
		switch (stage) {
		case GameState.PREFLOP:
			return preflopActions;
		case GameState.FLOP:
			return flopActions;
		case GameState.TURN:
			return turnActions;
		case GameState.RIVER:
			return riverActions;

		default:
			return null;
		}
	}
	
	public void setActions(List<Action> actions, int stage) {
		switch (stage) {
		case GameState.PREFLOP:
			this.preflopActions= actions ;
		case GameState.FLOP:
			this.flopActions = actions;
		case GameState.TURN:
			this.turnActions= actions ;
		case GameState.RIVER:
			this.riverActions = actions;

		default:
			break;
		}
	}
	
	
	public Player doCallOrRaiseAction(Action action, int stage) /*throws AllInException*/ {
		//This allows players to have negative amounts of money.
		//TODO: make it so that an ALLInException will be thrown if needed.
		
		final Player newPlayer = this.clone();
		
		List<Action> newPastActions = new LinkedList<Action>();
		if(getActions(stage)!=null) newPastActions.addAll(getActions(stage));
		newPastActions.add(action);
		newPlayer.setActions(newPastActions, stage);
		
		newPlayer.money = money-action.getAmount();
		newPlayer.amountInPot = amountInPot + action.getAmount();
		newPlayer.amountInPotInCurrentRound = amountInPotInCurrentRound + action.getAmount();
		
		return newPlayer;
	}
	
	public Player doFoldAction(FoldAction action, int stage) {
		final Player newPlayer = this.clone();
		
		final List<Action> newPastActions = new LinkedList<Action>(getActions(stage));
		newPastActions.add(action);
		newPlayer.setActions(newPastActions, stage);
		
		newPlayer.amountInPotInCurrentRound = 0.0;
		
		return newPlayer;
	}
	
	public int getSeat() {
		return seat;
	}
	
	public double getMoney() {
		return money;
	}
	
	public double getAmountInPot() {
		return amountInPot;
	}

	public double getAmountInPotInCurrentRound() {
		return amountInPotInCurrentRound;
	}
	
	public Player newRound() {
		final Player newPlayer = this.clone();
		
		newPlayer.amountInPotInCurrentRound = 0;

		return newPlayer;
	}
	
	public Player clone() {
		final Player newPlayer = new Player();
		
		newPlayer.money = money;
		newPlayer.seat = seat;
		newPlayer.amountInPot = amountInPot;
		newPlayer.amountInPotInCurrentRound = amountInPotInCurrentRound;
		newPlayer.preflopActions = preflopActions;
		newPlayer.flopActions = flopActions;
		newPlayer.turnActions = turnActions;
		newPlayer.riverActions = riverActions;
		
		return newPlayer;
	}

}



