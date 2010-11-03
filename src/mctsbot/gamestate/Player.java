package mctsbot.gamestate;

import mctsbot.actions.Action;
import mctsbot.actions.ActionList;
import mctsbot.actions.AllInException;
import mctsbot.actions.FoldAction;

public class Player {
	
	private final double money;
	private final ActionList pastActions;
	private int seat;
	
	protected Player(double money, ActionList pastActions) {
		this.money = money;
		this.pastActions = pastActions;
	}
	
	public double getMoney() {
		return money;
	}
	
	public ActionList getActions() {
		return pastActions;
	}
	
	
	public Player doCallOrRaiseAction(Action action) throws AllInException {
		if(money<=action.getAmount()) throw new AllInException();
		
		final double newMoney = money - action.getAmount();
		final ActionList newPastActions = new ActionList(pastActions, action);
		
		return new Player(newMoney, newPastActions);
	}
	
	public Player doFoldAction(FoldAction action) {
		final ActionList newPastActions = new ActionList(pastActions, action);
		return new Player(money, newPastActions);
	}

}



