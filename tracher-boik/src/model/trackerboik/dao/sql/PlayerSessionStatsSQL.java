package model.trackerboik.dao.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.trackerboik.exception.TBException;

import model.trackerboik.businessobject.PlayerSessionStats;
import model.trackerboik.businessobject.PokerSession;
import model.trackerboik.dao.PlayerSessionStatsDAO;

public class PlayerSessionStatsSQL extends GeneralSQLDBOperations implements PlayerSessionStatsDAO {
	public static final String TABLE_NAME = "player_session_stats";

	
	public PlayerSessionStatsSQL() throws TBException {
		super();
	}

	private static final String ATT_WINRATE = "winrate";
	private static final String ATT_BENEFIT = "benefit";
	private static final String ATT_NB_AGRESSION_FACTOR_BET_RAISE = "nb_hand_af_bet_raise";
	private static final String ATT_NB_AGRESSION_FACTOR_CALL = "nb_hand_af_call";

	private static final Integer NB_INTEGER_INDICATORS = 23;
	private static final Integer NB_OTHER_INDICATORS = 4;

	private static final String ATT_NB_HANDS = "nb_hands";
	private static final String ATT_NB_HANDS_VPIP = "nb_hands_vpip";
	private static final String ATT_NB_RAISE_PREFLOP = "nb_hands_preflop_raise";
	
	private static final String ATT_NB_ATS_POSSIBLE = "nb_hands_ats_possible";
	private static final String ATT_NB_ATS = "nb_hands_ats";

	private static final String ATT_NB_FOLD_TO_ATS_SB_POSSIBLE = "nb_hands_fold_to_ats_sb_possible";
	private static final String ATT_NB_FOLD_TO_ATS_BB_POSSIBLE = "nb_hands_fold_to_ats_bb_possible";
	private static final String ATT_NB_FOLD_TO_ATS_SB = "nb_hands_fold_to_ats_sb";
	private static final String ATT_NB_FOLD_TO_ATS_BB = "nb_hands_fold_to_ats_bb";

	private static final String ATT_NB_3BET_POSSIBLE = "nb_hands_3bet_possible";
	private static final String ATT_NB_3BET = "nb_3bet";
	
	private static final String ATT_NB_FOLD_TO_3BET_POSSIBLE = "nb_hands_fold_to_3bet_possible";
	private static final String ATT_NB_FOLD_TO_3BET = "nb_hands_fold_to_3bet";
	
	private static final String ATT_NB_CBET_POSSIBLE = "nb_cbet_possible";
	private static final String ATT_NB_CBET = "nb_cbet";
	
	private static final String ATT_NB_FOLD_TO_CBET_POSSIBLE = "nb_fold_to_cbet_possible";
	private static final String ATT_NB_FOLD_TO_CBET = "nb_fold_to_cbet";
	
	private static final String ATT_NB_SECOND_BARREL_POSSIBLE = "nb_second_barrel_possible";
	private static final String ATT_NB_SECOND_BARREL = "nb_second_barrel";
	
