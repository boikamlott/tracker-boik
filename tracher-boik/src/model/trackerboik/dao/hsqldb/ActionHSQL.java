package model.trackerboik.dao.hsqldb;

import model.trackerboik.businessobject.ActionKind;
import model.trackerboik.businessobject.HandMoment;
import model.trackerboik.dao.ActionDAO;

import com.trackerboik.exception.TBException;
import com.trackerboik.util.BDDUtil;

public class ActionHSQL extends GeneralHSQLDBOperations implements ActionDAO {
	
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
		rq += GEN_ATT_HAND_ID + " VARCHAR(20),";
		rq += GEN_ATT_PLAYER_ID + " VARCHAR(50),";
		rq += ATT_ACTION_NUMBER + " int NOT NULL,";
		rq += ATT_AMOUNT_BET + " double NOT NULL,";
		rq += ATT_KIND + " VARCHAR(10) NOT NULL,";
		rq += ATT_MOMENT + " VARCHAR(10) NOT NULL,";
		rq += "CONSTRAINT pk_action PRIMARY KEY (" + GEN_ATT_HAND_ID + "," + GEN_ATT_PLAYER_ID + "," + ATT_ACTION_NUMBER + "),";
		rq += "CONSTRAINT fk_hand_id_player_id_a FOREIGN KEY (" + GEN_ATT_HAND_ID + "," +GEN_ATT_PLAYER_ID + ") REFERENCES " + HandPLayerHSQL.TABLE_NAME + "(" + GEN_ATT_HAND_ID + "," +GEN_ATT_PLAYER_ID + "),";	
		rq += "CONSTRAINT kind_enum CHECK (" + ATT_KIND + " in (" + kindEnumValues + ")),";
		rq += "CONSTRAINT moment_enum CHECK (" + ATT_MOMENT + " in (" + momentValues + ")))";
		
		executeSQLUpdate(rq);

	}

}
