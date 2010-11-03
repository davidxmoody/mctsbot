package mctsbot.gamestate;

import java.util.List;

import com.biotools.meerkat.GameInfo;

public class GameState {
	
	private double pot = 0.0;
	private List<Player> activePlayers = null;
	
	public GameState(GameInfo gi) {
		
	}
	
	public double getAmountToCall() {
		return 0.0;
	}

	
}
