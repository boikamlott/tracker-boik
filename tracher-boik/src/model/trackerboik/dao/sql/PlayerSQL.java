package model.trackerboik.dao.sql;

import java.sql.ResultSet;

import com.trackerboik.exception.TBException;

import model.trackerboik.businessobject.PokerPlayer;
import model.trackerboik.dao.PlayerDAO;

public class PlayerSQL extends GeneralSQLDBOperations implements PlayerDAO {
	public static final String TABLE_NAME = "player";

	private static final String ATT_COMMENT = "comment";

	@Override
	public void createTable() throws TBException {
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_PLAYER_ID + " VARCHAR(50) PRIMARY KEY,";
		rq += ATT_COMMENT + " VARCHAR(200))";

		executeSQLUpdate(rq);

	}

	@Override
	public void insertPlayer(PokerPlayer pp) throws TBException {
		String rq = "INSERT INTO " + TABLE_NAME + "(";
		rq += "'" + pp.getPlayerID() + "',";
		rq += "'" + pp.getComment() + "')";
		
		executeSQLUpdate(rq);
		
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

}
