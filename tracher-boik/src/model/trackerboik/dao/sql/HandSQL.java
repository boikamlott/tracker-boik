package model.trackerboik.dao.sql;

import java.sql.ResultSet;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.dao.HandDAO;

import com.trackerboik.exception.TBException;

public class HandSQL extends GeneralSQLDBOperations implements HandDAO {

	public static final String TABLE_NAME = "hand";

	private static String ATT_POT = "pot", ATT_SITE_RAKE = "rake",
			ATT_BB_VALUE = "bb_value", ATT_TABLE_NAME = "table_name",
			ATT_MOMENT = "moment";

	@Override
	public void createTable() throws TBException {
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_HAND_ID + " VARCHAR(20) PRIMARY KEY,";
		rq += ATT_POT + " double NOT NULL,";
		rq += ATT_SITE_RAKE += " double NOT NULL,";
		rq += ATT_BB_VALUE += " double NOT NULL,";
		rq += ATT_TABLE_NAME += " VARCHAR(20),";
		rq += ATT_MOMENT + " TIMESTAMP,";
		rq += GEN_ATT_SESSION_ID + " VARCHAR(50) REFERENCES "
				+ SessionSQL.TABLE_NAME + "(" + GEN_ATT_SESSION_ID + "))";

		executeSQLUpdate(rq);
	}

	@Override
	public void insertHand(Hand h) throws TBException {
		String rq = "INSERT INTO " + TABLE_NAME + "(";
		rq += "'" + h.getId() + "',";
		rq += h.getPot() + ",";
		rq += h.getSiteRake() + ",";
		rq += h.getLimitBB() + ",";
		rq += "'" + h.getTableName() + "',";
		rq += "'" + h.getSQLFormattedMoment() + "',";
		rq += "'" + h.getAssociatedSession().getId() + "')";
		
		executeSQLUpdate(rq);
		
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

}
