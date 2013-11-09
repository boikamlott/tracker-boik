package model.trackerboik.dao.hsqldb;

import com.trackerboik.exception.TBException;

import model.trackerboik.dao.BoardDAO;

public class BoardHSQL extends GeneralHSQLDBOperations implements BoardDAO {
	public static final String TABLE_NAME = "board";
	
	private static final String ATT_FLOP_1 = "flop_1",
								ATT_FLOP_2 = "flop_2",
								ATT_FLOP_3 = "flop_3",
								ATT_TURN = "turn",
								ATT_RIVER = "river";
	
	@Override
	public void createTable() throws TBException {
		
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
			   rq += GEN_ATT_BOARD_ID + " varchar(10) PRIMARY KEY,"; 	
			   rq += ATT_FLOP_1 + "flop_1 varchar(2) NOT NULL,";	
			   rq += ATT_FLOP_2 + "flop_2 varchar(2) NOT NULL,";
			   rq += ATT_FLOP_3 + "flop_3 varchar(2) NOT NULL,";
			   rq += ATT_TURN + "turn varchar(2),";
			   rq += ATT_RIVER + "river varchar(2))";
			   
		executeSQLUpdate(rq);		
	}

}
