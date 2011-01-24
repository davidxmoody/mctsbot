package mctsbot;

import mctsbot.gamestate.GameState;
import mctsbot.nodes.LeafNode;
import mctsbot.nodes.Node;
import mctsbot.nodes.RootNode;
import mctsbot.strategies.selection.UCTSelectionStrategy;
import mctsbot.strategies.simulation.DynamicDistributionSimulationStrategy;
import tools.HHConverter;

import com.biotools.meerkat.Action;
import com.biotools.meerkat.Card;
import com.biotools.meerkat.GameInfo;
import com.biotools.meerkat.Player;
import com.biotools.meerkat.util.Preferences;

public class MCTSBot implements Player {
	
	private static final long THINKING_TIME = 500;
	
	private int seat;
	private GameInfo gi;
	
	protected GameState currentGameState;
	private Config config;
	
	private static boolean convert = HHConverter.CONVERT;
	
	
	public void init(Preferences prefs) {
		setConfig(new Config(prefs));
	}
	
	
	public void setConfig(Config config) {
		this.config = config;
	}
	
	public void setCurrentGameState(GameState currentGameState) {
		this.currentGameState = currentGameState;
	}
	
	
	public void gameStartEvent(GameInfo gi) {
		this.gi = gi;
		currentGameState = GameState.initialise(gi);
	}
	
	
	public void holeCards(Card c1, Card c2, int seat) {
		this.seat = seat;
		currentGameState = currentGameState.holeCards(c1, c2, seat);
	}
	
	
	
	double[] C = new double [10];
	
	double[] total = new double [10];
	
	int[] trials = new int [10];
	
	//TODO: rename
	public Action getAction2() {
		UCTSelectionStrategy.C = 0.5;
		for(int i=0; i<total.length; i++) {

			getAction2();
			
			C[i] = UCTSelectionStrategy.C;
			
			UCTSelectionStrategy.C += 0.5;
			
			total[i] += (100*UCTSelectionStrategy.explorationTally/
					(UCTSelectionStrategy.explorationTally
					+UCTSelectionStrategy.exploitationTally));
				
			trials[i]++;

			
			// Debugging stuff.

			//System.out.println("C = " + UCTSelectionStrategy.C);
			//System.out.println("explorationTally = " + UCTSelectionStrategy.explorationTally);
			//System.out.println("exploitationTally = " + UCTSelectionStrategy.exploitationTally);
//			System.out.println("exploration percentage = " + 
//					(100*UCTSelectionStrategy.explorationTally/
//							(UCTSelectionStrategy.explorationTally
//									+UCTSelectionStrategy.exploitationTally)));
			
			
			
			
			
			
			UCTSelectionStrategy.exploitationTally = 0;
			UCTSelectionStrategy.explorationTally = 0;
			
			/*System.out.print("Fold Tally = ");
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
			System.out.println();*/
			
			
			
			
			
			
			
		}
		if(trials[0]==5) {
			for(int i=0; i<total.length; i++) {
				System.out.println("C: " + C[i] + ", expPercent: " + (total[i]/trials[i]));
			}
			System.out.println();
		}
		return null;
	}
	
	
	
	
	public Action getAction() {
		
		// Perform conversion on histories.txt if the flag is set.
		if(convert) {
			try {
				HHConverter.convertHistoriesToGameRecords();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Conversion Caused An Error");
				return null;
			}
		}

		// Make root node.
		RootNode root = new RootNode(currentGameState, config);
		
		// Do iterations until time limit reached.
		performIterations(root, getThinkingTime());
		
		// Select the (Meerkat) Action to perform.
		final Action action = convertToMeerkatAction(
				config.getActionSelectionStrategy().select(root));
		
		//TODO: return a check action instead of fold if possible
		
		// Debugging stuff.

//		System.out.println("C = " + UCTSelectionStrategy.C);
//		System.out.println("explorationTally = " + UCTSelectionStrategy.explorationTally);
//		System.out.println("exploitationTally = " + UCTSelectionStrategy.exploitationTally);
		System.out.println("exploration percentage = " + 
				(100*UCTSelectionStrategy.explorationTally/
						(UCTSelectionStrategy.explorationTally
								+UCTSelectionStrategy.exploitationTally)));
		System.out.println();
		
		int maxDepth = 10;
		Node current = root;
		
		for(int i=0; i<maxDepth; i++) {
			System.out.println("Depth = " + i);
			current.printDetails();
			current.printChildrensDetails();
			if(current instanceof LeafNode) break;
			if(current.getChildren()==null) break;
			current = current.getConfig().getSelectionStrategy().select(current);
			System.out.println();
		}
		
		
		
		
		
		
		System.out.println();
		
		// Return the (Meerkat) Action.
		return action;
	}
	
	
	public void performIterations(RootNode root, long thinkingTime) {
		
		final long startTime = System.currentTimeMillis();
		final long endTime = startTime + thinkingTime;
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
		
//		System.out.println();
//		System.out.println("Performed " + noIterations + " iterations in " + 
//				(System.currentTimeMillis()-startTime) + " milliseconds.");
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
		throw new RuntimeException("Invalid action type passed to convertToMeerkatAction");
	}
	
	
	
	public void actionEvent(int seat, Action action) {
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
	}
	
	public void stageEvent(int stage) {
		if(stage>0) {
			currentGameState = currentGameState.setTable(gi.getBoard());
			currentGameState = currentGameState.goToNextStage();
		}
		
	}
	
	
	
	
	
	
	public void dealHoleCardsEvent() {
		
	}

	public void gameOverEvent() {
		
	}


	public void gameStateChanged() {
		
	}

	public void showdownEvent(int arg0, Card arg1, Card arg2) {
		
	}

	public void winEvent(int arg0, double arg1, String arg2) {
		
	}

	

	

	

}
