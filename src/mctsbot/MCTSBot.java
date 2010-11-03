package mctsbot;

import mctsbot.gamestate.GameState;
import mctsbot.nodes.Node;
import mctsbot.nodes.RootNode;

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
	
	private GameState currentGameState;
	
	private static final long THINKING_TIME = 1000;

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

		// Make root node.
		RootNode root = new RootNode(currentGameState);
		
		// Do iterations until time limit reached.
		final long endTime = System.currentTimeMillis() + getThinkingTime();
		int noIterations = 0;
		
		do {
			iterate(root);
			iterate(root);
			iterate(root);
			iterate(root);
			iterate(root);
			iterate(root);
			iterate(root);
			iterate(root);
			
			noIterations+=8;
			
		} while(endTime>System.currentTimeMillis());
		
		System.out.println("Performed " + noIterations);
		
		// Perform action.
		
		
		
		
		
		
		
		
		return Action.checkOrFoldAction(0);
	}
	
	
	private void iterate(RootNode node) {
		// Selection until a leaf of the stored tree is reached.
		Node selectedNode = node.selectRecursively();
		
		// Expand selected leaf node.
		selectedNode.generateChildren();
		
		// Simulate a game.
		final double expectedValue = selectedNode.simulate();
		
		// Propagate changes.
		selectedNode.backpropagate(expectedValue);
	}
	
	private long getThinkingTime() {
		return THINKING_TIME;
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
