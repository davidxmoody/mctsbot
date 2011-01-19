package tools;

import com.biotools.meerkat.Card;

public class HandStrengthConverter {
	
	/*
	private static final int HIGHEST_HIGH_CARD = 1276;
	private static final int HIGHEST_PAIR = 4136;
	private static final int HIGHEST_TWO_PAIRS = 4994;
	private static final int HIGHEST_THREE_OF_A_KIND = 5852;
	private static final int HIGHEST_STRAIGHT = 5862;
	private static final int HIGHEST_FLUSH = 7139;
	private static final int HIGHEST_FULL_HOUSE = 7295;
	private static final int HIGHEST_FOUR_OF_A_KIND = 7451;
	private static final int HIGHEST_STRAIGHT_FLUSH = 7461;
	*/
	
	private static final int[] HIGHEST_RANKS = {1276, 4136, 4994, 5852, 5862, 
												7139, 7295, 7451, 7461};
	
	private static final double[] weights = {1,			// High Card.
											 1,			// One Pair.
											 1,			// Two Pairs.
											 1,			// Three Of A Kind.
											 1,			// Straight.
											 1,			// Flush.
											 1,			// Full House.
											 1,			// Four Of A Kind
											 1};		// Straight Flush.
											 
	
	public static void main(String[] args) {
		System.out.println(Card.TWO);
	}
	
	
	public static double rankToStrength(int rank) {
		
		double strength = 0.0;
		
		for(int i=0; i<HIGHEST_RANKS.length; i++) {
			if(rank<HIGHEST_RANKS[i]) {
				strength += weights[i]*(rank-(i==0?0:HIGHEST_RANKS[i-1]))
						/(HIGHEST_RANKS[i]-(i==0?0:HIGHEST_RANKS[i-1]));
				break;
			} else {
				strength += weights[i];
			}
		}
		
		return strength;
		
		
		/*
		if(rank<=HIGHEST_HIGH_CARD) {
			
		} else if(rank<=HIGHEST_PAIR) {
			
		} else if(rank<=HIGHEST_TWO_PAIRS) {
			
		} else if(rank<=HIGHEST_THREE_OF_A_KIND) {
			
		} else if(rank<=HIGHEST_STRAIGHT) {
			
		} else if(rank<=HIGHEST_FLUSH) {
			
		} else if(rank<=HIGHEST_FULL_HOUSE) {
			
		} else if(rank<=HIGHEST_FOUR_OF_A_KIND) {
			
		} else if(rank<=HIGHEST_STRAIGHT_FLUSH) {
			
		} else throw new RuntimeException("invalid rank: " + rank);
		*/
		
	}

}


