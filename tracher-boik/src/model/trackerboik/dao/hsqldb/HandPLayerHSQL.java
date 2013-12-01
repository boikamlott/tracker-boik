package model.trackerboik.dao.hsqldb;

import com.trackerboik.exception.TBException;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PokerPlayer;
import model.trackerboik.dao.HandPlayerDAO;

public class HandPLayerHSQL extends GeneralHSQLDBOperations implements
		HandPlayerDAO {
	public static final String TABLE_NAME = "hand_player";

	private static final String ATT_CARD_1 = "card_1", ATT_CARD_2 = "card_2", ATT_POSITION = "pos";

	@Override
	public void createTable() throws TBException {
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_HAND_ID + " VARCHAR(20) REFERENCES "
				+ HandHSQL.TABLE_NAME + "(" + GEN_ATT_HAND_ID + "),";
		rq += GEN_ATT_PLAYER_ID + " VARCHAR(50) REFERENCES "
				+ PlayerHSQL.TABLE_NAME + "(" + GEN_ATT_PLAYER_ID + "),";
		rq += ATT_CARD_1 + " VARCHAR(2),";
		rq += ATT_CARD_2 + " VARCHAR(2),";
		rq += ATT_POSITION + " INT,";
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
		rq += h.getPositionForPlayer(pp) + ")";
		
		executeSQLUpdate(rq);
		
	}

}
