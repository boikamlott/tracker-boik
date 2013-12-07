package model.trackerboik.businessobject;

import java.util.ArrayList;
import java.util.List;

import com.trackerboik.exception.TBException;

public class PokerBoard {

	public static int FLOP_1 = 0;
	public static int FLOP_2 = 1;
	public static int FLOP_3 = 2;
	public static int TURN = 3;
	public static int RIVER = 4;
	
	private PokerCard[] cards;
	
	private String id;
	
	public PokerBoard(String id) {
		this.id = id;
		cards = new PokerCard[5];
	}
	
	public String getID() {
		return this.id;
	}
	
	/**
	 * Update board flop
	 * PRE: Board flop has not be updated before (i.e all flop cards are null)
	 * @param flopCards
	 * @throws TBException
	 */
	public void setFlop(List<PokerCard> flopCards) throws TBException {
		if(flopCards == null) {
			throw new TBException("Internal error in PokerBoard module: Data structure or parameters are null !");
		} else if(flopCards.size() != 3) {
			throw new TBException("Business error in PokerBoard module: Invalid set flop call for poker Board '" + id + "' !");
		}
		
		for(int i = FLOP_1; i < FLOP_3; i++) {
			setCard(i, flopCards.get(i));
		}
	}
	
	/**
	 * Set turn of board
	 * PRE: Board's turn has to be not already set
	 * @param turn
	 * @throws TBException 
	 */
	public void setTurn(PokerCard turn) throws TBException {
		setCard(TURN, turn);
	}
	
	/**
	 * Set river of board
	 * PRE: Board's river has to be not already set
	 * @param river
	 * @throws TBException 
	 */
	public void setRiver(PokerCard river) throws TBException {
		setCard(RIVER, river);
	}
	/**
	 * Set card of board
	 * PRE: Card has to be not already writed (i.e is null)
	 * @param position
	 * @param card
	 * @throws TBException
	 */
	private void setCard(Integer position, PokerCard card) throws TBException {
		if(position == null || card == null) {
			throw new TBException("Internal error in PokerBoard module: Data structure or parameters are null !");
		} else if (!(0 <= position && position <= RIVER)) {
			throw new TBException("Internal error in PokerBoard module: Invalid position for card !");
		} else if(cards[position] != null) {
			throw new TBException("Internal error in PokerBoard module: Card already setted !");
		}
		
		cards[position] = card;
	}
	
	public List<PokerCard> getFlop() {
		List<PokerCard> res = new ArrayList<PokerCard>();
		res.add(cards[FLOP_1]);
		res.add(cards[FLOP_2]);
		res.add(cards[FLOP_3]);
		
		return res;
	}
	
	public PokerCard getTurn() {
		return cards[TURN];
	}
	
	public PokerCard getRiver() {
		return cards[RIVER];
	}
	
}
