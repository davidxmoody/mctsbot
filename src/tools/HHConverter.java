package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;

import com.biotools.meerkat.HandEvaluator;

import mctsbot.actions.BigBlindAction;
import mctsbot.actions.CallAction;
import mctsbot.actions.FoldAction;
import mctsbot.actions.RaiseAction;
import mctsbot.actions.SmallBlindAction;
import mctsbot.gamestate.GameState;
import mctsbot.opponentmodel.SimpleWekaHandRankOpponentModel;

public class HHConverter {
	
	private static final String DEFAULT_INPUT_FILE_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\histories2.txt";
	
	private static final String DEFAULT_OUTPUT_FILE_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\output.arff";
	
	private static final String DEFAULT_TEMPORARY_STORAGE_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\serializedGameRecords.txt";
	
	private static final String DEFAULT_ARFF_HEADER_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\template.arff";
	
	private static final String[] simpleBotAliases = {"SimpleBot", 
													  "Unknown", 
													  "LoggingSimpleBot", 
													  "LoggingSimpleBot1", 
													  "LoggingSimpleBot2"};
	
	
	private static final char CURRENCY_SYMBOL = '$';
	
	private static final boolean APPEND = false;
	
	private static final WekaFormat format = new SBHROMWekaFormat(null);
	
	// Set this to true if you want to run the convertHistoriesToGameRecords 
	// method when running MCTSBot in PA Pro.
	public static final boolean CONVERT = false;
	
	/**
	 * When run, this method will go through the entire file given to it and 
	 * convert it to 
	 * 
	 * @param args 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		System.out.println("Starting...");
		
		BufferedWriter out = new BufferedWriter(
				new FileWriter(DEFAULT_OUTPUT_FILE_LOCATION, APPEND));
		
		ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(DEFAULT_TEMPORARY_STORAGE_LOCATION));
		
		if(!APPEND) format.writeHeader(out);
		
		final long startTime = System.currentTimeMillis();
		
		int numSuccesses = 0;
		
		GameRecord gameRecord = null;
		
		try {
			
			while((gameRecord=(GameRecord)ois.readObject())!=null) {
				gameRecord.checkGame();
				
				for(int i=0; i<simpleBotAliases.length; i++) {
					format.write(gameRecord, simpleBotAliases[i], out);
					numSuccesses++;
				}
				
			}
	
		} catch(EOFException e) {
			// Do Nothing.
			
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}

		out.close();
		ois.close();
		
		// Write The Template.
		out = new BufferedWriter(
				new FileWriter(DEFAULT_ARFF_HEADER_LOCATION));
		format.writeHeader(out);
		out.close();
		
		System.out.println(numSuccesses + " games successfully converted.");
		System.out.println("Total time taken = " + (System.currentTimeMillis()-startTime) + "ms.");
		
		// Launch Weka on output.arff
		launchWeka();
		
		// Rebuild Classifier.
		SimpleWekaHandRankOpponentModel hrom = new SimpleWekaHandRankOpponentModel();
		hrom.rebuildClassifier();
	}
	
	private static void launchWeka() {
		try {
			Class<?> desktopClass = ClassLoader.getSystemClassLoader().loadClass("java.awt.Desktop");
			Method getDesktop = desktopClass.getMethod("getDesktop", (Class[])null);
			Object desktop = getDesktop.invoke((Object[])null, (Object[])null);
			Method open = desktopClass.getMethod("open", new Class[] {File.class});
			File file = new File(DEFAULT_OUTPUT_FILE_LOCATION);
			open.invoke(desktop, file);
			System.out.println("Launching Weka...");
		} catch(Exception e) {
			System.out.println("Could not launch Weka: ");
			e.printStackTrace();
		}
	}
	
	
	public static void convertHistoriesToGameRecords() throws Exception {

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
		ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(DEFAULT_TEMPORARY_STORAGE_LOCATION));
		
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
						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
						final PlayerRecord player = gameRecord.getPlayer(playerName);
						player.setHandRank(HandEvaluator.rankHand(
								player.getC1(), player.getC2(), gameRecord.getTable()));
						
					// Something Else.
					} else {
						// Do Nothing.
						
					}
					
					inputLine = in.readLine();
				}

				//gameRecord.print();
				gameRecord.checkGame();
				
				
				oos.writeObject(gameRecord);
				
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
		oos.close();
		
		System.out.println();
		System.out.println(numSuccesses + " games successfully converted.");
		System.out.println(numErrors + " games caused errors.");
		System.out.println("Total time taken = " + (System.currentTimeMillis()-startTime) + "ms.");
		System.out.println();
	}

}




