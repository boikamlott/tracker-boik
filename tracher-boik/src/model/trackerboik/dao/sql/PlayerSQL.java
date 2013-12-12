package model.trackerboik.dao.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import model.trackerboik.businessobject.PokerPlayer;
import model.trackerboik.dao.PlayerDAO;

import com.trackerboik.exception.TBException;

public class PlayerSQL extends GeneralSQLDBOperations implements PlayerDAO {
	public PlayerSQL() throws TBException {
		super();
	}

	public static final String TABLE_NAME = "player";

	private static final String ATT_COMMENT = "comment";

	@Override
	public void createTable() throws TBException {
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_PLAYER_ID + " VARCHAR(256) PRIMARY KEY,";
		rq += ATT_COMMENT + " VARCHAR(256))";

		executeSQLUpdate(rq);

	}

	@Override
	public void insertPlayer(PokerPlayer pp) throws TBException {
		try {
		
			psInsert.setString(1, pp.getPlayerID());
			psInsert.setString(2, pp.getComment());
		
			if(psInsert.execute()) {
				throw new TBException("Unexpected result while trying to insert player " + pp.getPlayerID());
			}
		} catch (SQLException e) {
			throw new TBException("Impossible to add player " + pp.getPlayerID() + " because: " + e.getMessage());
		}
		
	}

	@Override
	public boolean isPlayerExists(String playerID) throws TBException {
		try {
			String rq = "SELECT * FROM " + TABLE_NAME + " WHERE " + GEN_ATT_PLAYER_ID + "='" + playerID + "'";
			ResultSet rs = executeSQLQuery(rq);
			
			return rs.next();
		} catch (Exception e) {
			throw new TBException("Impossible to check Player existence in database: '" + e.getMessage() + "'");
		}
	}

	@Override
	protected String getInsertPreCompiledRequest() {
		return "INSERT INTO " + TABLE_NAME + " VALUES (?, ?)";
	}

}
