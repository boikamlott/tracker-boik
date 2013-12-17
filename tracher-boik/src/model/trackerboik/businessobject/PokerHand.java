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

	/**
	 * Return true if hand could be read by others application structures
	 * @return
	 */
	public boolean isCorrect() {
		return cards != null && cards.length == 2 && cards[0] != null && 
				cards[0].isCorrect() && cards[1] != null && cards[1].isCorrect();
	}
}
