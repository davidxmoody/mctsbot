package mctsbot.actions;

// TODO: make the blinds extend a blind action not a raise action.
public class SmallBlindAction implements Action {

	private static final long serialVersionUID = 1L;
	
	private final double amount;

	public SmallBlindAction(double amount) {
		this.amount = amount;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public String getDescription() {
		return "Small_Blind_" + getAmount();
	}

}
