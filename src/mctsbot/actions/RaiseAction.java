package mctsbot.actions;

public class RaiseAction implements Action {
	
	private static final long serialVersionUID = 1L;
	
	private final double amount;
	
	public RaiseAction(double amount) {
		this.amount = amount;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public String getDescription() {
		return "Raise Action " + amount;
	}

}
