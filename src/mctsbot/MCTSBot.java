package mctsbot;

import mctsbot.gamestate.GameState;
import mctsbot.nodes.Node;
import mctsbot.nodes.RootNode;
import mctsbot.strategies.AveragingBackpropagationStrategy;
import mctsbot.strategies.HighestEVActionSelectionStrategy;
import mctsbot.strategies.RandomSelectionStrategy;
import mctsbot.strategies.RandomSimulationStrategy;
import mctsbot.strategies.StrategyConfiguration;

import com.biotools.meerkat.Action;
import com.biotools.meerkat.Card;
import com.biotools.meerkat.GameInfo;
import com.biotools.meerkat.Player;
import com.biotools.meerkat.util.Preferences;

public class MCTSBot implements Player {
	
	private static final long THINKING_TIME = 500;
	
	private int seat;
	private Card c1, c2;
	private GameInfo gi;
	private Preferences prefs;
	
	private GameState currentGameState;
	private StrategyConfiguration config;
	

	@Override
	public void init(Preferences prefs) {
		this.prefs = prefs;
		
		// Create a new config.
		
		// This is just a simple config.
		// TODO: create the config from the given preferences.
		config = new StrategyConfiguration(
				new HighestEVActionSelectionStrategy(), 
				new RandomSelectionStrategy(), 
				new RandomSimulationStrategy(), 
				new AveragingBackpropagationStrategy() );
	}
	
	
	@Override
	public void holeCards(Card c1, Card c2, int seat) {
		this.c1 = c1;
		this.c2 = c2;
		this.seat = seat;
	}
	
	
	@Override
	public Action getAction() {

		// Make root node.
		RootNode root = new RootNode(currentGameState, config);
		
		// Do iterations until time limit reached.
		final long startTime = System.currentTimeMillis();
		final long endTime = startTime + getThinkingTime();
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
		
		System.out.println("Performed " + noIterations + " iterations in " + 
				(System.currentTimeMillis()-startTime) + " milliseconds.");
		
		// Perform action.
		return convertToMeerkatAction(config.getActionSelectionStrategy().select(root));
	}
	
	
	private void iterate(RootNode node) {
		// Selection until a leaf of the stored tree is reached.
		Node selectedNode = node.selectRecursively();
		
		// Expand selected leaf node (and select one of the new children).
		selectedNode.generateChildren();
		selectedNode = selectedNode.select();
		
		// Simulate a game.
		final double expectedValue = selectedNode.simulate();
		
		// Propagate changes.
		selectedNode.backpropagate(expectedValue);
	}
	
	
	private long getThinkingTime() {
		return THINKING_TIME;
	}
	
	
	private Action convertToMeerkatAction(mctsbot.actions.Action action) {
		final double toCall = gi.getAmountToCall(seat);
		
		// Raise.
		if(action instanceof mctsbot.actions.RaiseAction) return Action.raiseAction(gi);
		
		// Call.
		if(action instanceof mctsbot.actions.CallAction) return Action.callAction(toCall);
		
		// Fold.
		if(action instanceof mctsbot.actions.FoldAction) return Action.foldAction(toCall);
		
		// Else something went wrong.
		System.err.println("Received invalid action type: " + action.getClass().toString());
		return Action.checkOrFoldAction(toCall);
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
