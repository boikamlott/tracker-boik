package model.trackerboik.businessobject;

import java.util.HashMap;
import java.util.Map;

public class PokerPlayer {

	private Map<PokerAction, Hand> actionsHands;
	private String playerID;
	private String comment;
	public Double benefitGeneral, winrate;
	public Integer nbHand, nbHandVPIP, nbHandPFR;
	public Integer nbATSPossible, nbATS;
	public Integer nbFoldToATSSBPossible, nbFoldToATSBBPossible, nbFoldToATSSB, nbFoldToATSBB;
	public Integer nbAFHandBetAndRaise, nbAFHandCalled;
	public Integer nb3betPossible, nb3bet;
	public Integer nbFoldTo3betPossible, nbFoldTo3bet;
	public Integer nbCbetPossible, nbCbet;
	public Integer nbFoldToCbetPossible, nbFoldToCbet;
	public Integer nbSecondBarrelPossible, nbSecondBarrel;
	public Integer nbFoldToSecondBarrelPossible, nbFoldToSecondBarrel;
	
	public PokerPlayer(String playerID) {
		this.playerID = playerID;
		actionsHands = new HashMap<PokerAction, Hand>();
	}

	public String getPlayerID() {
		return playerID;
	}

	public void setPlayerID(String playerID) {
		this.playerID = playerID;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public void addActionHand(PokerAction pa, Hand h) {
		this.actionsHands.put(pa, h);
	}
	
	public boolean equals(Object o) {
		return (o instanceof PokerPlayer) && ((PokerPlayer) o).getPlayerID().equals(this.playerID);
	}
	
	
}
