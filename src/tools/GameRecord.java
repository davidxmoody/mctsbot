package tools;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import mctsbot.gamestate.GameState;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.Hand;

public class GameRecord implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected int stage = -1;
	private int[] tableCardIndexes = new int[0];
	private List<PlayerRecord> players = new LinkedList<PlayerRecord>();

	private int tableRank = -1;

	protected GameRecord() { }
	
	//TODO
	/*public GameRecord(GameState gameState) {
	 	
		// First check to see whether gameState represents an ended game.
		if(!(gameState.getStage()==GameState.SHOWDOWN || gameState.getNoOfActivePlayers()<2)) 
			throw new RuntimeException("");
		
		// Set variables in GameRecord.
		stage = gameState.getStage();
		
		// Create the players
		
	}*/
	
	protected void setTableRank(int rank) {
		this.tableRank = rank;
	}
	
	protected int getTableRank() {
		return tableRank;
	}
	
	protected void setStageReached(int stageReached) {
		this.stage = stageReached;
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
		if(!(tableSize==0 || tableSize==3 || tableSize==4 || tableSize==5)) 
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
		throw new RuntimeException("Player: " + name + " is not in this game.");
	}
	
	public int getStageReached() {
		return stage;
	}
	
	public boolean endedInShowdown() {
		return stage == GameState.SHOWDOWN;
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
	
	
	public void print() {
		System.out.println("Stage reached = " + stage + " (" + 
			((stage==GameState.PREFLOP)?"PREFLOP":
				(stage==GameState.FLOP)?"FLOP":
					(stage==GameState.TURN)?"TURN":
						(stage==GameState.RIVER)?"RIVER":
							(stage==GameState.SHOWDOWN)?"SHOWDOWN":
								"UNKNOWN_" + stage) + ")");
		
		System.out.print("Table size = " + tableCardIndexes.length + " (");
		for(int i=0; i<tableCardIndexes.length; i++) 
			System.out.print((i==0?"":" ") + 
					Card.getRankChar(Card.getRank(tableCardIndexes[i])) + 
					Card.getSuitChar(Card.getSuit(tableCardIndexes[i])));
		System.out.println(")");
		
		System.out.println("Number of players = " + players.size());
		for(PlayerRecord player: players) player.print();
		
		System.out.println();
	}
	
	public void checkGame() throws Exception {
		
		if(stage==GameState.PREFLOP) {
			if(tableCardIndexes.length!=0) throw new Exception(
					"wrong number of table cards for the current stage (preflop): " 
						+ tableCardIndexes.length);
			
		} else if(stage==GameState.FLOP) {
			if(tableCardIndexes.length!=3) throw new Exception(
				"wrong number of table cards for the current stage (flop): " 
					+ tableCardIndexes.length);
			
		} else if(stage==GameState.TURN) {
			if(tableCardIndexes.length!=4) throw new Exception(
					"wrong number of table cards for the current stage (turn): " 
						+ tableCardIndexes.length);
			
		} else if(stage==GameState.RIVER) {
			if(tableCardIndexes.length!=5) throw new Exception(
					"wrong number of table cards for the current stage (river): " 
						+ tableCardIndexes.length);
			
		} else if(stage==GameState.SHOWDOWN) {
			if(tableCardIndexes.length!=5) throw new Exception(
					"wrong number of table cards for the current stage (showdown): " 
						+ tableCardIndexes.length);
			
		} else {
			throw new Exception("invalid stage: " + stage);
		}
		
		if(players.size()<2) throw new Exception("too few players: " + players.size());
		if(players.size()>12) throw new Exception("too many players: " + players.size());
		
		for(int i=0; i<players.size(); i++) {
			for(int j=i+1; j<players.size(); j++) {
				if(players.get(i).getName().equals(players.get(j).getName())) {
					throw new Exception("two players have the same name: " + 
							players.get(i).getName() + " and " + players.get(j).getName());
				}
			}
		}
		
		// TODO: write more tests
		
	}
	
	

}
