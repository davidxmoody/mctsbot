package mctsbot.actions;

import mctsbot.gamestate.GameState;


public class RaiseAction implements Action {
	
	private final double amount;
	
	public RaiseAction(GameState gameState, int seat) {
		this.amount = gameState.getAmountToCall(seat) + gameState.getBetSize();
	}
	
	public double getAmount() {
		return amount;
	}

}
