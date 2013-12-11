package model.trackerboik.dao.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
		rq += GEN_ATT_HAND_ID + " VARCHAR(256) PRIMARY KEY REFERENCES " + HandSQL.TABLE_NAME + "(" + GEN_ATT_HAND_ID + "),";
		rq += GEN_ATT_BOARD_ID + " VARCHAR(256) REFERENCES " + BoardSQL.TABLE_NAME + "(" + GEN_ATT_BOARD_ID + "))";

		executeSQLUpdate(rq);
	}

	@Override
	public void insertHandBoard(Hand h, PokerBoard pb) throws TBException {
		try {
			String rq = "INSERT INTO " + TABLE_NAME + " VALUES (?, ?)";
			PreparedStatement psbdd = createPreparedStatement(rq);
		
			psbdd.setString(1, h.getId());
			psbdd.setString(2, pb.getID());
		
			if(psbdd.execute()) {
				throw new TBException("Unexpected result while trying to insert hand_boardr (" + h.getId() + "," + pb.getID() + ")");
			}
		} catch (SQLException e) {
			throw new TBException("Impossible to add hand_board (" + h.getId() + "," + pb.getID() + ")" + " because: " + e.getMessage());
		}
		
	}
	
	

}
