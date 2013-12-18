package model.trackerboik.dao.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.trackerboik.businessobject.PokerPlayer;
import model.trackerboik.dao.PlayerDAO;

import com.trackerboik.exception.TBException;

public class PlayerSQL extends GeneralSQLDBOperations implements PlayerDAO {
	public PlayerSQL() throws TBException {
		super();
	}

	public static final String TABLE_NAME = "player";
	private static final String ATT_COMMENT = "comment";
	private static final String ATT_WINRATE = "winrate";
	private static final String ATT_BENEFIT = "benefit";
	private static final String ATT_NB_HANDS = "nb_hands";
	private static final String ATT_NB_HANDS_VPIP = "nb_hands_vpip";
	private static final String ATT_NB_RAISE_PREFLOP = "nb_hands_preflop_raise";
	
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
		rq += GEN_ATT_PLAYER_ID + " VARCHAR(256) PRIMARY KEY,";
		rq += ATT_COMMENT + " VARCHAR(256),";
		rq += ATT_WINRATE + " DOUBLE,";
		rq += ATT_BENEFIT + " DOUBLE,";
		rq += ATT_NB_HANDS + " INTEGER,";
		rq += ATT_NB_HANDS_VPIP + " INTEGER,";
		rq += ATT_NB_RAISE_PREFLOP + " INTEGER,";
		rq += ATT_NB_CBET_POSSIBLE + " INTEGER,";
		rq += ATT_NB_CBET + " INTEGER,";
		rq += ATT_NB_FOLD_TO_CBET_POSSIBLE + " INTEGER,";
		rq += ATT_NB_FOLD_TO_CBET + " INTEGER,";
		rq += ATT_NB_SECOND_BARREL_POSSIBLE + " INTEGER,";
		rq += ATT_NB_SECOND_BARREL + " INTEGER,";
		rq += ATT_NB_FOLD_TO_SECOND_BARREL_POSSIBLE + " INTEGER,";
		rq += ATT_NB_FOLD_TO_SECOND_BARREL + " INTEGER)";

