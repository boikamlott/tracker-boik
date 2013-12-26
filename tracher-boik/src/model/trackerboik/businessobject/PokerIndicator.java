package model.trackerboik.businessobject;

public enum PokerIndicator {
	WINRATE("Winrate"),
	NB_HANDS("hands"),
	NB_FLOP_SEEN("FS"),
	NB_TURN_SEEN("TS"),
	NB_RIVER_SEEN("RS"),
	VPIP("VP$IP"),
	PREFLOP_RAISE("PFR"),
	AGRESSION_FACTOR_GENERAL("AFG"),
	THREE_BET("3Bet"),
	FOLD_TO_THREE_BET("F3B"),
	ATTEMPT_TO_STEAL_BLINDS("ATS"),
	FOLD_TO_ATS_SB("ATSFSB"),
	FOLD_TO_ATS_BB("ATSFBB"),
	LIMP_THEN_FOLD("LTF"),
	LIMP_THEN_CALL("LTC"),
	AGRESSION_FACTOR_FLOP("AFF"),
	CBET("CBet"),
	FOLD_TO_CBET("FCBet"),
	AGRESSION_FACTOR_TURN("AFT"),
	SECOND_BARREL("2ndB"),
	FOLD_TO_SECOND_BARREl("F2ndBarrel"),
	AGRESSION_FACTOR_RIVER("AFR"),
	WIN_TO_SHOWDOWN_WHEN_SEEING_FLOP("W$SDWSF"),
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
