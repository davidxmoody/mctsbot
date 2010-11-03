package mctsbot.actions;

public class ActionList {
	
	private final Action lastAction;
	private final ActionList pastActions;
	
	public ActionList(Action lastAction) {
		this.lastAction = lastAction;
		this.pastActions = null;
	}
	
	public ActionList(ActionList pastActions, Action lastAction) {
		this.lastAction = lastAction;
		this.pastActions = pastActions;
	}

}
