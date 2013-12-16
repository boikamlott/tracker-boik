package model.trackerboik.dao.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.HandResult;
import model.trackerboik.businessobject.PokerCard;
import model.trackerboik.businessobject.PokerHand;
import model.trackerboik.businessobject.PokerPlayer;
import model.trackerboik.dao.HandPlayerDAO;

import com.trackerboik.exception.TBException;

import controller.trackerboik.main.TrackerBoikController;

public class HandPLayerSQL extends GeneralSQLDBOperations implements
		HandPlayerDAO {
	public HandPLayerSQL() throws TBException {
		super();
	}

	public static final String TABLE_NAME = "hand_player";

	private static final String ATT_CARD_1 = "card_1", ATT_CARD_2 = "card_2", ATT_POSITION = "pos";

	private static final String ATT_IS_ALL_IN = "is_all_in";

	private static final String ATT_RESULT = "result";

	private static final String ATT_STACK_BEFORE = "stack_before";

	private static final String ATT_AMOUNT_WIN = "amount_won";

	@Override
	public void createTable() throws TBException {
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_HAND_ID + " VARCHAR(256) REFERENCES "
				+ HandSQL.TABLE_NAME + "(" + GEN_ATT_HAND_ID + "),";
		rq += GEN_ATT_PLAYER_ID + " VARCHAR(256) REFERENCES "
				+ PlayerSQL.TABLE_NAME + "(" + GEN_ATT_PLAYER_ID + "),";
		rq += ATT_CARD_1 + " VARCHAR(2),";
		rq += ATT_CARD_2 + " VARCHAR(2),";
		rq += ATT_POSITION + " INT NOT NULL,";
		rq += ATT_IS_ALL_IN + " VARCHAR(2) NOT NULL,";
		rq += ATT_RESULT + " VARCHAR(256) NOT NULL,";
		rq += ATT_STACK_BEFORE + " DOUBLE NOT NULL,";
		rq += ATT_AMOUNT_WIN + " DOUBLE NOT NULL,";
		rq += " CONSTRAINT all_in_bool_enum CHECK (" + ATT_IS_ALL_IN + " in ('y', 'n')),";
		rq += " CONSTRAINT result_enum CHECK (" + ATT_RESULT + " in ('no_bet', 'fold_preflop', 'fold_flop', 'fold_turn', 'fold_river', 'loose', 'win')),";
		rq += "CONSTRAINT pk_hand_player PRIMARY KEY (" + GEN_ATT_HAND_ID + ","
				+ GEN_ATT_PLAYER_ID + "))";

		executeSQLUpdate(rq);
	}

	@Override
	public void insertHandPlayer(Hand h, PokerPlayer pp) throws TBException {
		try {
		
			psInsert.setString(1, h.getId());
			psInsert.setString(2, pp.getPlayerID());
			psInsert.setString(3, h.getHandForPlayer(pp) == null ?  "" : h.getHandForPlayer(pp).getHand()[0].toString());
			psInsert.setString(4, h.getHandForPlayer(pp) == null ?  "" : h.getHandForPlayer(pp).getHand()[1].toString());
			psInsert.setInt(5, h.getPositionForPlayer(pp));
			psInsert.setString(6, (h.getPlayerHandData(pp).isAllIn() ? "y" : "n"));
			psInsert.setString(7, h.getPlayerHandData(pp).getResult().getTxtResult());
			psInsert.setDouble(8, h.getPlayerHandData(pp).getStackBefore());
			psInsert.setDouble(9, h.getPlayerHandData(pp).getAmountWin());
		
			if(psInsert.execute()) {
				throw new TBException("Unexpected result while trying to insert hand_player (" + h.getId() + "," + pp.getPlayerID() + ")");
			}
		} catch (SQLException e) {
			throw new TBException("Impossible to add hand_player (" + h.getId() + "," + pp.getPlayerID() + ")" + " because: " + e.getMessage());
		}
	}

	@Override
	protected String getInsertPreCompiledRequest() {
		return "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	}

	@Override
	protected String getExistenceTestPreCompiledRequest() {
		return "SELECT * FROM " + TABLE_NAME + " WHERE " + GEN_ATT_PLAYER_ID + " = ? AND " + GEN_ATT_HAND_ID + " = ?";
	}

	@Override
	protected String getAllElementsRequest() {
		return "SELECT * FROM " + TABLE_NAME + " WHERE " + GEN_ATT_HAND_ID + " = ?";
	}

	@Override
	public void addPlayerDataForHand(Hand h) throws TBException {
		try {
			psQuery = createPreparedStatement(getAllElementsRequest());
			psQuery.setString(1, h.getId());
			ResultSet rs = psQuery.executeQuery();
			
			while(rs.next()) {
				PokerPlayer pp = new PokerPlayer(rs.getString(GEN_ATT_PLAYER_ID));
				if(TrackerBoikController.getInstance().getPlayers().contains(pp)) {
					pp = TrackerBoikController.getInstance().getPlayers().get(TrackerBoikController.getInstance().getPlayers().indexOf(pp));
				}
				h.addPlayerToHand(pp);
				h.setAmountWonForPlayer(pp, rs.getDouble(ATT_AMOUNT_WIN));
				h.setPositionForPlayer(pp, rs.getInt(ATT_POSITION));
				h.setResultHandForPlayer(pp, HandResult.readHandResult(rs.getString(ATT_RESULT)));
				h.setStartStackForPlayer(pp, rs.getDouble(ATT_STACK_BEFORE));
				if(rs.getString(ATT_IS_ALL_IN).equals("y")) {
					h.upAllInFlagForPlayer(pp);
				}
				PokerHand ph = new PokerHand();
				ph.setHand(PokerCard.readCard(rs.getString(ATT_CARD_1)), PokerCard.readCard(rs.getString(ATT_CARD_2)));
				h.setHandForPlayer(pp, ph);
			}
		} catch (SQLException e) {
			throw new TBException("Impossible to retrieve players for hand " + h.getId());
		}
		
	}

}
