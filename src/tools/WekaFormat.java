package tools;

import java.io.BufferedWriter;

import mctsbot.gamestate.Player;
import mctsbot.nodes.ShowdownNode;

import weka.core.Instance;

public interface WekaFormat {
	
	public Instance getInstance(ShowdownNode showdownNode, Player opponent, int botHandRank);
	
	public void write(GameRecord gameRecord, String name, BufferedWriter out) throws Exception;
	
	public void writeHeader(BufferedWriter out) throws Exception;

}
