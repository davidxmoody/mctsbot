package tools;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Exporter {
	
	protected static final String DEFAULT_TEMPORARY_STORAGE_LOCATION = 
		HHConverter.DEFAULT_TEMPORARY_STORAGE_LOCATION;
	
	private static final String DEFAULT_OUTPUT_FOLDER_LOCATION = 
		"S:\\My Dropbox\\CompSci Work\\Part II\\Project\\Dissertation\\Graphs\\";

	private static final String FILE_EXTENSION = ".csv";
	
	private static final String FILE_NAME = "mctsbot1";

	private static final String DEFAULT_OUTPUT_FILE_LOCATION = 
		DEFAULT_OUTPUT_FOLDER_LOCATION + FILE_NAME + FILE_EXTENSION;
	
	private static final boolean APPEND = false;

	@SuppressWarnings("unused")
	private static final String[] simpleBotAliases = {"SimpleBot", 
													  "Unknown", 
													  "LoggingSimpleBot", 
													  "LoggingSimpleBot1", 
													  "LoggingSimpleBot2"};
	
	private static final String[] playerAliases = {"MCTSBot"};

	private static final int NUM_TO_WRITE = 1000000000;
	
	
	
	
	
	public static void main(String[] args) throws Exception {

//		System.out.println("Starting...");
		
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

	}





	private static boolean write(GameRecord gameRecord, String name, BufferedWriter out) throws IOException {
		try {
			out.write(//gameRecord.getStageReached() + ";" + 
					gameRecord.getPlayer(name).getProfit() + "\r");
			
//			if(index<=20) System.out.println(index + ";" + gameRecord.getStageReached() + 
//					";" + gameRecord.getPlayer(name).getProfit());
		} catch(Exception e) {
			return false;
		}
		return true;
				
//		System.out.print(index + //"," + gameRecord.getStageReached() + 
//				"    ," + gameRecord.getPlayer(name).getAmountInPot() +
//				" ," + gameRecord.getPlayer(name).getAmountWon() +
//				" ," + gameRecord.getPlayer(name).getProfit() + "\r");
		
	}

	private static void writeHeader(BufferedWriter out) throws IOException {
//		out.write("\"Stage\";\"Profit\"\r");
	}

}
