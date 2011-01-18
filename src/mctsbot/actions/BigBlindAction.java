package mctsbot.actions;

public class BigBlindAction implements Action {

	private static final long serialVersionUID = 1L;

	private final double amount;

	public BigBlindAction(double amount) {
		this.amount = amount;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public String getDescription() {
		return "Big_Blind_" + getAmount();
	}

}
