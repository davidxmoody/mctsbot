package tools;

import java.io.BufferedWriter;

public interface WekaFormat {
	
	public void write(GameRecord gameRecord, String name, BufferedWriter out) throws Exception;
	
	public void writeHeader(BufferedWriter out) throws Exception;

}
