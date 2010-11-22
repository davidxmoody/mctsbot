package mctsbot.actions;



public class CallAction implements Action {
	
	private final double amount;
	
	public CallAction(double amount) {
		this.amount = amount;
	}
	
	public double getAmount() {
		return amount;
	}

	public String getDescription() {
		return "Call Action " + amount;
	}

}
