package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import mctsbot.actions.BigBlindAction;
import mctsbot.actions.CallAction;
import mctsbot.actions.FoldAction;
import mctsbot.actions.RaiseAction;
import mctsbot.actions.SmallBlindAction;
import mctsbot.gamestate.GameState;

public class Exporter {
	
	private static final String DEFAULT_INPUT_FILE_LOCATION = 
		"S:\\My Dropbox\\CompSci Work\\Part II\\Project\\Hand Histories\\"
		+ "SBvMB-x3-2.txt";
	
	protected static final String DEFAULT_TEMPORARY_STORAGE_LOCATION = 
		HHConverter.DEFAULT_TEMPORARY_STORAGE_LOCATION;
	
	private static final String DEFAULT_OUTPUT_FOLDER_LOCATION = 
		"S:\\My Dropbox\\CompSci Work\\Part II\\Project\\Dissertation\\Graphs\\";

	private static final String FILE_EXTENSION = ".csv";
	
	private static final String FILE_NAME = "SBvMB-x3";

	private static final String DEFAULT_OUTPUT_FILE_LOCATION = 
		DEFAULT_OUTPUT_FOLDER_LOCATION + FILE_NAME + FILE_EXTENSION;
	
	private static final boolean APPEND = false;
	
	private static final char CURRENCY_SYMBOL = '$';
	
	@SuppressWarnings("unused")
	private static final String[] simpleBotAliases = {"SimpleBot", 
													  "Unknown", 
													  "LoggingSimpleBot", 
													  "LoggingSimpleBot1", 
													  "LoggingSimpleBot2"};
	private static final String[] MCTSBotAliases = {"MCTSBot"};
	private static final String[] playerAliases = MCTSBotAliases;
	
	private static final int NUM_TO_WRITE = 1000000;
	
	private static double m = 0;
	private static double s = 0;
	private static int n = 0;
	
	private static double m2 = 0;
	private static double m3 = 0;
	private static double m4 = 0;
	
//	private static double total = 0;
	
	
	
	public static void main(String[] args) throws Exception {
		
		convertHistoriesToGameRecords();
		
		BufferedWriter out = new BufferedWriter(
				new FileWriter(DEFAULT_OUTPUT_FILE_LOCATION, APPEND));
		
		ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(DEFAULT_TEMPORARY_STORAGE_LOCATION));
		
		if(!APPEND) writeHeader(out);
		
		final long startTime = System.currentTimeMillis();
		int numSuccesses = 0;
		
		GameRecord gameRecord = null;
		
		try {
			
			while((gameRecord=(GameRecord)ois.readObject())!=null) {
				
				for(int i=0; i<playerAliases.length; i++) {
					if(write(gameRecord, playerAliases[i], out))
						numSuccesses++;
				}
				if(numSuccesses>=NUM_TO_WRITE) break;
				
			}
	
		} catch(EOFException e) {
			System.out.println("End of file reached.");
			
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}

		out.close();
		ois.close();
		
		System.out.println(numSuccesses + " games successfully written.");
		System.out.println("Total time taken = " + (System.currentTimeMillis()-startTime) + "ms.");
		
