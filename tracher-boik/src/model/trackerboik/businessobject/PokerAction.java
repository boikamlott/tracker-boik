package model.trackerboik.businessobject;

public class PokerAction {

	private Hand hand;
	private PokerPlayer associatedPlayer;
	private Integer actionNumberInHand;
	private Double amountBet;
	private ActionKind kind;
	private HandMoment moment;
	
	public PokerAction(PokerPlayer pp, Hand ph) {
		setAssociatedPlayer(pp);
		setHand(ph);
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
	
	
	
	
	
}
