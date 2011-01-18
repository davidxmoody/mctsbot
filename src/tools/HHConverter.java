package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import mctsbot.actions.Action;
import mctsbot.actions.BigBlindAction;
import mctsbot.actions.CallAction;
import mctsbot.actions.FoldAction;
import mctsbot.actions.RaiseAction;
import mctsbot.actions.SmallBlindAction;
import mctsbot.gamestate.GameState;

import com.biotools.meerkat.Card;
import com.biotools.meerkat.Hand;
import com.biotools.meerkat.HandEvaluator;

public class HHConverter {
	
	private static final String DEFAULT_INPUT_FILE_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\histories.txt";
	
	private static final String DEFAULT_OUTPUT_FILE_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\output.arff";
	
	private static final char CURRENCY_SYMBOL = '$';
	
	private static final boolean APPEND = false;
	
	private static final WekaFormat format = new SimpleBotHROMWekaFormat3();
	
	/**
	 * When run, this method will go through the entire file given to it and 
	 * convert it to 
	 * 
	 * @param args 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		//TODO: Make program use arguments for file locations.
		
		/*
		 * ASSUMPTIONS:
		 * there are only 2 players               X
		 * MCTSBot is always called MCTSBot       X
		 * SimpleBot is always called SimpleBot   X
		 * blinds are always 0.50 and 1.00        X
		 * bet sizes are always 1.00 and 2.00     X
		 * 
		 * end of a game is always followed by a series of *'s
		 * seat numbers are always a single digit
		 * player name shave no spaces in them
		 * 
		 * 
		 */
		
		
		BufferedReader in = new BufferedReader(
				new FileReader(DEFAULT_INPUT_FILE_LOCATION));
		BufferedWriter out = new BufferedWriter(
				new FileWriter(DEFAULT_OUTPUT_FILE_LOCATION, APPEND));
		
		if(!APPEND) format.writeHeader(out);
		
		final long startTime = System.currentTimeMillis();
		
		int numErrors = 0;
		int numSuccesses = 0;
		
		String inputLine = in.readLine();
		
		// TODO: fix problem where the amount in the actions is wrong
		
		while(inputLine!=null) {
			
			try {
				
				final GameRecord gameRecord = new GameRecord();
				
				gameRecord.stage = GameState.PREFLOP;
				
				while(!inputLine.startsWith("***")) {
					
					//System.out.println("-" + inputLine);
					
					// Player Declaration.
					if(inputLine.matches("\\s\\d\\)\\s\\S+\\s.+")) {
						
						final String name = inputLine.substring(4,inputLine.indexOf(' ', 5));
						final int seat = Integer.parseInt(inputLine.substring(1, 2));
						final boolean dealer = inputLine.matches("\\s\\d\\)\\s\\S+\\s\\*.+");
						final PlayerRecord player = new PlayerRecord(name, seat, dealer);
						player.setCards(inputLine.substring(inputLine.length()-5));
						gameRecord.addPlayer(player);
						
					// Small Blind.
					} else if(inputLine.matches("\\S+ posts small blind .+")) {
						final double amount = Double.parseDouble(
								inputLine.substring(inputLine.indexOf(CURRENCY_SYMBOL)+1));
						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
						final PlayerRecord player = gameRecord.getPlayer(playerName);
						player.doAction(new SmallBlindAction(amount), gameRecord.stage);
						
					// Big Blind.
					} else if(inputLine.matches("\\S+ posts big blind .+")) {
						final double amount = Double.parseDouble(
								inputLine.substring(inputLine.indexOf(CURRENCY_SYMBOL)+1));
						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
						final PlayerRecord player = gameRecord.getPlayer(playerName);
						player.doAction(new BigBlindAction(amount), gameRecord.stage);
						
					// Check.
					} else if(inputLine.matches("\\S+ checks")){
						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
						final PlayerRecord player = gameRecord.getPlayer(playerName);
						player.doAction(new CallAction(0), gameRecord.stage);
						
					// Call.
					} else if(inputLine.matches("\\S+ calls .+")){
						final double amount = Double.parseDouble(
								inputLine.substring(inputLine.indexOf(CURRENCY_SYMBOL)+1));
						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
						final PlayerRecord player = gameRecord.getPlayer(playerName);
						player.doAction(new CallAction(amount), gameRecord.stage);
						
					// Raise or Bet.
					} else if(inputLine.matches("\\S+ (bets|raises) .+")){
						final double amount = Double.parseDouble(
								inputLine.substring(inputLine.indexOf(CURRENCY_SYMBOL)+1));
						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
						final PlayerRecord player = gameRecord.getPlayer(playerName);
						player.doAction(new RaiseAction(amount), gameRecord.stage);
						
					// Fold.
					} else if(inputLine.matches("\\S+ folds")){
						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
						final PlayerRecord player = gameRecord.getPlayer(playerName);
						player.doAction(new FoldAction(), gameRecord.stage);
						
					// Flop.
					} else if(inputLine.matches("FLOP.+")){
						gameRecord.stage = GameState.FLOP;
						gameRecord.setTable(inputLine.substring(7));
						
					// Turn.
					} else if(inputLine.matches("TURN.+")){
						gameRecord.stage = GameState.TURN;
						gameRecord.setTable(inputLine.substring(7));
						
					// Flop.
					} else if(inputLine.matches("RIVER.+")){
						gameRecord.stage = GameState.RIVER;
						gameRecord.setTable(inputLine.substring(8));

					// Shows.
					} else if(inputLine.matches("\\S+ shows .+")){
						gameRecord.stage = GameState.SHOWDOWN;
						
						
					// Something Else.
					} else {
						// Do Nothing.
						
					}
					
					inputLine = in.readLine();
				}

				//gameRecord.print();
				gameRecord.checkGame();
				
				
				format.write(gameRecord, out);
				
				numSuccesses++;
				inputLine = in.readLine();
				
			} catch(Exception e) {
				e.printStackTrace();
				numErrors++;
				
				// Skip to the next game.
				while(!inputLine.startsWith("***")) inputLine = in.readLine();
				inputLine = in.readLine();
				
				continue;
			}
			
		}
		
		in.close();
		out.close();
		
		System.out.println();
		System.out.println(numSuccesses + " games successfully converted.");
		System.out.println(numErrors + " games caused errors.");
		System.out.println("Total time taken = " + (System.currentTimeMillis()-startTime) + "ms.");
		System.out.println();
	}

}




