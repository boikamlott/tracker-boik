package model.trackerboik.dao.sql;

import java.sql.SQLException;

import model.trackerboik.businessobject.PokerBoard;
import model.trackerboik.dao.BoardDAO;

import com.trackerboik.exception.TBException;

public class BoardSQL extends GeneralSQLDBOperations implements BoardDAO {
	public BoardSQL() throws TBException {
		super();
	}

	public static final String TABLE_NAME = "board";
	
	private static final String ATT_FLOP_1 = "flop_1",
								ATT_FLOP_2 = "flop_2",
								ATT_FLOP_3 = "flop_3",
								ATT_TURN = "turn",
								ATT_RIVER = "river";
	
	@Override
	public void createTable() throws TBException {
		
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
			   rq += GEN_ATT_BOARD_ID + " varchar(256) PRIMARY KEY,"; 	
			   rq += ATT_FLOP_1 + " varchar(2) NOT NULL,";	
			   rq += ATT_FLOP_2 + " varchar(2) NOT NULL,";
			   rq += ATT_FLOP_3 + " varchar(2) NOT NULL,";
			   rq += ATT_TURN + " varchar(2),";
			   rq += ATT_RIVER + " varchar(2))";
			   
		executeSQLUpdate(rq);		
	}

	@Override
	public void insertBoard(PokerBoard pb) throws TBException {
		try {
		
			psInsert.setString(1, pb.getID());			
			if(pb.getFlop() == null) { 
				for(int i = 2; i <= 4; i++) {
					psInsert.setString(i, "");
				}
			} else {
				for(int i = 0; i <= 2 ; i++) {
					psInsert.setString(i + 2, pb.getFlop().get(i).toString());
				}
			}
			
			psInsert.setString(5, pb.getTurn() == null ? "" : pb.getTurn().toString());
			psInsert.setString(6, pb.getRiver() == null ? "" : pb.getRiver().toString());
			
		
			if(psInsert.execute()) {
				throw new TBException("Unexpected result while trying to insert board " + pb.getID());
			}
		} catch (SQLException e) {
			throw new TBException("Impossible to add board " + pb.getID() + " because: " + e.getMessage());
		}	
	}

	@Override
	protected String getInsertPreCompiledRequest() {
		return "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?, ?, ?)";
	}

}
