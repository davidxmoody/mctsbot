package mctsbot;

import mctsbot.nodes.Node;

import com.biotools.meerkat.Action;
import com.biotools.meerkat.Card;
import com.biotools.meerkat.GameInfo;
import com.biotools.meerkat.Player;
import com.biotools.meerkat.util.Preferences;

public class MCTSBot implements Player {
	
	private int seat;
	private Card c1, c2;
	private GameInfo gi;
	private Preferences prefs;

	@Override
	public void init(Preferences arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void holeCards(Card arg0, Card arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Action getAction() {


		//Make root node
		
		
		
		
		//Do iterations until time limit reached
		
		
		
		
		
		//Perform action		
		
		
		
		return null;
	}
	
	private void iterate(Node node) {
		//Selection until a leaf is reached
		
		
		//Expand selected leaf node
		
		
		//
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public void actionEvent(int arg0, Action arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dealHoleCardsEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameOverEvent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameStartEvent(GameInfo gi) {
		this.gi = gi;
	}

	@Override
	public void gameStateChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showdownEvent(int arg0, Card arg1, Card arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stageEvent(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void winEvent(int arg0, double arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	

	

	

}
