package tools;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.biotools.meerkat.Card;

import mctsbot.actions.Action;
import mctsbot.gamestate.GameState;

public class PlayerRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name = "NoName";
	private int seat;
	private boolean dealer = false;
	
	private int card1Index = -1;
	private int card2Index = -1;
	
	private List<Action> preflopActions = null;
	private List<Action> flopActions = null;
	private List<Action> turnActions = null;
	private List<Action> riverActions = null;
	
	protected PlayerRecord(String name, int seat, boolean dealer) {
		this.name = name;
		this.seat = seat;
		this.dealer = dealer;
	}
	
	protected void setCards(int card1Index, int card2Index) {
		this.card1Index = card1Index;
		this.card2Index = card2Index;
	}
	
	protected void setCards(Card card1, Card card2) {
		this.card1Index = card1.getIndex();
		this.card2Index = card2.getIndex();
	}
	
	public Card getC1() {
		if(card1Index==-1) return null;
		else return new Card(card1Index);
	}
	
	public Card getC2() {
		if(card2Index==-1) return null;
		else return new Card(card2Index);
	}
	
	protected void doAction(Action action, int stage) {
		switch (stage) {
		case GameState.PREFLOP:
			if(preflopActions==null) preflopActions = new LinkedList<Action>();
			preflopActions.add(action);
		case GameState.FLOP:
			if(flopActions==null) flopActions = new LinkedList<Action>();
			flopActions.add(action);
		case GameState.TURN:
			if(turnActions==null) turnActions = new LinkedList<Action>();
			turnActions.add(action);
		case GameState.RIVER:
			if(riverActions==null) riverActions = new LinkedList<Action>();
			riverActions.add(action);
		default:
			throw new RuntimeException("doAction called with invalid stage: " + stage);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public double getSeat() {
		return seat;
	}
	
	public List<Action> getPreflopActions() {
		return preflopActions;
	}
	
	public List<Action> getFlopActions() {
		return flopActions;
	}
	
	public List<Action> getTurnActions() {
		return turnActions;
	}
	
	public List<Action> getRiverActions() {
		return riverActions;
	}
	
	public boolean isDealer() {
		return dealer;
	}
	
	public List<Action> getActions(int stage) {
		switch (stage) {
		case GameState.PREFLOP:
			return getPreflopActions();
		case GameState.FLOP:
			return getFlopActions();
		case GameState.TURN:
			return getTurnActions();
		case GameState.RIVER:
			return getRiverActions();
		default:
			throw new RuntimeException("getActions called with invalid stage: " + stage);
		}
	}
	
	
	
}
