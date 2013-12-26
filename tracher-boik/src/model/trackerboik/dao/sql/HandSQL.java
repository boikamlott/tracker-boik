package model.trackerboik.dao.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PlayerStats;
import model.trackerboik.businessobject.PokerSession;
import model.trackerboik.dao.HandDAO;
import model.trackerboik.dao.StatsDAO;

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
			ATT_MOMENT = "moment", ATT_BUTTON_SEAT_NO = "bouton_seat_no", ATT_NB_PLAYER = "nb_players";

	@Override
	public void createTable() throws TBException {
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_HAND_ID + " VARCHAR(256) PRIMARY KEY,";
		rq += ATT_POT + " double NOT NULL,";
		rq += ATT_SITE_RAKE + " double NOT NULL,";
		rq += ATT_BB_VALUE + " double NOT NULL,";
		rq += ATT_TABLE_NAME + " VARCHAR(256),";
		rq += ATT_MOMENT + " TIMESTAMP,";
		rq += ATT_BUTTON_SEAT_NO + " INTEGER NOT NULL,";
		rq += ATT_NB_PLAYER + " INTEGER NOT NULL,";
		rq += GEN_ATT_HAND_DATA_CALCULATED + " VARCHAR(10) NOT NULL,";
		rq += GEN_ATT_SESSION_ID + " VARCHAR(256) REFERENCES "
				+ SessionSQL.TABLE_NAME + "(" + GEN_ATT_SESSION_ID + "),";
		rq +=  "CONSTRAINT hand_data_computed_bool_enum CHECK (" + GEN_ATT_HAND_DATA_CALCULATED + " in ('y', 'n'))";

		executeSQLUpdate(rq);
	}

	@Override
	public void insertHand(Hand h) throws TBException {
		try {
			int i = 1;
			psInsert.setString(i++, h.getId());
			psInsert.setDouble(i++, h.getPot());
			psInsert.setDouble(i++, h.getSiteRake());
			psInsert.setDouble(i++, h.getLimitBB());
			psInsert.setString(i++, h.getTableName());
			psInsert.setString(i++, h.getSQLFormattedMoment());
			psInsert.setInt(i++, h.getButtonSeatNumber());
			psInsert.setInt(i++, h.getNbPlayers());
			psInsert.setString(i++, "n");
			psInsert.setString(i++, h.getAssociatedSession().getId());
		
			if(psInsert.execute()) {
				throw new TBException("Unexpected result while trying to insert hand " + h.getId());
			}
		} catch (SQLException e) {
			throw new TBException("Impossible to add hand " + h.getId() + " because: " + e.getMessage());
		}
		
	}
	
	@Override
	protected String getAllElementsForLoadSessionInMemoryRequest() {
		return "SELECT * FROM " + TABLE_NAME + " WHERE " + GEN_ATT_HAND_DATA_CALCULATED + " = ?";
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
		return "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	}

	@Override
	protected String getExistenceTestPreCompiledRequest() {
		return "SELECT * FROM " + TABLE_NAME + " WHERE " + GEN_ATT_HAND_ID + " = ?";
	}


	@Override
	public List<Hand> getAllHandsUncalculated() throws TBException {
		try {
			psQuery = createPreparedStatement(getAllElementsForLoadSessionInMemoryRequest());
			psQuery.setString(1, "n");
			ResultSet rs = psQuery.executeQuery();
			List<Hand> res = new ArrayList<Hand>();
			
			while(rs.next()) {
				try {
					Hand h = new Hand(rs.getString(GEN_ATT_HAND_ID), new PokerSession(rs.getString(GEN_ATT_SESSION_ID)));
					h.setDateTime(AppUtil.parseCalendar(rs.getString(ATT_MOMENT), "yyyy-MM-dd hh:mm:ss"));
					h.setPot(rs.getDouble(ATT_POT));
					h.setSiteRake(rs.getDouble(ATT_SITE_RAKE));
					h.setLimitBB(rs.getDouble(ATT_BB_VALUE));
					h.setTableName(rs.getString(ATT_TABLE_NAME));
					h.setButtonSeatNumber(rs.getInt(ATT_BUTTON_SEAT_NO));
					h.setNbPlayers(rs.getInt(ATT_NB_PLAYER));
					res.add(h);
				} catch (SQLException e) {
					TrackerBoikLog.getInstance().log(
							Level.WARNING,
							"Impossible to retrieve hand basic details in DB: " + e.getMessage());
				}
			}
		
			return res;
		} catch (SQLException e) {
			throw new TBException("Impossible to read not computed hands !");
		}
	}
	
	@Override
	public void markAllHandsAsCalculated() throws TBException {
		try {
			psQuery = createPreparedStatement("UPDATE " + TABLE_NAME + " SET " + GEN_ATT_HAND_DATA_CALCULATED + 
																"=? WHERE " + GEN_ATT_HAND_DATA_CALCULATED + "=?");
			psQuery.setString(1, "y");
			psQuery.setString(2, "n");
			psQuery.execute();
		} catch (SQLException e) {
			throw new TBException("Error during database update on session table: " + e.getMessage());
		}
		
	}

}
