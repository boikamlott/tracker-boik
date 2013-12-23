package model.trackerboik.businessobject;

public enum PokerIndicator {
	NB_HANDS("hands"),
	VPIP("VP$IP"),
	PREFLOP_RAISE("PFR"),
	AGRESSION_FACTOR("AF"),
	THREE_BET("3Bet"),
	FOLD_TO_THREE_BET("F3B"),
	ATTEMPT_TO_STEAL_BLINDS("ATS"),
	FOLD_TO_ATS_SB("ATSFSB"),
	FOLD_TO_ATS_BB("ATSFBB"),
	LIMP_FOLD_CALL("LFC"),
	CBET("CBet"),
	FOLD_TO_CBET("FCBet"),
	SECOND_BARREL("2ndB"),
	FOLD_TO_SECOND_BARREl("F2ndBarrel"),
	WENT_TO_SHOWDOWN("WTSD"),
	WIN_TO_SHOWDOWN("W$TSD");
	
	private String txt;
	
	private PokerIndicator(String lbl) {
		this.txt = lbl;
	}
	
	public String toString() {
		return txt;
	}
}
