package model.trackerboik.businessobject;

public enum ActionKind {
	CALL("call"),
	CHECK("check"),
	FOLD("fold"),
	POSTBIGBLIND("post bb"),
	POSTSBLIND("post sb"),
	RAISE("raise");
	
	public String valueText;
	
	private ActionKind(String valTxt) {
		this.valueText = valTxt;
	}
}
