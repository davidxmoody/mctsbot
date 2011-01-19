package tools;


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
	
	private static final double[] weights = {258,			// High Card.
											 866,			// One Pair.
											 511,			// Two Pairs.
											 93,			// Three Of A Kind.
											 58,			// Straight.
											 84,			// Flush.
											 62,			// Full House.
											 6,			// Four Of A Kind.
											 1};		// Straight Flush.
	
	private static final double[] gaps = {0,			// High Card - One Pair.
										  0,			// One Pair - Two Pairs.
										  0,			// Two Pairs - Three Of A Kind.
										  0,			// Three Of A Kind - Straight.
										  0,			// Straight - Flush.
										  0,			// Flush - Full House.
										  0,			// Full House - Four Of A Kind.
										  0};			// Four Of A Kind - Straight Flush.
											 
	
	
	public static double rankToStrength(int rank) {
		
		double strength = 0.0;
		
		for(int i=0; i<HIGHEST_RANKS.length; i++) {
			if(rank<HIGHEST_RANKS[i]) {
				strength += weights[i]*(rank-(i==0?0:HIGHEST_RANKS[i-1]))
						/(HIGHEST_RANKS[i]-(i==0?0:HIGHEST_RANKS[i-1]));
				break;
			} else {
				strength += weights[i] + gaps[i];
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


