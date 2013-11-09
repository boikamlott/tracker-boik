package model.trackerboik.dao.hsqldb;

import com.trackerboik.exception.TBException;

import model.trackerboik.dao.SessionDAO;

public class SessionHSQL extends GeneralHSQLDBOperations implements SessionDAO {

	public static final String TABLE_NAME = "session";
	
	private static final String ATT_FILE_ASSOCIATED_NM = "file_associated_name", 
						  ATT_SESSION_KIND = "session_kind";
	
	@Override
	public void createTable() throws TBException {
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_SESSION_ID + " VARCHAR(50) PRIMARY KEY,";
		rq += ATT_FILE_ASSOCIATED_NM + " VARCHAR(100) NOT NULL,";
		rq += ATT_SESSION_KIND + " VARCHAR(50))";
		
		executeSQLUpdate(rq);
	}

}
