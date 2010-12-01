package mctsbot.actions;

import java.io.Serializable;

public interface Action extends Serializable {
	
	public static final int RAISE = 1;
	public static final int CALL = 2;
	public static final int FOLD = 3;
	public static final int SMALL_BLIND = 4;
	public static final int BIG_BLIND = 5;
	
	public double getAmount();
	
	public String getDescription();

}
