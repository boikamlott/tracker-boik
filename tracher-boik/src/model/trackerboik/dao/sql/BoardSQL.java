package model.trackerboik.dao.sql;

import com.trackerboik.exception.TBException;

import model.trackerboik.businessobject.PokerBoard;
import model.trackerboik.dao.BoardDAO;

public class BoardSQL extends GeneralSQLDBOperations implements BoardDAO {
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
			   rq += ATT_FLOP_1 + " varchar(2) NOT NULL,";	
			   rq += ATT_FLOP_2 + " varchar(2) NOT NULL,";
			   rq += ATT_FLOP_3 + " varchar(2) NOT NULL,";
			   rq += ATT_TURN + " varchar(2),";
			   rq += ATT_RIVER + " varchar(2))";
			   
		executeSQLUpdate(rq);		
	}

	@Override
	public void insertBoard(PokerBoard pb) throws TBException {
		String rq = "INSERT INTO " + TABLE_NAME + "(";
		rq += "'" + pb.getID() + "',";
		
		if(pb.getFlop() != null) { 
			rq += "'','','',";
		} else {
			for(int i = 0; i < 3 ; i++) {
				rq += "'" + pb.getFlop().get(i) + "',";
			}
		}	
		
		rq += (pb.getTurn() == null ? "''" : "'" + pb.getTurn() + "'") + ",";
		rq += (pb.getRiver() == null ? "''" : "'" + pb.getRiver() + "'") + ")";
		
		executeSQLUpdate(rq);
		
	}

}
