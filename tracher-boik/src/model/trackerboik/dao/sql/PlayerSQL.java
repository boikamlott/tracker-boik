package model.trackerboik.dao.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.HandResult;
import model.trackerboik.businessobject.PokerCard;
import model.trackerboik.businessobject.PokerHand;
import model.trackerboik.businessobject.PokerPlayer;
import model.trackerboik.dao.PlayerDAO;

import com.trackerboik.exception.TBException;

import controller.trackerboik.main.TrackerBoikController;

public class PlayerSQL extends GeneralSQLDBOperations implements PlayerDAO {
	public PlayerSQL() throws TBException {
		super();
	}

	public static final String TABLE_NAME = "player";
	private static final String ATT_COMMENT = "comment";

	
	@Override
	public void createTable() throws TBException {
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_PLAYER_ID + " VARCHAR(256) PRIMARY KEY,";
		rq += ATT_COMMENT + " VARCHAR(256))";

		executeSQLUpdate(rq);

	}

	@Override
	public void insertPlayer(PokerPlayer pp) throws TBException {
		try {
			psInsert.setString(1, pp.getPlayerID());
			psInsert.setString(2, pp.getComment());
			
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
		return "INSERT INTO " + TABLE_NAME + " VALUES (?, ?)";
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
	public void updatePlayerData(PokerPlayer pp) throws TBException {
		try {
			String rq = "UPDATE " + TABLE_NAME + " SET ";
			rq += ATT_COMMENT + "=?,";
			rq += " WHERE " + GEN_ATT_PLAYER_ID + "=?";
			
			psQuery = createPreparedStatement(rq);
			int i = 1;
			psQuery.setString(i++, pp.getComment());
			psQuery.setString(i++, pp.getPlayerID());
			
			psQuery.execute();
		} catch (SQLException e) {
			throw new TBException("Impossible to store player '" + pp.getPlayerID() + "' data in DB: " + e.getMessage());
		}
		
	}

	@Override
	public void addPlayersDetails(PokerPlayer p) throws TBException {
		try {
			psQuery = createPreparedStatement(getAllElementsForLoadSessionInMemoryRequest());
			int i = 1;
			psQuery.setString(i++, p.getPlayerID());
			ResultSet rs = psQuery.executeQuery();
			if(rs.next()) {
				p.setComment(rs.getString(ATT_COMMENT));
			}
		} catch (SQLException e) {
			throw new TBException("Impossible to retrieve player '" + p.getPlayerID() + "' data in DB: " + e.getMessage());
		}
		
	}

	
}
