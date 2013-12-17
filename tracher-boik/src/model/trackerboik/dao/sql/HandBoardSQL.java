package model.trackerboik.dao.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PokerBoard;
import model.trackerboik.dao.HandBoardDAO;

import com.trackerboik.exception.TBException;

public class HandBoardSQL extends GeneralSQLDBOperations implements
HandBoardDAO {
	public HandBoardSQL() throws TBException {
		super();
	}

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
			psInsert.setString(1, h.getId());
			psInsert.setString(2, pb.getID());
		
			if(psInsert.execute()) {
				throw new TBException("Unexpected result while trying to insert hand_boardr (" + h.getId() + "," + pb.getID() + ")");
			}
		} catch (SQLException e) {
			throw new TBException("Impossible to add hand_board (" + h.getId() + "," + pb.getID() + ")" + " because: " + e.getMessage());
		}
		
	}

	@Override
	protected String getInsertPreCompiledRequest() {
		return "INSERT INTO " + TABLE_NAME + " VALUES (?, ?)";
	}

	@Override
	protected String getExistenceTestPreCompiledRequest() {
		return "SELECT * FROM " + TABLE_NAME + " WHERE " + GEN_ATT_BOARD_ID + " = ? AND " + GEN_ATT_HAND_ID + " = ?";
	}

	@Override
	protected String getAllElementsRequest() {
		return "SELECT * FROM " + TABLE_NAME + " WHERE " + GEN_ATT_HAND_ID + " = ?";
	}

	@Override
	public String getBoardIDForHand(Hand h) throws TBException {
		try {
			psQuery = createPreparedStatement(getAllElementsRequest());
			psQuery.setString(1, h.getId());
			ResultSet rs = psQuery.executeQuery();
			String res = null;
			
			if(rs.next()) {
				res = rs.getString(GEN_ATT_BOARD_ID);
			}
			
			return res;
		} catch (SQLException e) {
			throw new TBException("Impossible to read the association beetween hand " + h.getId() + " and board !");
		}
	}
	
	

}
