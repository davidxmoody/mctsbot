package tools;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import mctsbot.gamestate.GameState;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.Hand;

public class GameRecord implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int stageReached = 0;
	private int[] tableCardIndexes = null;
	private List<PlayerRecord> players = new LinkedList<PlayerRecord>();

	protected GameRecord() { }
	
	public GameRecord(GameState gameState) {
		//TODO
		
		// First check to see whether gameState represents an ended game.
		if(!(gameState.getStage()==GameState.SHOWDOWN || gameState.getNoOfActivePlayers()<2)) 
			throw new RuntimeException("");
		
		// Set variables in GameRecord.
		stageReached = gameState.getStage();
		
		// Create the players
		
	}
	
	protected void setStageReached(int stageReached) {
		this.stageReached = stageReached;
	}
	
	protected void setTable(int[] tableCardIndexes) {
		this.tableCardIndexes = tableCardIndexes;
	}
	
	protected void setTable(String tableDescription) {
		final String[] cardDescriptions = tableDescription.split(" ");
		final Hand table = new Hand();
		
		for(int i=0; i<cardDescriptions.length; i++) {
			if(cardDescriptions[i].length()!=2) continue;
			table.addCard(new Card(cardDescriptions[i]));
		}
		
		table.clearBadCards();
		final int tableSize = table.size();
		if((tableSize==0 || tableSize==3 || tableSize==4 || tableSize==5)) 
			throw new RuntimeException(
					"invalid String passed to setTable: " + tableDescription);
		
		setTable(table);
	}
	
	protected void setTable(Hand table) {
		tableCardIndexes = new int[table.size()];
		for(int i=0; i<table.size(); i++) 
			tableCardIndexes[i] = table.getCard(i+1).getIndex();
	}
	
	protected void addPlayer(PlayerRecord player) {
		players.add(player);
	}
	
	public PlayerRecord getPlayer(String name) {
		for(PlayerRecord player: players) 
			if(player.getName().equals(name)) return player;
		return null;
	}
	
	public int getStageReached() {
		return stageReached;
	}
	
	public boolean endedInShowdown() {
		return stageReached == GameState.SHOWDOWN;
	}
	
	public Hand getTable() {
		final Hand table = new Hand();
		for(int i=0; i<tableCardIndexes.length; i++) 
			table.addCard(new Card(tableCardIndexes[i]));
		return table;
	}
	
	public List<PlayerRecord> getPlayers() {
		return players;
	}
	
	
	
	

}
