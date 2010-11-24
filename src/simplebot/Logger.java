package simplebot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import com.biotools.meerkat.Action;
import com.biotools.meerkat.Card;
import com.biotools.meerkat.GameInfo;
import com.biotools.meerkat.HandEvaluator;

public class Logger {
	
	final String logFileLocation;
	
	private BufferedWriter writer = null;
	
	private Card c1, c2;
	private GameInfo gi;
	private int seat;
	
	@SuppressWarnings("unchecked")
	private LinkedList<Action>[] actions = new LinkedList[4];
	private double[] amounts = new double[4];
	
	
	public Logger(String logFileLocation) {
		this.logFileLocation = logFileLocation;
		
		for(int i=0; i<4; i++)
			actions[i] = new LinkedList<Action>();
	}
	
	public void holeCards(Card c1, Card c2, int seat) {
		this.c1 = c1;
		this.c2 = c2;
		this.seat = seat;
	}
	
	public void reset() {
		c1 = null;
		c2 = null;
		gi = null;
		
		for(int i=0; i<4; i++) {
			actions[i].clear();
			amounts[i] = 0;
		}
	}
	
	public void logAction(Action action, GameInfo gi) {
		this.gi = gi;
		
		if(action.isBetOrRaise() || action.isCheckOrCall())
			actions[gi.getStage()].add(action);
		amounts[gi.getStage()] = gi.getPlayer(seat).getAmountInPotThisRound();
	}

	
	private String getLogEntry() {
		StringBuilder s = new StringBuilder();
		
		// Write actions and amount for each stage.
		for(int i=0; i<4; i++) {
			
			int raiseCount = 0;
			for(int j=0; j<actions[i].size(); j++) 
				if(actions[i].get(j).isBetOrRaise()) raiseCount++;
			
			if(raiseCount<1) s.append("c,");
			else if(raiseCount==1) s.append("r,");
			else if(raiseCount>1) s.append("rr,");

			//TODO: fix adding amounts.
			//s.append("," + amounts[i] + ",");
		}
		
		// Write card ranks and the number of suited cards.
		int[] ranks = new int[5];
		int[] suits = new int[5];
		
		int[] cards = gi.getBoard().getCardArray();
		
		for(int i=0; i<5; i++) {
			ranks[i] = Card.getRank(cards[i+1]);
			suits[i] = Card.getSuit(cards[i+1]);
		}
		
		// Write card ranks
		for(int i=0; i<5; i++) {
			s.append(Card.getRankChar(ranks[i]) + ",");
		}
		
		// Write suits.
		int[] suitCounts = new int[4];
		
		// Flop.
		suitCounts[suits[0]]++;
		suitCounts[suits[1]]++;
		suitCounts[suits[2]]++;
		
		String numSuitedFlop = "1,";
		for(int i=0; i<4; i++) {
			if(suitCounts[i]==3) {
				numSuitedFlop = "3,";
				break;
			} else if(suitCounts[i]==2) {
				numSuitedFlop = "2,";
				break;
			} 
		}
		s.append(numSuitedFlop);
		
		// Turn.
		suitCounts[suits[3]]++;
		
		String numSuitedTurn = "1,";
		for(int i=0; i<4; i++) {
			if(suitCounts[i]==4) {
				numSuitedTurn = "4,";
				break;
			} else if(suitCounts[i]==3) {
				numSuitedTurn = "3,";
				break;
			} else if(suitCounts[i]==2) {
				for(int j=0; j<4; j++) {
					if(j!=i && suitCounts[j]==2) {
						numSuitedTurn = "22,";
					} else if(suitCounts[j]==1) {
						numSuitedTurn = "2,";
					}
				}
			} 
		}
		s.append(numSuitedTurn);
		
		// River.
		suitCounts[suits[4]]++;
		
		String numSuitedRiver = "0,";
		for(int i=0; i<4; i++) {
			if(suitCounts[i]==5) {
				numSuitedRiver = "5,";
				break;
			} else if(suitCounts[i]==4) {
				numSuitedRiver = "4,";
				break;
			} else if(suitCounts[i]==3) {
				numSuitedRiver = "3,";
				break;
			}
		}
		s.append(numSuitedRiver);
		
		
		// Write hand rank
		final int handRank = HandEvaluator.rankHand(c1, c2, gi.getBoard());
		s.append(handRank + "\r");
		
		return s.toString();
	}
	
	
	public void writeToLog() {

		try {

			getWriter().write(getLogEntry());
			getWriter().flush();

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		
	}
	
	public BufferedWriter getWriter() {
		   try {
			   if(writer==null) {
				   final File loggingFile = new File(logFileLocation);
				   loggingFile.createNewFile();
				   writer = new BufferedWriter(new FileWriter(loggingFile, true));
			   }
		   } catch (IOException e) {
			   e.printStackTrace();
			   throw new RuntimeException();
		   }
		   return writer;
	   }
	
	
	protected void finalize() throws Throwable {
		if(writer!=null) writer.close();
	}

}
