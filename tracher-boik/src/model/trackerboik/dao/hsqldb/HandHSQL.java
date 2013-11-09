package model.trackerboik.dao.hsqldb;

import com.trackerboik.exception.TBException;

import model.trackerboik.dao.HandDAO;

public class HandHSQL extends GeneralHSQLDBOperations implements HandDAO {

	public static String TABLE_NAME = "hand";

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
		rq += GEN_ATT_SESSION_ID + "VARCHAR(50) REFERENCES "
				+ SessionHSQL.TABLE_NAME + "(" + GEN_ATT_SESSION_ID + "))";

		executeSQLUpdate(rq);
	}

}
