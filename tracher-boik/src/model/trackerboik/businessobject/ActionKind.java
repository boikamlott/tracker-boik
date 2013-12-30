package model.trackerboik.businessobject;

public enum ActionKind {
	BET("bet", "bets"),
	CALL("call", "calls"),
	CHECK("check", "checks"),
	FOLD("fold", "folds"),
	POSTBIGBLIND("postBB", "posts big blind"),
	POSTSBLIND("postSB", "posts small blind"),
	RAISE("raise", "raises"), 
	UNCALLED_BET("uncalled_bet", "Uncalled Bet");
	
	public String valueText;
	public String valueFile;
	
	private ActionKind(String valTxt, String valFile) {
		this.valueText = valTxt;
		this.valueFile = valFile;
	}
	
	public String getFileValue() {
		return this.valueFile;
	}

	/**
	 * Return all elements separated with ',' and each element is written
	 * beetween "'".
	 * @return
	 */
	public String toString() {
		return valueText;
	}

	public static ActionKind readActionKind(String attKind) {
		for(ActionKind ak : ActionKind.values()) {
			if(ak.toString().equals(attKind)) {
				return ak;
			}
		}
		
		return null;
	}
}
