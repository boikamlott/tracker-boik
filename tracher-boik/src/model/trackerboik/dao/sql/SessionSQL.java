package model.trackerboik.dao.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.trackerboik.businessobject.PokerSession;
import model.trackerboik.dao.SessionDAO;

import com.trackerboik.exception.TBException;

public class SessionSQL extends GeneralSQLDBOperations implements SessionDAO {

	public SessionSQL() throws TBException {
		super();
	}

	public static final String TABLE_NAME = "session";
	
	private static final String ATT_FILE_ASSOCIATED_NM = "file_associated_name", 
						  ATT_SESSION_KIND = "session_kind";	
	@Override
	protected String getInsertPreCompiledRequest() {
		return "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?)";
	}
	
	@Override
	protected String getExistenceTestPreCompiledRequest() {
		return "SELECT * FROM " + TABLE_NAME + " WHERE " + GEN_ATT_SESSION_ID + "= ?";
	}

	@Override
	protected String getAllElementsForLoadSessionInMemoryRequest() {
		return "SELECT * FROM " + TABLE_NAME + " WHERE " + GEN_ATT_SESSION_CALCULATED + " = ?";
	}
	
	@Override
	public void createTable() throws TBException {
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_SESSION_ID + " VARCHAR(256) PRIMARY KEY,";
		rq += ATT_FILE_ASSOCIATED_NM + " VARCHAR(256) NOT NULL,";
		rq += ATT_SESSION_KIND + " VARCHAR(256),";
		rq += GEN_ATT_SESSION_CALCULATED + " VARCHAR(10) NOT NULL,";
		rq += " CONSTRAINT agg_calculate_bool_enum CHECK (" + GEN_ATT_SESSION_CALCULATED + " in ('y', 'n'))";
		rq += ")";
		
		executeSQLUpdate(rq);
	}

	@Override
	public void insertSession(PokerSession ps) throws TBException {
		try {		
			psInsert.setString(1, ps.getId());
			psInsert.setString(2, ps.getAssociatedFileName());
			psInsert.setString(3, ps.getSessionKind());
			psInsert.setString(4, "n");
		
			if(psInsert.execute()) {
				throw new TBException("Unexpected result while trying to insert session " + ps.getId());
			}
			
		} catch (SQLException e) {
			throw new TBException("Error while preparing session " + ps.getId() + " insertion: " + e.getMessage());
		}
		
	}

	@Override
	public boolean sessionExists(PokerSession associatedSession)
			throws TBException {
		try {
			psQuery = createPreparedStatement(getExistenceTestPreCompiledRequest());
			psQuery.setString(1, associatedSession.getId());
			ResultSet rs = psQuery.executeQuery();
			
			return rs.next();
		} catch (Exception e) {
			throw new TBException("Impossible to check Hand existence in database: '" + e.getMessage() + "'");
		}
	}

	@Override
	public List<PokerSession> getAllSesssionsUncalculated() throws TBException {
		try {
			psQuery = createPreparedStatement(getAllElementsForLoadSessionInMemoryRequest());
			psQuery.setString(1, "n");
			ResultSet rs = psQuery.executeQuery();
			List<PokerSession> res = new ArrayList<PokerSession>();
			
			while(rs.next()) {
				res.add(new PokerSession(rs.getString(GEN_ATT_SESSION_ID), 
						ATT_FILE_ASSOCIATED_NM, ATT_SESSION_KIND));
			}
			
			return res;
		} catch (SQLException e) {
			throw new TBException("Impossible to read sessions in BDD: " + e.getMessage());
		}
	}

	@Override
	public void markAllSessionsAsCalculated() throws TBException {
		try {
			psQuery = createPreparedStatement("UPDATE " + TABLE_NAME + " SET " + GEN_ATT_SESSION_CALCULATED + "=?");
			psQuery.setString(1, "y");
			psQuery.execute();
		} catch (SQLException e) {
			throw new TBException("Error during database update on session table: " + e.getMessage());
		}
		
	}

}
