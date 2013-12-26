package model.trackerboik.dao;

import model.trackerboik.businessobject.PokerSession;

import com.trackerboik.exception.TBException;

public interface SessionDAO extends GeneralDBOperationsDAO {

	public void insertSession(PokerSession ps) throws TBException;

	public boolean sessionExists(PokerSession associatedSession) throws TBException;

}