		executeSQLUpdate(rq);

	}

	@Override
	public void insertPlayer(PokerPlayer pp) throws TBException {
		try {
			psInsert.setString(1, pp.getPlayerID());
			psInsert.setString(2, pp.getComment());
			psInsert.setDouble(3, 0.0);
			psInsert.setDouble(4, 0.0);
			for(int i = 5; i <= 15; i++) {
				psInsert.setInt(i, 0);
			}
			
			if(psInsert.execute()) {
				throw new TBException("Unexpected result while trying to insert player " + pp.getPlayerID());
			}
		} catch (SQLException e) {
			throw new TBException("Impossible to add player " + pp.getPlayerID() + " because: " + e.getMessage());
		}
		
	}

	@Override
	public boolean isPlayerExists(String playerID) throws TBException {
		try {
			String rq = "SELECT * FROM " + TABLE_NAME + " WHERE " + GEN_ATT_PLAYER_ID + "='" + playerID + "'";
			ResultSet rs = executeSQLQuery(rq);
			
			return rs.next();
		} catch (Exception e) {
			throw new TBException("Impossible to check Player existence in database: '" + e.getMessage() + "'");
		}
	}

	@Override
	protected String getInsertPreCompiledRequest() {
		return "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	}
	
	@Override
	protected String getExistenceTestPreCompiledRequest() {
		return "SELECT * FROM " + TABLE_NAME + " WHERE " + GEN_ATT_PLAYER_ID + " = ?";
	}

	@Override
	protected String getAllElementsForLoadSessionInMemoryRequest() {
		return getExistenceTestPreCompiledRequest();
	}

	@Override
	public void addPlayerDetails(PokerPlayer p) throws TBException {
		try {
			psQuery = createPreparedStatement(getAllElementsForLoadSessionInMemoryRequest());
			psQuery.setString(1, p.getPlayerID());
			ResultSet rs = psQuery.executeQuery();
			addPlayerDetailsFromResultSet(rs, p);
		} catch (SQLException e) {
			throw new TBException("Impossible to add player details for player " + p.getPlayerID() + ": " + e.getMessage());
		}
		
	}
	
	@Override
	public List<PokerPlayer> getPlayersWithIndicatorsToUpdate()
			throws TBException {
		try {
			List<PokerPlayer> res = new ArrayList<PokerPlayer>();
			psQuery = createPreparedStatement("SELECT * FROM " + TABLE_NAME);
			ResultSet rs = psQuery.executeQuery();
			
			while(rs.next()) {
				PokerPlayer p = new PokerPlayer(rs.getString(GEN_ATT_PLAYER_ID));
				addPlayerDetailsFromResultSet(rs, p);
				res.add(p);
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
	private void addPlayerDetailsFromResultSet(ResultSet rs, PokerPlayer p) throws TBException, SQLException {
		if(rs.next()) {
			p.setComment(rs.getString(ATT_COMMENT));
			p.winrate = rs.getDouble(ATT_WINRATE);
			p.benefitGeneral = rs.getDouble(ATT_BENEFIT);
			p.nbHand = rs.getInt(ATT_NB_HANDS);
			p.nbHandVPIP = rs.getInt(ATT_NB_HANDS_VPIP);
			p.nbHandPFR = rs.getInt(ATT_NB_RAISE_PREFLOP);
			p.nbCbetPossible = rs.getInt(ATT_NB_CBET_POSSIBLE);
			p.nbCbet = rs.getInt(ATT_NB_CBET);
			p.nbFoldToCbetPossible = rs.getInt(ATT_NB_FOLD_TO_CBET_POSSIBLE);
			p.nbFoldToCbet = rs.getInt(ATT_NB_FOLD_TO_CBET);
			p.nbSecondBarrelPossible = rs.getInt(ATT_NB_SECOND_BARREL_POSSIBLE);
			p.nbSecondBarrel = rs.getInt(ATT_NB_SECOND_BARREL);
			p.nbFoldToSecondBarrelPossible = rs.getInt(ATT_NB_FOLD_TO_SECOND_BARREL_POSSIBLE);
			p.nbFoldToSecondBarrel = rs.getInt(ATT_NB_FOLD_TO_SECOND_BARREL);
		} else {
			throw new TBException("Impossible to add player details for player " + p.getPlayerID() + ": unknow player");
		}
		
	}

	@Override
	public void updatePlayerData(PokerPlayer pp) throws TBException {
		try {
			String rq = "UPDATE " + TABLE_NAME + " SET ";
			rq += ATT_COMMENT + "=?,";
			rq += ATT_WINRATE + "=?,";
			rq += ATT_BENEFIT + "=?,";
			rq += ATT_NB_HANDS + "=?,";
			rq += ATT_NB_HANDS_VPIP + "=?,";
			rq += ATT_NB_RAISE_PREFLOP + "=?,";
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
			psQuery.setString(1, pp.getComment());
			psQuery.setDouble(2, pp.winrate);
			psQuery.setDouble(3, pp.benefitGeneral);
			psQuery.setInt(4, pp.nbHand);
			psQuery.setInt(5, pp.nbHandVPIP);
			psQuery.setInt(6, pp.nbHandPFR);
			psQuery.setInt(7, pp.nbCbetPossible);
			psQuery.setInt(8, pp.nbCbet);
			psQuery.setInt(9, pp.nbFoldToCbetPossible);
			psQuery.setInt(10, pp.nbFoldToCbet);
			psQuery.setInt(11, pp.nbSecondBarrelPossible);
			psQuery.setInt(12, pp.nbSecondBarrel);
			psQuery.setInt(13, pp.nbFoldToSecondBarrelPossible);
			psQuery.setInt(14, pp.nbFoldToSecondBarrel);
			
			psQuery.execute();
		} catch (SQLException e) {
			throw new TBException("Impossible to store player '" + pp.getPlayerID() + "' data in DB: " + e.getMessage());
		}
		
	}

	
}
