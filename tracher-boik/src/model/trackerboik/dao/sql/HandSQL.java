package model.trackerboik.dao.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PokerSession;
import model.trackerboik.dao.HandDAO;

import com.trackerboik.appmngt.TrackerBoikLog;
import com.trackerboik.exception.TBException;
import com.trackerboik.util.AppUtil;

public class HandSQL extends GeneralSQLDBOperations implements HandDAO {

	public HandSQL() throws TBException {
		super();
	}

	public static final String TABLE_NAME = "hand";

	private static String ATT_POT = "pot", ATT_SITE_RAKE = "rake",
			ATT_BB_VALUE = "bb_value", ATT_TABLE_NAME = "table_name",
			ATT_MOMENT = "moment", ATT_BUTTON_SEAT_NO = "bouton_seat_no";

	@Override
	public void createTable() throws TBException {
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_HAND_ID + " VARCHAR(256) PRIMARY KEY,";
		rq += ATT_POT + " double NOT NULL,";
		rq += ATT_SITE_RAKE += " double NOT NULL,";
		rq += ATT_BB_VALUE += " double NOT NULL,";
		rq += ATT_TABLE_NAME += " VARCHAR(256),";
		rq += ATT_MOMENT + " TIMESTAMP,";
		rq += ATT_BUTTON_SEAT_NO + " INTEGER NOT NULL,";
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
			psInsert.setInt(7, h.getButtonSeatNumber());
			psInsert.setString(8, h.getAssociatedSession().getId());
		
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
			psQuery = createPreparedStatement(getExistenceTestPreCompiledRequest());
			psQuery.setString(1, id);
			ResultSet rs = psQuery.executeQuery();
			
			return rs.next();
		} catch (Exception e) {
			throw new TBException("Impossible to check Hand existence in database: '" + e.getMessage() + "'");
		}
	}

	@Override
	protected String getInsertPreCompiledRequest() {
		return "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	}

	@Override
	protected String getExistenceTestPreCompiledRequest() {
		return "SELECT * FROM " + TABLE_NAME + " WHERE " + GEN_ATT_HAND_ID + " = ?";
	}

	@Override
	protected String getAllElementsForLoadSessionInMemoryRequest() {
		return "SELECT * FROM " + TABLE_NAME + " WHERE " + GEN_ATT_SESSION_ID + " = ?";
	}

	@Override
	public List<Hand> getAllHandsForSession(PokerSession ps) throws TBException {
		try {
			psQuery = createPreparedStatement(getAllElementsForLoadSessionInMemoryRequest());
			psQuery.setString(1, ps.getId());
			ResultSet rs = psQuery.executeQuery();
			List<Hand> res = new ArrayList<Hand>();
			
			while(rs.next()) {
				try {
					Hand h = new Hand(rs.getString(GEN_ATT_HAND_ID), ps);
					h.setDateTime(AppUtil.parseCalendar(rs.getString(ATT_MOMENT), "yyyy-MM-dd hh:mm:ss"));
					h.setPot(rs.getDouble(ATT_POT));
					h.setSiteRake(rs.getDouble(ATT_SITE_RAKE));
					h.setLimitBB(rs.getDouble(ATT_BB_VALUE));
					h.setTableName(rs.getString(ATT_TABLE_NAME));
					h.setButtonSeatNumber(rs.getInt(ATT_BUTTON_SEAT_NO));
					res.add(h);
				} catch (SQLException e) {
					TrackerBoikLog.getInstance().log(
							Level.WARNING,
							"Impossible to retrieve hand basic details in DB: " + e.getMessage());
				}
			}
		
			return res;
		} catch (SQLException e) {
			throw new TBException("Impossible to reads hands for session '" + ps.getId() + "' !");
		}
	}

}
