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
	
	protected Player(double money, List<Action> pastActions, int seat) {
		this.money = money;
		this.pastActions = pastActions;
		this.seat = seat;
	}
	
	
	public Player doCallOrRaiseAction(Action action) throws AllInException {
		//if(money<=action.getAmount()) throw new AllInException();
		
		final double newMoney = money - action.getAmount();
		final List<Action> newPastActions = new LinkedList<Action>(pastActions);
		newPastActions.add(action);
		
		return new Player(newMoney, newPastActions, seat);
	}
	
	public Player doFoldAction(FoldAction action) {
		final List<Action> newPastActions = new LinkedList<Action>(pastActions);
		newPastActions.add(action);
		return new Player(money, newPastActions, seat);
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

}



