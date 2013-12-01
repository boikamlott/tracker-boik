package model.trackerboik.businessobject;

import java.util.LinkedList;
import java.util.List;

public class PokerBoard {

	private static int FLOP_1 = 0, FLOP_2 = 1, FLOP_3 = 2, TURN = 3, RIVER = 4;
	
	private List<PokerCard> cards;
	
	private String id;
	
	public PokerBoard(String id) {
		this.id = id;
		cards = new LinkedList<PokerCard>();
	}
	
	public String getID() {
		return this.id;
	}
	
	public List<PokerCard> getFlop() {
		if(cards.size() >= FLOP_3 + 1) {
			return cards.subList(FLOP_1, FLOP_3);
		} else {
			return null;
		}
	}
	
	public PokerCard getTurn() {
		return getCardOrNull(TURN);
	}
	
	public PokerCard getRiver() {
		return getCardOrNull(RIVER);
	}
	
	public PokerCard getCardOrNull(int moment) {
		if(cards.size() >= moment + 1) {
			return cards.get(moment);
		} else {
			return null;
		}
	}
	
}
