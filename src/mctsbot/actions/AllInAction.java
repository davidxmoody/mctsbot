package mctsbot.actions;

//TODO: Make use of this class.
public class AllInAction implements Action {

	private static final long serialVersionUID = 1L;
	
	private final double amount;
	
	public AllInAction(double amount) {
		this.amount = amount;
	}
	
	public double getAmount() {
		return amount;
	}

	public String getDescription() {
		return "All In Action " + amount;
	}

}
