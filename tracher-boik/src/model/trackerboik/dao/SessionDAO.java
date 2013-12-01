package model.trackerboik.dao;

import com.trackerboik.exception.TBException;

import model.trackerboik.businessobject.PokerSession;

public interface SessionDAO extends GeneralDBOperationsDAO {

	public void insertSession(PokerSession ps) throws TBException;
}
