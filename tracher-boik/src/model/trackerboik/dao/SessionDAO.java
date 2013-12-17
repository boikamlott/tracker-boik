package model.trackerboik.dao;

import java.util.List;

import com.trackerboik.exception.TBException;

import model.trackerboik.businessobject.PokerSession;

public interface SessionDAO extends GeneralDBOperationsDAO {

	public void insertSession(PokerSession ps) throws TBException;

	public boolean sessionExists(PokerSession associatedSession) throws TBException;

	public List<PokerSession> getAllSesssionsUncalculated() throws TBException;

	public void markAllSessionsAsCalculated() throws TBException;
}
