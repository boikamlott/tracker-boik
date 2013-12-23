package model.trackerboik.dao.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.trackerboik.businessobject.PlayerSessionStats;
import model.trackerboik.businessobject.PokerSession;
import model.trackerboik.dao.PlayerSessionStatsDAO;

import com.trackerboik.exception.TBException;

public class PlayerSessionStatsSQL extends GeneralSQLDBOperations implements
		PlayerSessionStatsDAO {
	public static final String TABLE_NAME = "player_session_stats";

	public PlayerSessionStatsSQL() throws TBException {
		super();
	}
	
	@Override
	public void createTable() throws TBException {
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_PLAYER_ID + " VARCHAR(256)  REFERENCES "
				+ PlayerSQL.TABLE_NAME + "(" + GEN_ATT_PLAYER_ID + "),";
		rq += GEN_ATT_SESSION_ID + " VARCHAR(256)  REFERENCES "
				+ SessionSQL.TABLE_NAME + "(" + GEN_ATT_SESSION_ID + "),";
		rq += ATT_WINRATE + " DOUBLE,";
		rq += ATT_BENEFIT + " DOUBLE,";
		for(String att : INT_ATTRIBUTES) {
			rq += att + " INTEGER,";
		}
		
		rq += "CONSTRAINT pk_plasyer_session_stats PRIMARY KEY ("
				+ GEN_ATT_PLAYER_ID + "," + GEN_ATT_SESSION_ID + ")";
		rq += ")";

		executeSQLUpdate(rq);

	}

	@Override
	public void insertPlayerStats(PlayerSessionStats pss) throws TBException {
		try {
			int i = 1;
			psInsert.setString(i++, pss.getPlayerID());
			psInsert.setString(i++, pss.getSession().getId());
			psInsert.setDouble(i++, 0.0);
			psInsert.setDouble(i++, 0.0);
			for (; i <= NB_INTEGER_INDICATORS + NB_OTHER_INDICATORS; i++) {
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
	public List<PlayerSessionStats> getPlayersWithIndicatorsToUpdate(
			PokerSession ps) throws TBException {
		try {
			List<PlayerSessionStats> res = new ArrayList<PlayerSessionStats>();
			psQuery = createPreparedStatement("SELECT * FROM " + TABLE_NAME
					+ " WHERE " + GEN_ATT_SESSION_ID + "=?");
			psQuery.setString(1, ps.getId());
			ResultSet rs = psQuery.executeQuery();

			while (rs.next()) {
				PlayerSessionStats pss = new PlayerSessionStats(
						rs.getString(GEN_ATT_PLAYER_ID), ps);
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
			PlayerSessionStats p) throws TBException, SQLException {
		p.winrate = rs.getDouble(ATT_WINRATE);
		p.benefitGeneral = rs.getDouble(ATT_BENEFIT);

		for(String att : PlayerSessionStatsDAO.INT_ATTRIBUTES) {
			p.getIntegerData().put(att, rs.getInt(att));
		}
	}

	@Override
	public void updatePlayerStats(PlayerSessionStats pss) throws TBException {
		try {
			String rq = "UPDATE " + TABLE_NAME + " SET ";
			rq += ATT_WINRATE + "=?,";
			rq += ATT_BENEFIT + "=?,";
			
			for(String att : PlayerSessionStatsDAO.INT_ATTRIBUTES) {
				rq += att + "=?,";
			}
			
			rq += " WHERE " + GEN_ATT_PLAYER_ID + "=?";

			psQuery = createPreparedStatement(rq);
			int i = 1;
			psQuery.setDouble(i++, pss.winrate);
			psQuery.setDouble(i++, pss.benefitGeneral);
			
			for(String att : PlayerSessionStatsDAO.INT_ATTRIBUTES) {
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
	public void getAggregatedDataForAllSession(PlayerSessionStats playerStats) throws TBException {
		try {
		String rq = "SELECT SUM(" + PlayerSessionStatsDAO.ATT_BENEFIT + "), ";
		for(String att : PlayerSessionStatsDAO.INT_ATTRIBUTES) {
			rq += " SUM(" + att + "),";
		}
		rq += " WHERE " + GEN_ATT_PLAYER_ID + " = ?";
		
		psQuery = createPreparedStatement(rq);
		psQuery.setString(1, playerStats.getPlayerID());
		ResultSet rs = psQuery.executeQuery();
		
		if(rs.next()) {
			int i = 1;
			playerStats.benefitGeneral = rs.getDouble(i++);
			for(String att : PlayerSessionStatsDAO.INT_ATTRIBUTES) {
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
		for (int i = 1; i < NB_INTEGER_INDICATORS + NB_OTHER_INDICATORS; i++) {
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

}
