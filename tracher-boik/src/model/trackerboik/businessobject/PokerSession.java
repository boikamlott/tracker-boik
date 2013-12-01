package model.trackerboik.businessobject;

public class PokerSession {

	private String id;
	private String associatedFileName;
	private String sessionKind;
	
	public PokerSession(String id, String fn, String kind) {
		setId(id);
		setAssociatedFileName(fn);
		setSessionKind(kind);
	}
	
	public String getSessionKind() {
		return sessionKind;
	}

	public void setSessionKind(String sessionKind) {
		this.sessionKind = sessionKind;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAssociatedFileName() {
		return associatedFileName;
	}
	public void setAssociatedFileName(String associatedFileName) {
		this.associatedFileName = associatedFileName;
	}
	
	
}
