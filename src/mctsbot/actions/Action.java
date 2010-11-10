package mctsbot.actions;

public interface Action {
	
	public static final int RAISE = 1;
	public static final int CALL = 2;
	public static final int FOLD = 3;
	
	public double getAmount();

}
