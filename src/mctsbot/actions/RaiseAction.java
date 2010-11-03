package mctsbot.actions;


public class RaiseAction implements Action {
	
	private final double amount;
	
	public RaiseAction(double amount) {
		this.amount = amount;
	}
	
	public double getAmount() {
		return amount;
	}

}
