package mctsbot;

import mctsbot.gamestate.GameState;
import mctsbot.nodes.Node;
import mctsbot.nodes.RootNode;
import mctsbot.strategies.DynamicDistributionSimulationStrategy;
import mctsbot.strategies.StrategyConfiguration;
import mctsbot.strategies.UCTSelectionStrategy;

import com.biotools.meerkat.Action;
import com.biotools.meerkat.Card;
import com.biotools.meerkat.GameInfo;
import com.biotools.meerkat.Player;
import com.biotools.meerkat.util.Preferences;

public class MCTSBot implements Player {
	
	private static final long THINKING_TIME = 500;
	
	private int seat;
	@SuppressWarnings("unused")
	private Card c1, c2;
	private GameInfo gi;
	@SuppressWarnings("unused")
	private Preferences prefs;
	
	protected GameState currentGameState;
	private StrategyConfiguration config;
	
	
	//TODO: remove all System.out.println's 
	
	@Override
	public void init(Preferences prefs) {
		//System.out.println("init called");
		
		this.prefs = prefs;
		
		// Create a new config.
		// TODO: create the config from the given preferences.
		config = StrategyConfiguration.getDefault();
		
		//System.out.println("init finished");
	}
	
	
	@Override
	public void gameStartEvent(GameInfo gi) {
		//System.out.println("gameStartEvent called");
		this.gi = gi;
		currentGameState = GameState.initialise(gi);
		//System.out.println("gameStartEvent finished");
	}
	
	
	@Override
	public void holeCards(Card c1, Card c2, int seat) {
		//System.out.println("holeCards called");
		
		this.c1 = c1;
		this.c2 = c2;
		this.seat = seat;
		
		currentGameState = currentGameState.holeCards(c1, c2, seat);
		
		//System.out.println("holeCards finished");
	}
	
	
	@Override
	public Action getAction() {
		//System.out.println("getAction called");
		
		

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
		
		System.out.println();
		System.out.println("Performed " + noIterations + " iterations in " + 
				(System.currentTimeMillis()-startTime) + " milliseconds.");
		
		
		// Perform action.
		final Action action = convertToMeerkatAction(config.getActionSelectionStrategy().select(root));
		
		System.out.println("explorationTally = " + UCTSelectionStrategy.explorationTally);
		System.out.println("exploitationTally = " + UCTSelectionStrategy.exploitationTally);
		System.out.println("exploration percentage = " + 
				(100*UCTSelectionStrategy.explorationTally/
						(UCTSelectionStrategy.explorationTally
								+UCTSelectionStrategy.exploitationTally)));
		
		UCTSelectionStrategy.exploitationTally = 0;
		UCTSelectionStrategy.explorationTally = 0;
		
		System.out.print("Fold Tally = ");
		for(int i=1; i<=4; i++) {
			System.out.print(DynamicDistributionSimulationStrategy.foldTally[i] + " ");
		}
		System.out.println();
		
		System.out.print("Call Tally = ");
		for(int i=1; i<=4; i++) {
			System.out.print(DynamicDistributionSimulationStrategy.callTally[i] + " ");
		}
		System.out.println();
		
		System.out.print("Raise Tally = ");
		for(int i=1; i<=4; i++) {
			System.out.print(DynamicDistributionSimulationStrategy.raiseTally[i] + " ");
		}
		System.out.println();
		
		
		
		return action;
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
		//System.out.println("convert called");
		//System.out.println("convert called on " + action.toString());
		
		final double toCall = gi.getAmountToCall(seat);
		
		// Raise.
		if(action instanceof mctsbot.actions.RaiseAction) return Action.raiseAction(gi);
		
		// Call.
		if(action instanceof mctsbot.actions.CallAction) return Action.callAction(toCall);
		
		// Fold.
		if(action instanceof mctsbot.actions.FoldAction) return Action.foldAction(toCall);
		
		// Else something went wrong.
		//System.err.println("Received invalid action type: " + action.toString());
		return Action.checkOrFoldAction(toCall);
	}
	
	
	
	
	
	@Override
	public void actionEvent(int seat, Action action) {
		//System.out.println("actionEvent called");
		
		if(action.isBetOrRaise()) {
			currentGameState = currentGameState.doAction(1);
			DynamicDistributionSimulationStrategy.actionHappened(
					currentGameState.getStage(), 1);
			
		} else if(action.isCheckOrCall()) {
			currentGameState = currentGameState.doAction(2);
			DynamicDistributionSimulationStrategy.actionHappened(
					currentGameState.getStage(), 2);
			
		} else if(action.isFold()) {
			currentGameState = currentGameState.doAction(3);
			DynamicDistributionSimulationStrategy.actionHappened(
					currentGameState.getStage(), 3);
			
		} else if(action.isSmallBlind()) {
			currentGameState = currentGameState.doSmallBlind(seat);
			
		} else if(action.isBigBlind()) {
			currentGameState = currentGameState.doBigBlind(seat);
			
		}
		
		//System.out.println("actionEvent finished");
	}
	
	@Override
	public void stageEvent(int stage) {
		//System.out.println("stageEvent called, stage = " + stage);
		
		if(stage>0) {
			currentGameState = currentGameState.setTable(gi.getBoard());
			currentGameState = currentGameState.goToNextStage();
		}
		
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
	public void gameStateChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showdownEvent(int arg0, Card arg1, Card arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void winEvent(int arg0, double arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

	

	

	

}
