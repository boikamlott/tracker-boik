package model.trackerboik.businessobject;

import java.util.ArrayList;
import java.util.List;

import com.trackerboik.exception.TBException;

public class PokerSession {

	public static final PokerSession ALL = new PokerSession("ALL", "ALL", "Hold'em");
	private String id;
	private String associatedFileName;
	private String sessionKind;
	private List<Hand> hands;
	
	public PokerSession(String id, String fn, String kind) {
		setId(id);
		setAssociatedFileName(fn);
		setSessionKind(kind);
		this.hands = new ArrayList<Hand>();
	}
	
	public void addHand(Hand h) throws TBException {
		HandValidator hv = new HandValidator(h);
		hv.validate();
		if(hands.contains(new Hand(h.getId()))) {
			throw new TBException("Hand (" + h.getId() + ") already exists in the session '" + id + "' !");
		}
		hands.add(h);
	}
	
	public Hand getHand(String id) {
		Hand res = null;
		
		if(hands.contains(new Hand(id))) {
			res = hands.get(hands.indexOf(new Hand(id)));
		}
		
		return res;
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
	
	/**
	 * Constructor to test equals function only
	 * @param id
	 */
	public PokerSession(String id) {
		this.id = id;
	}
	
	public boolean equals(Object o) {
		return (o instanceof PokerSession) && ((PokerSession) o).getId().equals(id);
	}

	/**
	 * Return the list of hands for the session
	 * @return
	 */
	public List<Hand> getHands() {
		return hands;
	}
	
}
