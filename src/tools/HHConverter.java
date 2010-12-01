package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.biotools.meerkat.Card;

public class HHConverter {
	
	private static final String DEFAULT_INPUT_FILE_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\histories.txt";
	
	private static final String DEFAULT_OUTPUT_FILE_LOCATION = 
		"S:\\Workspace\\MCTSBot\\weka\\simplebotmodel.arff";
	
	private final String SIMPLEBOT = "SimpleBot";
	private final String MCTSBOT = "MCTSBot";

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
		 * 
		 * 
		 */
		
		String s = " 7) MCTSBot *       $992  5h 3h";
		
		System.out.println(s.substring(4,s.indexOf(' ', 5)));
		
		if(true) return;
		
		
		
		ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(DEFAULT_OUTPUT_FILE_LOCATION, true));
		
		BufferedReader in = new BufferedReader(new FileReader(DEFAULT_INPUT_FILE_LOCATION));
		//BufferedWriter out = new BufferedWriter(new FileWriter(DEFAULT_OUTPUT_FILE_LOCATION));
		
		int numErrors = 0;
		int numSuccesses = 0;
		
		String inputLine = "";
		
		while(true) {
			
			try {
				
				final GameRecord gameRecord = new GameRecord();
				
				
				while(!((inputLine = in.readLine()).startsWith("***"))) {
					
					// inputLine is a player declaration.
					if(inputLine.matches("\\s\\d\\)\\s\\S+\\s.+")) {
						
						final String name = inputLine.substring(4,inputLine.indexOf(' ', 5));
						final int seat = Integer.parseInt(inputLine.substring(1, 2));
						final boolean dealer = inputLine.matches("\\s\\d\\)\\s\\S+\\s\\*.+");
						
						gameRecord.addPlayer(new PlayerRecord(name, seat, dealer));
						
					} else if(inputLine.startsWith("SimpleBot posts small blind")) {
						// Do Nothing.
					} else if(inputLine.startsWith("SimpleBot posts big blind")) {
						// Do Nothing.
					} else if(inputLine.startsWith("SimpleBot calls") || 
							inputLine.startsWith("SimpleBot checks")) {
						
						
						
						
					} else if(inputLine.startsWith("SimpleBot raises") || 
							inputLine.startsWith("SimpleBot bets")) {
						
						
						
						
					} else if(inputLine.startsWith("SimpleBot folds")) {
						
						
						
						
					} else {
						
					}
				}
				// Write to file.
				
				
				break;
				
				
			} catch(Exception e) {
				numErrors++;
				continue;
			}
			
			
			
		}

		System.out.println(numSuccesses + " games successfully converted.");
		System.out.println(numErrors + " games caused errors.");
		//TODO: add more useful information here.
		
	}

}




