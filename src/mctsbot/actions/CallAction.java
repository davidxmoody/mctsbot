package mctsbot.actions;

public class CallAction implements Action {
	
	private static final long serialVersionUID = 1L;
	
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
