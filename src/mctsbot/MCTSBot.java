package mctsbot;

import mctsbot.gamestate.GameState;
import mctsbot.nodes.Node;
import mctsbot.nodes.RootNode;
import mctsbot.strategies.StrategyConfiguration;

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
		
		
		/*
		// Just testing how rank hand works.
		Deck d = new Deck();
		Card c1 = d.extractRandomCard();
		Card c2 = d.extractRandomCard();
		Card h1 = d.extractRandomCard();
		Card h2 = d.extractRandomCard();
		Card h3 = d.extractRandomCard();
		
		Hand h = new Hand();
		h.addCard(h1);
		h.addCard(h2);
		h.addCard(h3);
		
		int rank = HandEvaluator.rankHand(c1, c2, h);
		
		System.out.println("C1 = " + c1.getRank() + " " + c1.getSuit());
		System.out.println("C2 = " + c2.getRank() + " " + c2.getSuit());
		System.out.println("H1 = " + h1.getRank() + " " + h1.getSuit());
		System.out.println("H2 = " + h2.getRank() + " " + h2.getSuit());
		System.out.println("H3 = " + h3.getRank() + " " + h3.getSuit());
		System.out.println("RANK = " + rank);
		System.out.println("");
		
		if(true) throw new RuntimeException();*/
		
		

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
		
		//System.out.println("getAction about to finish");
		
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
		} else if(action.isCheckOrCall()) {
			currentGameState = currentGameState.doAction(2);
		} else if(action.isFold()) {
			currentGameState = currentGameState.doAction(3);
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
