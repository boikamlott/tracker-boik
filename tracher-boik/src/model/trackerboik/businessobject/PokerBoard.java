package model.trackerboik.businessobject;

import java.util.LinkedList;
import java.util.List;

public class PokerBoard {

	private static int FLOP_1 = 0, FLOP_2 = 1, FLOP_3 = 2, TURN = 3, RIVER = 4;
	
	private List<PokerCard> cards;
	
	public PokerBoard() {
		cards = new LinkedList<PokerCard>();
	}
}
