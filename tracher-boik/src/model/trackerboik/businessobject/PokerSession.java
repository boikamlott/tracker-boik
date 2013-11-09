package model.trackerboik.businessobject;

public class PokerSession {

	private String id;
	private String associatedFileName;
	
	public PokerSession(String id, String fn) {
		setId(id);
		setAssociatedFileName(fn);
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
