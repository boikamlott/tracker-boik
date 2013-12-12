package model.trackerboik.dao.sql;

import java.sql.SQLException;

import model.trackerboik.businessobject.ActionKind;
import model.trackerboik.businessobject.HandMoment;
import model.trackerboik.businessobject.PokerAction;
import model.trackerboik.dao.ActionDAO;

import com.trackerboik.exception.TBException;
import com.trackerboik.util.BDDUtil;

public class ActionSQL extends GeneralSQLDBOperations implements ActionDAO {
	
	public ActionSQL() throws TBException {
		super();
	}

	public static final String TABLE_NAME = "action";
	
	private static final String ATT_ACTION_NUMBER = "action_number",
								ATT_AMOUNT_BET = "amount_bet",
								ATT_KIND = "kind",
								ATT_MOMENT = "moment";
	
	@Override
	public void createTable() throws TBException {
		String kindEnumValues = BDDUtil.getEnumValuesToStringForBDD(ActionKind.values());
		String momentValues = BDDUtil.getEnumValuesToStringForBDD(HandMoment.values());
		
		String rq = "CREATE TABLE " + TABLE_NAME + " (";
		rq += GEN_ATT_HAND_ID + " VARCHAR(256),";
		rq += GEN_ATT_PLAYER_ID + " VARCHAR(256),";
		rq += ATT_ACTION_NUMBER + " int NOT NULL,";
		rq += ATT_AMOUNT_BET + " double NOT NULL,";
		rq += ATT_KIND + " VARCHAR(10) NOT NULL,";
		rq += ATT_MOMENT + " VARCHAR(10) NOT NULL,";
		rq += "CONSTRAINT pk_action PRIMARY KEY (" + GEN_ATT_HAND_ID + "," + GEN_ATT_PLAYER_ID + "," + ATT_ACTION_NUMBER + "),";
		rq += "CONSTRAINT fk_hand_id_player_id_a FOREIGN KEY (" + GEN_ATT_HAND_ID + "," +GEN_ATT_PLAYER_ID + ") REFERENCES " + HandPLayerSQL.TABLE_NAME + "(" + GEN_ATT_HAND_ID + "," +GEN_ATT_PLAYER_ID + "),";	
		rq += "CONSTRAINT kind_enum CHECK (" + ATT_KIND + " in (" + kindEnumValues + ")),";
		rq += "CONSTRAINT moment_enum CHECK (" + ATT_MOMENT + " in (" + momentValues + ")))";
		
		executeSQLUpdate(rq);

	}

	@Override
	public void insertAction(PokerAction a) throws TBException {
		try {
		
			psInsert.setString(1, a.getHand().getId());
			psInsert.setString(2, a.getAssociatedPlayer().getPlayerID());
			psInsert.setInt(3, a.getActNoForHand());
			psInsert.setDouble(4, a.getAmountBet() == null ? 0.0 : a.getAmountBet());
			psInsert.setString(5, a.getKind().toString());
			psInsert.setString(6, a.getMoment().toString());
		
			if(psInsert.execute()) {
				throw new TBException("Unexpected result while trying to insert action n°" + a.getActNoForHand() + " for hand " + a.getHand().getId());
			}
		} catch (SQLException e) {
			throw new TBException("Impossible to add action n°" + a.getActNoForHand() + " for hand " + a.getHand().getId() + " because: " + e.getMessage());
		}
	}

	@Override
	protected String getInsertPreCompiledRequest() {
		return "INSERT INTO " + TABLE_NAME + " VALUES (?, ?, ?, ?, ?, ?)";
	}

}
