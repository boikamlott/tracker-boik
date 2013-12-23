package model.trackerboik.businessobject;

public class PlayerSessionStats {

	private String playerID;
	private PokerSession session;
	
	public Double benefitGeneral, winrate;
	public Integer nbHand, nbHandVPIP, nbHandPFR;
	public Integer nbATSPossible, nbATS;
	public Integer nbFoldToATSSBPossible, nbFoldToATSBBPossible, nbFoldToATSSB, nbFoldToATSBB;
	public Integer nbAFHandBetAndRaise, nbAFHandCalled;
	public Integer nbLimpTotal, nbLimpThenFold, nbLimpThenCall;
	public Integer nb3betPossible, nb3bet;
	public Integer nbFoldTo3betPossible, nbFoldTo3bet;
	public Integer nbCbetPossible, nbCbet;
	public Integer nbFoldToCbetPossible, nbFoldToCbet;
	public Integer nbSecondBarrelPossible, nbSecondBarrel;
	public Integer nbFoldToSecondBarrelPossible, nbFoldToSecondBarrel;
	public Integer nbWentToShowdownHand, nbWinToShowdownHand;
	
	public PlayerSessionStats(String playerID, PokerSession ps) {
		this.playerID = playerID;
		this.session = ps;
	}

	public String getPlayerID() {
		return playerID;
	}

	public PokerSession getSession() {
		return session;
	}
	
	public boolean equals(Object o) {
		return o instanceof PlayerSessionStats && 
					((PlayerSessionStats) o).getPlayerID().equals(this.playerID) &&
					((PlayerSessionStats) o).getSession().equals(this.session);
	}
	
	public String toString() {
		return session + " -> " + playerID;
	}
}
