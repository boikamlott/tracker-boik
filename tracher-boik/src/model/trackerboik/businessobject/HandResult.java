package model.trackerboik.businessobject;

public enum HandResult {
	NO_BET("no_bet"),
	FOLD_PREFLOP("fold_preflop"),
	FOLD_FLOP("fold_flop"),
	FOLD_TURN("fold_turn"),
	FOLD_RIVER("fold_river"),
	LOOSE("loose"),
	WIN("win");
	
	private String txtResult;
	
	private HandResult(String txtStatus) {
		this.txtResult = txtStatus;
	}
	
	public String getTxtResult() {
		return this.txtResult;
	}
}
