package model.trackerboik.dao;

import model.trackerboik.businessobject.Hand;

import com.trackerboik.exception.TBException;

public interface HandDAO extends GeneralDBOperationsDAO {
	public void insertHand(Hand h) throws TBException;

	public boolean isHandExists(String id) throws TBException;
	
}