	private static final String ATT_NB_FOLD_TO_SECOND_BARREL_POSSIBLE = "nb_fold_to_second_barrel_possible";
	private static final String ATT_NB_FOLD_TO_SECOND_BARREL = "nb_fold_to_second_barrel";
	
	
	@Override
	public void createTable() throws TBException {
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_PLAYER_ID + " VARCHAR(256)  REFERENCES " + PlayerSQL.TABLE_NAME + "(" + GEN_ATT_PLAYER_ID + "),";
		rq += GEN_ATT_SESSION_ID + " VARCHAR(256)  REFERENCES " + SessionSQL.TABLE_NAME + "(" + GEN_ATT_SESSION_ID + "),";
		rq += ATT_WINRATE + " DOUBLE,";
		rq += ATT_BENEFIT + " DOUBLE,";
		rq += ATT_NB_AGRESSION_FACTOR_BET_RAISE + " INTEGER,";
		rq += ATT_NB_AGRESSION_FACTOR_CALL + " INTEGER,";
		rq += ATT_NB_HANDS + " INTEGER,";
		rq += ATT_NB_HANDS_VPIP + " INTEGER,";
		rq += ATT_NB_RAISE_PREFLOP + " INTEGER,";
		rq += ATT_NB_ATS_POSSIBLE + " INTEGER,";
		rq += ATT_NB_ATS + " INTEGER,";
		rq += ATT_NB_FOLD_TO_ATS_SB_POSSIBLE + " INTEGER,";
		rq += ATT_NB_FOLD_TO_ATS_BB_POSSIBLE + " INTEGER,";
		rq += ATT_NB_FOLD_TO_ATS_SB + " INTEGER,";
		rq += ATT_NB_FOLD_TO_ATS_BB + " INTEGER,";
		rq += ATT_NB_3BET_POSSIBLE + " INTEGER,";
		rq += ATT_NB_3BET + " INTEGER,";
		rq += ATT_NB_FOLD_TO_3BET_POSSIBLE + " INTEGER,";
		rq += ATT_NB_FOLD_TO_3BET + " INTEGER,";
		rq += ATT_NB_CBET_POSSIBLE + " INTEGER,";
		rq += ATT_NB_CBET + " INTEGER,";
		rq += ATT_NB_FOLD_TO_CBET_POSSIBLE + " INTEGER,";
		rq += ATT_NB_FOLD_TO_CBET + " INTEGER,";
		rq += ATT_NB_SECOND_BARREL_POSSIBLE + " INTEGER,";
		rq += ATT_NB_SECOND_BARREL + " INTEGER,";
		rq += ATT_NB_FOLD_TO_SECOND_BARREL_POSSIBLE + " INTEGER,";
		rq += ATT_NB_FOLD_TO_SECOND_BARREL + " INTEGER,";
		rq += "CONSTRAINT pk_plasyer_session_stats PRIMARY KEY (" + GEN_ATT_PLAYER_ID + "," + GEN_ATT_SESSION_ID + ")";
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
			for(int j = i; j <= NB_INTEGER_INDICATORS + NB_OTHER_INDICATORS; i++) {
				psInsert.setInt(j, 0);
			}
			
			if(psInsert.execute()) {
				throw new TBException("Unexpected result while trying to insert player " + pss.getPlayerID());
			}
		} catch (SQLException e) {
			throw new TBException("Impossible to add player " + pss.getPlayerID() + " because: " + e.getMessage());
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
			throw new TBException("Impossible to find player stats from database: " + e.getMessage());
		}
	}

	@Override
	public List<PlayerSessionStats> getPlayersWithIndicatorsToUpdate(PokerSession ps)
			throws TBException {
		try {
			List<PlayerSessionStats> res = new ArrayList<PlayerSessionStats>();
			psQuery = createPreparedStatement("SELECT * FROM " + TABLE_NAME + " WHERE " + GEN_ATT_SESSION_ID + "=?");
			psQuery.setString(1, ps.getId());
			ResultSet rs = psQuery.executeQuery();
			
			while(rs.next()) {
				PlayerSessionStats pss = new PlayerSessionStats(rs.getString(GEN_ATT_PLAYER_ID), ps);
				addPlayerDetailsFromResultSet(rs, pss);
				res.add(pss);
			}
			
			return res;
		} catch (SQLException e) {
			throw new TBException("Impossible to load all players data from database: " + e.getMessage());
		}
	}
	
	/**
	 * Routine which add all available data from DB to the player object
	 * @param rs
	 * @param p
	 * @throws TBException
	 * @throws SQLException
	 */
	private void addPlayerDetailsFromResultSet(ResultSet rs, PlayerSessionStats p) throws TBException, SQLException {
			p.winrate = rs.getDouble(ATT_WINRATE);
			p.benefitGeneral = rs.getDouble(ATT_BENEFIT);
			
			p.nbAFHandBetAndRaise = rs.getInt(ATT_NB_AGRESSION_FACTOR_BET_RAISE);
			p.nbAFHandCalled = rs.getInt(ATT_NB_AGRESSION_FACTOR_CALL);
			
			p.nbHand = rs.getInt(ATT_NB_HANDS);
			p.nbHandVPIP = rs.getInt(ATT_NB_HANDS_VPIP);
			p.nbHandPFR = rs.getInt(ATT_NB_RAISE_PREFLOP);
			
			p.nbATSPossible = rs.getInt(ATT_NB_ATS_POSSIBLE);
			p.nbATS = rs.getInt(ATT_NB_ATS);
			p.nbFoldToATSSBPossible = rs.getInt(ATT_NB_FOLD_TO_ATS_SB_POSSIBLE);
			p.nbFoldToATSBBPossible = rs.getInt(ATT_NB_FOLD_TO_ATS_BB_POSSIBLE);
			p.nbFoldToATSSB = rs.getInt(ATT_NB_FOLD_TO_ATS_SB);
			p.nbFoldToATSBB = rs.getInt(ATT_NB_FOLD_TO_ATS_BB);
			
			p.nb3betPossible = rs.getInt(ATT_NB_3BET_POSSIBLE);
			p.nb3bet = rs.getInt(ATT_NB_3BET);
			p.nbFoldTo3betPossible = rs.getInt(ATT_NB_FOLD_TO_3BET_POSSIBLE);
			p.nbFoldTo3bet = rs.getInt(ATT_NB_FOLD_TO_3BET);
			
			p.nbCbetPossible = rs.getInt(ATT_NB_CBET_POSSIBLE);
			p.nbCbet = rs.getInt(ATT_NB_CBET);
			p.nbFoldToCbetPossible = rs.getInt(ATT_NB_FOLD_TO_CBET_POSSIBLE);
			p.nbFoldToCbet = rs.getInt(ATT_NB_FOLD_TO_CBET);
			
			p.nbSecondBarrelPossible = rs.getInt(ATT_NB_SECOND_BARREL_POSSIBLE);
			p.nbSecondBarrel = rs.getInt(ATT_NB_SECOND_BARREL);
			p.nbFoldToSecondBarrelPossible = rs.getInt(ATT_NB_FOLD_TO_SECOND_BARREL_POSSIBLE);
			p.nbFoldToSecondBarrel = rs.getInt(ATT_NB_FOLD_TO_SECOND_BARREL);
	}

	@Override
	public void updatePlayerStats(PlayerSessionStats pss) throws TBException {
		try {
			String rq = "UPDATE " + TABLE_NAME + " SET ";
			rq += ATT_WINRATE + "=?,";
			rq += ATT_BENEFIT + "=?,";
			rq += ATT_NB_AGRESSION_FACTOR_BET_RAISE + "=?,";
			rq += ATT_NB_AGRESSION_FACTOR_CALL + "=?,";
			rq += ATT_NB_HANDS + "=?,";
			rq += ATT_NB_HANDS_VPIP + "=?,";
			rq += ATT_NB_RAISE_PREFLOP + "=?,";
			rq += ATT_NB_ATS_POSSIBLE + "=?,";
			rq += ATT_NB_ATS + "=?,";
			rq += ATT_NB_FOLD_TO_ATS_SB_POSSIBLE + "=?,";
			rq += ATT_NB_FOLD_TO_ATS_BB_POSSIBLE + "=?,";
			rq += ATT_NB_FOLD_TO_ATS_SB + "=?,";
			rq += ATT_NB_FOLD_TO_ATS_BB + "=?,";
			rq += ATT_NB_3BET_POSSIBLE + "=?,";
			rq += ATT_NB_3BET + "=?,";
			rq += ATT_NB_FOLD_TO_3BET_POSSIBLE + "=?,";
			rq += ATT_NB_FOLD_TO_3BET + "=?,";
			rq += ATT_NB_CBET_POSSIBLE + "=?,";
			rq += ATT_NB_CBET + "=?,";
			rq += ATT_NB_FOLD_TO_CBET_POSSIBLE + "=?,";
			rq += ATT_NB_FOLD_TO_CBET + "=?,";
			rq += ATT_NB_SECOND_BARREL_POSSIBLE + "=?,";
			rq += ATT_NB_SECOND_BARREL + "=?,";
			rq += ATT_NB_FOLD_TO_SECOND_BARREL_POSSIBLE + "=?,";
			rq += ATT_NB_FOLD_TO_SECOND_BARREL + "=? ";
			rq += " WHERE " + GEN_ATT_PLAYER_ID + "=?";
			
			psQuery = createPreparedStatement(rq);
			int i = 1;
			psQuery.setDouble(i++, pss.winrate);
			psQuery.setDouble(i++, pss.benefitGeneral);
			psQuery.setDouble(i++, pss.nbAFHandBetAndRaise);
			psQuery.setDouble(i++, pss.nbAFHandCalled);
			psQuery.setInt(i++, pss.nbHand);
			psQuery.setInt(i++, pss.nbHandVPIP);
			psQuery.setInt(i++, pss.nbHandPFR);
			psQuery.setInt(i++, pss.nbATSPossible);
			psQuery.setInt(i++, pss.nbATS);
			psQuery.setInt(i++, pss.nbFoldToATSSBPossible);
			psQuery.setInt(i++, pss.nbFoldToATSBBPossible);
			psQuery.setInt(i++, pss.nbFoldToATSSB);
			psQuery.setInt(i++, pss.nbFoldToATSBB);
			psQuery.setInt(i++, pss.nb3betPossible);
			psQuery.setInt(i++, pss.nb3bet);
			psQuery.setInt(i++, pss.nbFoldTo3bet);
			psQuery.setInt(i++, pss.nbFoldTo3betPossible);
			psQuery.setInt(i++, pss.nbCbetPossible);
			psQuery.setInt(i++, pss.nbCbet);
			psQuery.setInt(i++, pss.nbFoldToCbetPossible);
			psQuery.setInt(i++, pss.nbFoldToCbet);
			psQuery.setInt(i++, pss.nbSecondBarrelPossible);
			psQuery.setInt(i++, pss.nbSecondBarrel);
			psQuery.setInt(i++, pss.nbFoldToSecondBarrelPossible);
			psQuery.setInt(i++, pss.nbFoldToSecondBarrel);
			psQuery.setString(i++, pss.getPlayerID());
			
			psQuery.execute();
		} catch (SQLException e) {
			throw new TBException("Impossible to store player '" + pss.getPlayerID() + "' data in DB: " + e.getMessage());
		}
		
	}

	@Override
	protected String getInsertPreCompiledRequest() {
		String rq = "INSERT INTO " + TABLE_NAME + " VALUES (";
		for(int i = 1; i < NB_INTEGER_INDICATORS + NB_OTHER_INDICATORS; i++) {
			rq += "?,";
		}
		rq += "?)";
		
		return rq;
	}

	@Override
	protected String getExistenceTestPreCompiledRequest() {
		return "SELECT * FROM " + TABLE_NAME + " WHERE " + GEN_ATT_PLAYER_ID + "=? AND " + GEN_ATT_SESSION_ID + "=?";
	}

	@Override
	protected String getAllElementsForLoadSessionInMemoryRequest() {
		return getExistenceTestPreCompiledRequest();
	}


}