		System.out.println();
		System.out.println("Count = " + n);
		System.out.println("Mean = " + m);
		System.out.println("StdDev = " + getStdDev());
		System.out.println("StdErr = " + (getStdDev()/Math.sqrt(n)));
		System.out.println("2*StdErr = " + 2.0*getStdDev()/Math.sqrt((double)n));
		
	}

	private static void update(double x) {
		n++;
		if(n==1) {
			m = x;
			s = 0;
		} else {
			m = m + (x-m)/n;
			s = s + (n/(n-1))*(m-x)*(m-x);
		}
		
	}
	
	private static double getStdDev() {
		return Math.sqrt(s/(n-1));
	}
	
	private static boolean write(GameRecord gameRecord, String name, BufferedWriter out) throws IOException {
		
		
		
		try {
			final double profit = gameRecord.getPlayer(name).getProfit();
		
			final double profit2 = gameRecord.getPlayer("SimpleBot1").getProfit();
			final double profit3 = gameRecord.getPlayer("SimpleBot2").getProfit();
			final double profit4 = gameRecord.getPlayer("SimpleBot3").getProfit();
			
	//		total += profit;
	//		update(total/((double) (n+1)));
			update(profit);
			
			m2 = m2 + (profit2-m2)/n;
			m3 = m3 + (profit3-m3)/n;
			m4 = m4 + (profit4-m4)/n;
			
//			final double conf = 2.0*getStdDev()/Math.sqrt((double)n);
			
//			if(n%10==0) out.write(String.format("%d,%f,%f,%f\r\n", n, m, m+conf, m-conf));
//			out.write(profit + "\r\n");
			out.write(String.format("%d,%f,%f,%f,%f\r\n", n, m, m2, m3, m4));
			
		} catch(Exception e) {
//			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static void writeHeader(BufferedWriter out) throws IOException {
//		out.write("\"Stage\";\"Profit\"\r");
	}
	
	
	
	
	public static void convertHistoriesToGameRecords() throws Exception {
		
		BufferedReader in = new BufferedReader(
				new FileReader(DEFAULT_INPUT_FILE_LOCATION));
		ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(DEFAULT_TEMPORARY_STORAGE_LOCATION));
		
		final long startTime = System.currentTimeMillis();
		
		int numErrors = 0;
		int numSuccesses = 0;
		
		String inputLine = in.readLine();
		
		while(numSuccesses<NUM_TO_WRITE && inputLine!=null) {
			
			try {
				
				final GameRecord gameRecord = new GameRecord();
				
				gameRecord.stage = GameState.PREFLOP;
				
				double inPot = 0;
				
				while(!inputLine.startsWith("***")) {
					
					// Player Declaration.
					if(inputLine.matches("\\s\\d\\)\\s\\S+\\s.+")) {
						
						final String name = inputLine.substring(4,inputLine.indexOf(' ', 5));
						final int seat = Integer.parseInt(inputLine.substring(1, 2));
						final boolean dealer = inputLine.matches("\\s\\d\\)\\s\\S+\\s\\*.+");
						final PlayerRecord player = new PlayerRecord(name, seat, dealer);
						player.setCards(inputLine.substring(inputLine.length()-5));
						gameRecord.addPlayer(player);
						inPot = 0;
						
					// Small Blind.
					} else if(inputLine.matches("\\S+ posts small blind .+")) {
						final double amount = Double.parseDouble(
								inputLine.substring(inputLine.indexOf(CURRENCY_SYMBOL)+1));
						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
						final PlayerRecord player = gameRecord.getPlayer(playerName);
						player.doAction(new SmallBlindAction(amount), gameRecord.stage);
						inPot = amount;
						player.setAmountInPot(amount);
						
					// Big Blind.
					} else if(inputLine.matches("\\S+ posts big blind .+")) {
						final double amount = Double.parseDouble(
								inputLine.substring(inputLine.indexOf(CURRENCY_SYMBOL)+1));
						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
						final PlayerRecord player = gameRecord.getPlayer(playerName);
						player.doAction(new BigBlindAction(amount), gameRecord.stage);
						inPot = amount;
						player.setAmountInPot(amount);
						
					// Check.
					} else if(inputLine.matches("\\S+ checks")){
						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
						final PlayerRecord player = gameRecord.getPlayer(playerName);
						player.doAction(new CallAction(0), gameRecord.stage);
						player.setAmountInPot(inPot);
						
					// Call.
					} else if(inputLine.matches("\\S+ calls .+")){
						final double amount = Double.parseDouble(
								inputLine.substring(inputLine.indexOf(CURRENCY_SYMBOL)+1));
						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
						final PlayerRecord player = gameRecord.getPlayer(playerName);
						player.doAction(new CallAction(amount), gameRecord.stage);
						player.setAmountInPot(inPot);
						
					// Bet or Raise.
					} else if(inputLine.matches("\\S+ (bets|raises) .+")){
						final double amount = Double.parseDouble(
								inputLine.substring(inputLine.indexOf(CURRENCY_SYMBOL)+1));
						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
						final PlayerRecord player = gameRecord.getPlayer(playerName);
						player.doAction(new RaiseAction(amount), gameRecord.stage);
						inPot += amount;
						player.setAmountInPot(inPot);
					
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
						
					// River.
					} else if(inputLine.matches("RIVER.+")){
						gameRecord.stage = GameState.RIVER;
						gameRecord.setTable(inputLine.substring(8));
//						gameRecord.setTableRank(HandEvaluator.rankHand(
//								gameRecord.getTable()));

					// Shows.
					} else if(inputLine.matches("\\S+ shows .+")){
						gameRecord.stage = GameState.SHOWDOWN;
//						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
//						final PlayerRecord player = gameRecord.getPlayer(playerName);
//						player.setHandRank(HandEvaluator.rankHand(
//								player.getC1(), player.getC2(), gameRecord.getTable()));
						inPot = 0;
						
					// Wins.
					} else if(inputLine.matches("\\S+ wins .+")){
						final String playerName = inputLine.substring(0, inputLine.indexOf(' '));
						final PlayerRecord player = gameRecord.getPlayer(playerName);
//						final double amount = Double.parseDouble(inputLine.substring(
//								inputLine.indexOf(CURRENCY_SYMBOL)+1, 
//								inputLine.indexOf(' ', inputLine.indexOf(CURRENCY_SYMBOL)+1)));
						double amountWon = 0;
						int numWinners = 1;
						for(PlayerRecord p: gameRecord.getPlayers()) {
							amountWon += p.getAmountInPot();
							if(p.getAmountWon()!=0) numWinners++;
						}
						
						player.setAmountWon(amountWon);
						
						if(numWinners!=1) {
							for(PlayerRecord p: gameRecord.getPlayers()) {
								if(p.getAmountWon()!=0) {
									p.setAmountWon(amountWon/numWinners);
								}
							}
						}
						inPot = 0;
					
						// Something Else.
					} else {
						// Do Nothing.
						
					}
					
					inputLine = in.readLine();
				}

				//gameRecord.print();
//				gameRecord.checkGame();
				
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
		
//		System.out.println();
		System.out.println(numSuccesses + " games successfully converted.");
		System.out.println(numErrors + " games caused errors.");
		System.out.println("Total time taken = " + (System.currentTimeMillis()-startTime) + "ms.");
		System.out.println();
	}

}
