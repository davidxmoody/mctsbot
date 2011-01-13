package tools;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;

import mctsbot.actions.BigBlindAction;
import mctsbot.actions.CallAction;
import mctsbot.actions.FoldAction;
import mctsbot.actions.RaiseAction;
import mctsbot.actions.SmallBlindAction;
import mctsbot.gamestate.GameState;

public class HHConverter {
	
	private static final String DEFAULT_INPUT_FILE_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\test.txt";
	
	private static final String DEFAULT_OUTPUT_FILE_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\output.txt";
	
	private static final String SIMPLEBOT = "SimpleBot";
	private static final String MCTSBOT = "MCTSBot";
	private static final char CURRENCY_SYMBOL = '$';
	
	private static double smallBlindSize = 0.5;
	private static double bigBlindSize = 1.0;

	/**
	 * When run, this method will go through the entire file given to it and 
	 * convert it to 
	 * 
	 * @param args 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		//TODO: Make program use arguments for file locations.
		
		/*
		 * ASSUMPTIONS:
		 * there are only 2 players
		 * MCTSBot is always called MCTSBot
		 * SimpleBot is always called SimpleBot
		 * blinds are always 0.50 and 1.00
		 * bet sizes are always 1.00 and 2.00
		 * 
		 * end of a game is always followed by a series of *'s
		 * seat numbers are always a single digit
		 * player name shave no spaces in them
		 * 
		 * 
		 */
		
		/*String s = "MCTSBot posts small blind $0.50";
		System.out.println(s.matches("\\S+ posts small blind .+"));
		if(true) return;*/
		
		
		
		
		ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(DEFAULT_OUTPUT_FILE_LOCATION, true));
		
		BufferedReader in = new BufferedReader(new FileReader(DEFAULT_INPUT_FILE_LOCATION));
		//BufferedWriter out = new BufferedWriter(new FileWriter(DEFAULT_OUTPUT_FILE_LOCATION));
		
		int numErrors = 0;
		int numSuccesses = 0;
		
		String inputLine = "";
		
		while(inputLine!=null) {
			
			try {
				
				final GameRecord gameRecord = new GameRecord();
				
				int stage = GameState.PREFLOP;
				
				
				while(!((inputLine = in.readLine()).startsWith("***"))) {
					
					System.out.println("-" + inputLine);
					
					// Player Declaration.
					if(inputLine.matches("\\s\\d\\)\\s\\S+\\s.+")) {
						
						final String name = inputLine.substring(4,inputLine.indexOf(' ', 5));
						final int seat = Integer.parseInt(inputLine.substring(1, 2));
						final boolean dealer = inputLine.matches("\\s\\d\\)\\s\\S+\\s\\*.+");
						final PlayerRecord player = new PlayerRecord(name, seat, dealer);
						player.setCards(inputLine.substring(inputLine.length()-5));
						gameRecord.addPlayer(player);
						
						//System.out.println("# of players = " + gameRecord.getPlayers().size());
						//for(PlayerRecord p: gameRecord.getPlayers()) {
						//	System.out.println(p.getName());
						//}
						
						//System.out.println(name + seat + dealer);
						
					// Small Blind.
					} else if(inputLine.matches("\\S+ posts small blind .+")) {
						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
						final PlayerRecord player = gameRecord.getPlayer(playerName);
						player.doAction(new SmallBlindAction(smallBlindSize), stage);
						
					// Big Blind.
					} else if(inputLine.matches("\\S+ posts big blind .+")) {
						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
						final PlayerRecord player = gameRecord.getPlayer(playerName);
						player.doAction(new BigBlindAction(bigBlindSize), stage);
						
					// Check.
					} else if(inputLine.matches("\\S+ checks")){
						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
						final PlayerRecord player = gameRecord.getPlayer(playerName);
						player.doAction(new CallAction(0), stage);
						
					// Call.
					} else if(inputLine.matches("\\S+ calls .+")){
						final double amount = Double.parseDouble(
								inputLine.substring(inputLine.indexOf(CURRENCY_SYMBOL)+1));
						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
						final PlayerRecord player = gameRecord.getPlayer(playerName);
						player.doAction(new CallAction(amount), stage);
						
					// Raise or Bet.
					} else if(inputLine.matches("\\S+ (bets|raises) .+")){
						final double amount = Double.parseDouble(
								inputLine.substring(inputLine.indexOf(CURRENCY_SYMBOL)+1));
						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
						final PlayerRecord player = gameRecord.getPlayer(playerName);
						player.doAction(new RaiseAction(amount), stage);
						
					// Fold.
					} else if(inputLine.matches("\\S+ folds")){
						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
						final PlayerRecord player = gameRecord.getPlayer(playerName);
						player.doAction(new FoldAction(), stage);
						
					// Flop.
					} else if(inputLine.matches("FLOP.+")){
						stage = GameState.FLOP;
						gameRecord.setTable(inputLine.substring(7));
						
					// Turn.
					} else if(inputLine.matches("TURN.+")){
						stage = GameState.TURN;
						gameRecord.setTable(inputLine.substring(7));
						
					// Flop.
					} else if(inputLine.matches("RIVER.+")){
						stage = GameState.RIVER;
						gameRecord.setTable(inputLine.substring(8));

					// Shows.
					} else if(inputLine.matches("\\S+ shows .+")){
						stage = GameState.SHOWDOWN;
						
						
					// Something Else.
					} else {
						// Do Nothing.
						
					}
				}
				
				gameRecord.setStageReached(stage);
				
				// TODO: write to file here.
				System.out.println("# of players = " + gameRecord.getPlayers().size());
				for(PlayerRecord p: gameRecord.getPlayers()) {
					System.out.println(p.getName());
				}

				gameRecord.print();
				
				return;
				
				
				
			} catch(Exception e) {
				e.printStackTrace();
				numErrors++;
				continue;
				//TODO: fix this by skipping to the next game
			}
			
			
			
		}
		
		System.out.println();
		System.out.println(numSuccesses + " games successfully converted.");
		System.out.println(numErrors + " games caused errors.");
		//TODO: add more useful information here.
		
	}

}




