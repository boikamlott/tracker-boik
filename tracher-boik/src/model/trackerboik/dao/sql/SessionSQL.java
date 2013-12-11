package model.trackerboik.dao.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.trackerboik.exception.TBException;

import model.trackerboik.businessobject.PokerSession;
import model.trackerboik.dao.SessionDAO;

public class SessionSQL extends GeneralSQLDBOperations implements SessionDAO {

	public static final String TABLE_NAME = "session";
	
	private static final String ATT_FILE_ASSOCIATED_NM = "file_associated_name", 
						  ATT_SESSION_KIND = "session_kind";
	
	@Override
	public void createTable() throws TBException {
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_SESSION_ID + " VARCHAR(256) PRIMARY KEY,";
		rq += ATT_FILE_ASSOCIATED_NM + " VARCHAR(256) NOT NULL,";
		rq += ATT_SESSION_KIND + " VARCHAR(256))";
		
		executeSQLUpdate(rq);
	}

	@Override
	public void insertSession(PokerSession ps) throws TBException {
		try {
			String rq = "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?)";
			PreparedStatement psbdd = createPreparedStatement(rq);
		
			psbdd.setString(1, ps.getId());
			psbdd.setString(2, ps.getAssociatedFileName());
			psbdd.setString(3, ps.getSessionKind());
		
			if(psbdd.execute()) {
				throw new TBException("Unexpected result while trying to insert session " + ps.getId());
			}
			
		} catch (SQLException e) {
			throw new TBException("Error while preparing session " + ps.getId() + " insertion: " + e.getMessage());
		}
		
	}

}
