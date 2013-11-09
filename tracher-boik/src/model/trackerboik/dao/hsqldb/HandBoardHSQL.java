package model.trackerboik.dao.hsqldb;

import com.trackerboik.exception.TBException;

import model.trackerboik.dao.HandBoardDAO;

public class HandBoardHSQL extends GeneralHSQLDBOperations implements
HandBoardDAO {
	public static final String TABLE_NAME = "hand_board";
	
	@Override
	public void createTable() throws TBException {
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_HAND_ID + " VARCHAR(20) PRIMARY KEY REFERENCES " + HandHSQL.TABLE_NAME + "(" + GEN_ATT_HAND_ID + "),";
		rq += GEN_ATT_BOARD_ID + " VARCHAR(10) REFERENCES " + BoardHSQL.TABLE_NAME + "(" + GEN_ATT_BOARD_ID + "))";

		executeSQLUpdate(rq);
	}

}
