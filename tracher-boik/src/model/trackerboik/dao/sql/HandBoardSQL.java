package model.trackerboik.dao.sql;

import com.trackerboik.exception.TBException;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PokerBoard;
import model.trackerboik.dao.HandBoardDAO;

public class HandBoardSQL extends GeneralSQLDBOperations implements
HandBoardDAO {
	public static final String TABLE_NAME = "hand_board";
	
	@Override
	public void createTable() throws TBException {
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_HAND_ID + " VARCHAR(20) PRIMARY KEY REFERENCES " + HandSQL.TABLE_NAME + "(" + GEN_ATT_HAND_ID + "),";
		rq += GEN_ATT_BOARD_ID + " VARCHAR(10) REFERENCES " + BoardSQL.TABLE_NAME + "(" + GEN_ATT_BOARD_ID + "))";

		executeSQLUpdate(rq);
	}

	@Override
	public void insertHandBoard(Hand h, PokerBoard pb) throws TBException {
		String rq = "INSERT INTO " + TABLE_NAME + "(";
		rq += "'" + h.getId() + "',";
		rq += "'" + pb.getID() + "')";
		
		executeSQLUpdate(rq);
		
	}
	
	

}
