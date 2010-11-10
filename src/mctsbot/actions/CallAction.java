package mctsbot.actions;

import mctsbot.gamestate.GameState;


public class CallAction implements Action {
	
	private final double amount;
	
	public CallAction(GameState gameState, int seat) {
		this.amount = gameState.getAmountToCall(seat);
	}
	
	public double getAmount() {
		return amount;
	}

}
