package model.trackerboik.businessobject;

public class PokerHand {

	private static int NB_CARDS_PER_HAND = 2;
	private PokerCard[] cards;
	
	public PokerHand() {
		cards = new PokerCard[NB_CARDS_PER_HAND];
	}
	
	public void setHand(PokerCard firstCard, PokerCard secondCard) {
		cards[0] = firstCard;
		cards[1] = secondCard;
	}
	
	public PokerCard[] getHand() {
		return cards;
	}
}
