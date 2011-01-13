package mctsbot.actions;

// TODO: make the blinds extend a blind action not a raise action.
public class SmallBlindAction extends RaiseAction {

	private static final long serialVersionUID = 1L;

	public SmallBlindAction(double amount) {
		super(amount);
	}
	
	public String getDescription() {
		return "Small_Blind_" + getAmount();
	}

}
