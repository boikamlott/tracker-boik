package model.trackerboik.dao.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.trackerboik.businessobject.PlayerStats;
import model.trackerboik.dao.StatsDAO;

import com.trackerboik.exception.TBException;

public class PlayerStatsSQL extends GeneralSQLDBOperations implements
		StatsDAO {
	public static final String TABLE_NAME = "player_session_stats";

	public PlayerStatsSQL() throws TBException {
		super();
	}
	
	@Override
	public void createTable() throws TBException {
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_PLAYER_ID + " VARCHAR(256)  REFERENCES "
				+ PlayerSQL.TABLE_NAME + "(" + GEN_ATT_PLAYER_ID + "),";
		rq += ATT_BENEFIT + " DOUBLE,";
		for(String att : INT_ATTRIBUTES) {
			rq += att + " INTEGER,";
		}
		
		rq += "CONSTRAINT pk_plasyer_session_stats PRIMARY KEY ("
				+ GEN_ATT_PLAYER_ID + ")";
		rq += ")";

		executeSQLUpdate(rq);

	}

	/**
	 * Return a list with all players and the associated session to update
	 */
	@Override
	public List<PlayerStats> getPlayersWithIndicatorsToUpdate()
			throws TBException {
		try {
			List<PlayerStats> res = new ArrayList<PlayerStats>();
			String rq = "SELECT p.* " +
					  " FROM " + TABLE_NAME + " p, " + HandSQL.TABLE_NAME + " h, " + HandPLayerSQL.TABLE_NAME + " hp " 
					+ " WHERE " + GEN_ATT_HAND_DATA_CALCULATED + "=? "
					+ " AND h." + GEN_ATT_HAND_ID + "=hp." + GEN_ATT_HAND_ID
					+ " AND hp." + GEN_ATT_PLAYER_ID + "=p." + GEN_ATT_PLAYER_ID;

			psQuery = createPreparedStatement(rq);
			psQuery.setString(1, "n");
			ResultSet rs = psQuery.executeQuery();

			while (rs.next()) {
				PlayerStats pss = new PlayerStats(
						rs.getString(GEN_ATT_PLAYER_ID));
				addPlayerDetailsFromResultSet(rs, pss);
				res.add(pss);
			}

			return res;
		} catch (SQLException e) {
			throw new TBException(
					"Impossible to load all players data from database: "
							+ e.getMessage());
		}
	}
	
	/**
	 * Routine which add all available data from DB to the player object
	 * 
	 * @param rs
	 * @param p
	 * @throws TBException
	 * @throws SQLException
	 */
	private void addPlayerDetailsFromResultSet(ResultSet rs,
			PlayerStats p) throws TBException, SQLException {
		p.benefitGeneral = rs.getDouble(StatsDAO.ATT_BENEFIT);

		for(String att : StatsDAO.INT_ATTRIBUTES) {
			p.getIntegerData().put(att, rs.getInt(att));
		}
	}
	
	@Override
	public void insertPlayerStats(PlayerStats pss) throws TBException {
		try {
			int i = 1;
			psInsert.setString(i++, pss.getPlayerID());
			psInsert.setDouble(i++, 0.0);
			for (; i <= StatsDAO.INT_ATTRIBUTES.length + NB_OTHER_INDICATORS; i++) {
				psInsert.setInt(i, 0);
			}

			if (psInsert.execute()) {
				throw new TBException(
						"Unexpected result while trying to insert player "
								+ pss.getPlayerID());
			}
		} catch (SQLException e) {
			throw new TBException("Impossible to add player "
					+ pss.getPlayerID() + " because: " + e.getMessage());
		}

	}

	@Override
	public boolean isStatsExists(String playerID, String sessionID)
			throws TBException {
		try {
			psQuery = createPreparedStatement(getExistenceTestPreCompiledRequest());
			psQuery.setString(1, playerID);
			psQuery.setString(2, sessionID);
			ResultSet rs = psQuery.executeQuery();

			return rs.next();
		} catch (SQLException e) {
			throw new TBException(
					"Impossible to find player stats from database: "
							+ e.getMessage());
		}
	}

	@Override
	public void updatePlayerStats(PlayerStats pss) throws TBException {
		try {
			String rq = "UPDATE " + TABLE_NAME + " SET ";
			rq += ATT_BENEFIT + "=?,";
			
			for(String att : StatsDAO.INT_ATTRIBUTES) {
				rq += att + "=?,";
			}
			
			rq = rq.substring(0, rq.length() - 1);
			rq += " WHERE " + GEN_ATT_PLAYER_ID + "=?";

			psQuery = createPreparedStatement(rq);
			int i = 1;
			psQuery.setDouble(i++, pss.benefitGeneral);
			
			for(String att : StatsDAO.INT_ATTRIBUTES) {
				psQuery.setDouble(i++, pss.getIntegerData().get(att));
			}			
			
			psQuery.setString(i++, pss.getPlayerID());

			psQuery.execute();
		} catch (SQLException e) {
			throw new TBException("Impossible to store player '"
					+ pss.getPlayerID() + "' data in DB: " + e.getMessage());
		}

	}

	@Override
	public void getAggregatedDataForAllSession(PlayerStats playerStats) throws TBException {
		try {
		String rq = "SELECT SUM(" + StatsDAO.ATT_BENEFIT + "), ";
		for(String att : StatsDAO.INT_ATTRIBUTES) {
			rq += " SUM(" + att + "),";
		}
		rq += " WHERE " + GEN_ATT_PLAYER_ID + " = ?";
		
		psQuery = createPreparedStatement(rq);
		psQuery.setString(1, playerStats.getPlayerID());
		ResultSet rs = psQuery.executeQuery();
		
		if(rs.next()) {
			int i = 1;
			playerStats.benefitGeneral = rs.getDouble(i++);
			for(String att : StatsDAO.INT_ATTRIBUTES) {
				playerStats.getIntegerData().put(att, rs.getInt(i++));
			}
		} else {
			throw new TBException("Impossible to collect stats for all sessions for player " + playerStats.getPlayerID() + ": No data found");
		}
		} catch (SQLException e) {
			throw new TBException("Impossible to collect stats for all sessions for player " + playerStats.getPlayerID() + ": " + e.getMessage());
		}

	}

	@Override
	protected String getInsertPreCompiledRequest() {
		String rq = "INSERT INTO " + TABLE_NAME + " VALUES (";
		for (int i = 1; i < StatsDAO.INT_ATTRIBUTES.length + NB_OTHER_INDICATORS; i++) {
			rq += "?,";
		}
		rq += "?)";

		return rq;
	}

	@Override
	protected String getExistenceTestPreCompiledRequest() {
		return "SELECT * FROM " + TABLE_NAME + " WHERE " + GEN_ATT_PLAYER_ID
				+ "=? AND " + GEN_ATT_SESSION_ID + "=?";
	}

	@Override
	protected String getAllElementsForLoadSessionInMemoryRequest() {
		return getExistenceTestPreCompiledRequest();
	}

	@Override
	public void resetAllData() throws TBException {
		try {
			String rq = "UPDATE " + TABLE_NAME + " SET ";
			rq += ATT_BENEFIT + "=?,";
			
			for(String att : StatsDAO.INT_ATTRIBUTES) {
				rq += att + "=?,";
			}
			
			rq = rq.substring(0, rq.length() - 1);
			rq += " WHERE 1";

			psQuery = createPreparedStatement(rq);
			int i = 1;
			psQuery.setDouble(i++, 0.0);
			
			for(; i < StatsDAO.INT_ATTRIBUTES.length + NB_OTHER_INDICATORS; i++) {
				psQuery.setDouble(i, 0);
			}			
			
			psQuery.execute();
		} catch (SQLException e) {
			throw new TBException(e.getMessage());
		}
		
	}

}
