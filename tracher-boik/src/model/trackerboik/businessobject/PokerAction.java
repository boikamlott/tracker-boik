package model.trackerboik.businessobject;

public class PokerAction implements Comparable<PokerAction> {

	private Hand hand;
	private PokerPlayer associatedPlayer;
	private Integer actionNumberInHand;
	private Double amountBet;
	private ActionKind kind;
	private HandMoment moment;
	
	public PokerAction(PokerPlayer pp, Hand ph, Integer actNoInHand, 
			Double amount, ActionKind ak, HandMoment hm) {
		setAssociatedPlayer(pp);
		setHand(ph);
		setActNoForHand(actNoInHand);
		setAmountBet(amount);
		setKind(ak);
		setMoment(hm);
	}

	public PokerPlayer getAssociatedPlayer() {
		return associatedPlayer;
	}

	public void setAssociatedPlayer(PokerPlayer associatedPlayer) {
		this.associatedPlayer = associatedPlayer;
	}

	public Hand getHand() {
		return hand;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public Integer getActNoForHand() {
		return actionNumberInHand;
	}

	public void setActNoForHand(Integer posInHand) {
		this.actionNumberInHand = posInHand;
	}

	public Double getAmountBet() {
		return amountBet;
	}

	public void setAmountBet(Double amountBet) {
		this.amountBet = amountBet;
	}

	public ActionKind getKind() {
		return kind;
	}

	public void setKind(ActionKind kind) {
		this.kind = kind;
	}

	public HandMoment getMoment() {
		return moment;
	}

	public void setMoment(HandMoment moment) {
		this.moment = moment;
	}

	/**
	 * Define Order as the litte action number
	 */
	@Override
	public int compareTo(PokerAction a2) {		
		return this.getActNoForHand() - a2.getActNoForHand();
	}
	
	public String toString() {
		return kind.toString() + ": " + (amountBet != null ? amountBet + "€": "");
	}
	
	
	
}
