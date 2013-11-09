package model.trackerboik.businessobject;

public enum ActionKind {
	CALL("call"),
	CHECK("check"),
	FOLD("fold"),
	POSTBIGBLIND("postBB"),
	POSTSBLIND("postSB"),
	RAISE("raise");
	
	public String valueText;
	
	private ActionKind(String valTxt) {
		this.valueText = valTxt;
	}

	/**
	 * Return all elements separated with ',' and each element is written
	 * beetween "'".
	 * @return
	 */
	public String toString() {
		return valueText;
	}
}
