package model.trackerboik.dao.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.dao.HandDAO;

import com.trackerboik.exception.TBException;

public class HandSQL extends GeneralSQLDBOperations implements HandDAO {

	public HandSQL() throws TBException {
		super();
	}

	public static final String TABLE_NAME = "hand";

	private static String ATT_POT = "pot", ATT_SITE_RAKE = "rake",
			ATT_BB_VALUE = "bb_value", ATT_TABLE_NAME = "table_name",
			ATT_MOMENT = "moment";

	@Override
	public void createTable() throws TBException {
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_HAND_ID + " VARCHAR(256) PRIMARY KEY,";
		rq += ATT_POT + " double NOT NULL,";
		rq += ATT_SITE_RAKE += " double NOT NULL,";
		rq += ATT_BB_VALUE += " double NOT NULL,";
		rq += ATT_TABLE_NAME += " VARCHAR(256),";
		rq += ATT_MOMENT + " TIMESTAMP,";
		rq += GEN_ATT_SESSION_ID + " VARCHAR(256) REFERENCES "
				+ SessionSQL.TABLE_NAME + "(" + GEN_ATT_SESSION_ID + "))";

		executeSQLUpdate(rq);
	}

	@Override
	public void insertHand(Hand h) throws TBException {
		try {
			psInsert.setString(1, h.getId());
			psInsert.setDouble(2, h.getPot());
			psInsert.setDouble(3, h.getSiteRake());
			psInsert.setDouble(4, h.getLimitBB());
			psInsert.setString(5, h.getTableName());
			psInsert.setString(6, h.getSQLFormattedMoment());
			psInsert.setString(7, h.getAssociatedSession().getId());
		
			if(psInsert.execute()) {
				throw new TBException("Unexpected result while trying to insert hand " + h.getId());
			}
		} catch (SQLException e) {
			throw new TBException("Impossible to add hand " + h.getId() + " because: " + e.getMessage());
		}
		
	}

	@Override
	public boolean isHandExists(String id) throws TBException {
		try {
			String rq = "SELECT * FROM " + TABLE_NAME + " WHERE " + GEN_ATT_HAND_ID + "='" + id + "'";
			ResultSet rs = executeSQLQuery(rq);
			
			return rs.next();
		} catch (Exception e) {
			throw new TBException("Impossible to check Hand existence in database: '" + e.getMessage() + "'");
		}
	}

	@Override
	protected String getInsertPreCompiledRequest() {
		return "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?, ?, ?, ?)";
	}

}
