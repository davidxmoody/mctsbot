package mctsbot.gamestate;

import java.util.LinkedList;
import java.util.List;

import mctsbot.actions.Action;
import mctsbot.actions.AllInException;
import mctsbot.actions.FoldAction;

public class Player {
	
	private final double money;
	private final List<Action> pastActions;
	private int seat;
	
	private double amountInPot;
	private double amountInPotInCurrentRound;
	
	protected Player(double money, double amountInPot, 
			double amountInPotInCurrentRound, 
			List<Action> pastActions, int seat) {
		this.money = money;
		this.amountInPot = amountInPot;
		this.amountInPotInCurrentRound = amountInPotInCurrentRound;
		this.pastActions = pastActions;
		this.seat = seat;
	}
	
	
	public Player doCallOrRaiseAction(Action action) throws AllInException {
		//if(money<=action.getAmount()) throw new AllInException();
		
		//This allows players to have negative amounts of money.
		//TODO: make it so that an ALLInException will be thrown if needed.
		final List<Action> newPastActions = new LinkedList<Action>(pastActions);
		newPastActions.add(action);
		
		return new Player(money-action.getAmount(), 
				amountInPot+action.getAmount(), 
				amountInPotInCurrentRound+action.getAmount(), 
				newPastActions, seat);
	}
	
	public Player doFoldAction(FoldAction action) {
		final List<Action> newPastActions = new LinkedList<Action>(pastActions);
		newPastActions.add(action);
		return new Player(money, 0.0, 0.0, newPastActions, seat);
	}
	
	public int getSeat() {
		return seat;
	}
	
	public double getMoney() {
		return money;
	}
	
	public List<Action> getActions() {
		return pastActions;
	}

	public double getAmountInPot() {
		return amountInPot;
	}

	public double getAmountInPotInCurrentRound() {
		return amountInPotInCurrentRound;
	}

}



