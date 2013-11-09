package model.trackerboik.dao.hsqldb;

import com.trackerboik.exception.TBException;

import model.trackerboik.dao.PlayerDAO;

public class PlayerHSQL extends GeneralHSQLDBOperations implements PlayerDAO {
	public static final String TABLE_NAME = "player";

	private static final String ATT_COMMENT = "comment";

	@Override
	public void createTable() throws TBException {
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_PLAYER_ID + "VARCHAR(50) PRIMARY KEY,";
		rq += ATT_COMMENT + " VARCHAR(200))";

		executeSQLUpdate(rq);

	}

}
