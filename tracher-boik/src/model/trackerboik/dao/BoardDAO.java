package model.trackerboik.dao;

import model.trackerboik.businessobject.PokerBoard;

import com.trackerboik.exception.TBException;

public interface BoardDAO extends GeneralDBOperationsDAO {
	public void insertBoard(PokerBoard pb) throws TBException;
}
