package model.trackerboik.dao.sql;

import com.trackerboik.exception.TBException;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PokerPlayer;
import model.trackerboik.dao.HandPlayerDAO;

public class HandPLayerHSQL extends GeneralSQLDBOperations implements
		HandPlayerDAO {
	public static final String TABLE_NAME = "hand_player";

	private static final String ATT_CARD_1 = "card_1", ATT_CARD_2 = "card_2", ATT_POSITION = "pos";

	private static final String ATT_IS_ALL_IN = "is_all_in";

	private static final String ATT_RESULT = "result";

	private static final String ATT_STACK_BEFORE = "stack_before";

	private static final String ATT_AMOUNT_WIN = "amount_won";

	@Override
	public void createTable() throws TBException {
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_HAND_ID + " VARCHAR(20) REFERENCES "
				+ HandSQL.TABLE_NAME + "(" + GEN_ATT_HAND_ID + "),";
		rq += GEN_ATT_PLAYER_ID + " VARCHAR(50) REFERENCES "
				+ PlayerSQL.TABLE_NAME + "(" + GEN_ATT_PLAYER_ID + "),";
		rq += ATT_CARD_1 + " VARCHAR(2),";
		rq += ATT_CARD_2 + " VARCHAR(2),";
		rq += ATT_POSITION + " INT,";
		rq += ATT_IS_ALL_IN + " VARCHAR(2),";
		rq += ATT_RESULT + " VARCHAR(50) NOT NULL,";
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
		String rq = "INSERT INTO " + TABLE_NAME + "(";
		rq += "'" + h.getId() + "',";
		rq += "'" + pp.getPlayerID() + ",";
		rq += "'" + h.getHandForPlayer(pp).getHand()[0] + "',";
		rq += "'" + h.getHandForPlayer(pp).getHand()[1] + "',";
		rq += h.getPositionForPlayer(pp).toString() + ",";
		rq += "'" + (h.getPlayerHandData(pp).isAllIn() ? "y" : "n") + "',";
		rq += "'" + h.getPlayerHandData(pp).getResult() + "',";
		rq += h.getPlayerHandData(pp).getStackBefore() + ",";
		rq += h.getPlayerHandData(pp).getAmountWin() + ")";
		
		executeSQLUpdate(rq);
		
	}

}
