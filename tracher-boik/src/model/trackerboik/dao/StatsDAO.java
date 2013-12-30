package model.trackerboik.dao;

import java.util.Map;

import model.trackerboik.businessobject.PlayerStats;

import com.trackerboik.exception.TBException;

public interface StatsDAO extends GeneralDBOperationsDAO {

	public static final Integer NB_OTHER_INDICATORS = 2;

	public static final String ATT_BENEFIT = "benefit";
	public static final String ATT_AGRESSION_FACTOR_GENERAL_BET_RAISE = "af_general_br";
	public static final String ATT_AGRESSION_FACTOR_GENERAL_CALL = "af_general_call";

	public static final String ATT_HANDS = "hands";
	public static final String ATT_HANDS_FLOP = "hands_flop";
	public static final String ATT_HANDS_TURN = "hands_turn";
	public static final String ATT_HANDS_RIVER = "hands_river";
	public static final String ATT_HANDS_VPIP = "hands_vpip";
	public static final String ATT_RAISE_PREFLOP = "hands_pfr";

	public static final String ATT_ATS_POSSIBLE = "hands_ats";
	public static final String ATT_ATS = "ats";

	public static final String ATT_FOLD_TO_ATS_SB_POSSIBLE = "hands_fats_sb";
	public static final String ATT_FOLD_TO_ATS_BB_POSSIBLE = "hands_fats_bb";
	public static final String ATT_FOLD_TO_ATS_SB = "fats_sb";
	public static final String ATT_FOLD_TO_ATS_BB = "fats_bb";

	public static final String ATT_LIMP = "hands_limp";
	public static final String ATT_LIMP_THEN_FOLD = "hands_ltf";
	public static final String ATT_LIMP_THEN_CALL = "hands_ltc";

	public static final String ATT_3BET_POSSIBLE = "hands_3bet";
	public static final String ATT_3BET = "three_bet";

	public static final String ATT_FOLD_TO_3BET_POSSIBLE = "hands_f3bet";
	public static final String ATT_FOLD_TO_3BET = "f3bet";

	public static final String ATT_AF_FLOP_BR = "af_flop_br";
	public static final String ATT_AF_FLOP_C = "af_flop_c";

	public static final String ATT_CBET_POSSIBLE = "hands_cbet";
	public static final String ATT_CBET = "cbet";

	public static final String ATT_FOLD_TO_CBET_POSSIBLE = "hands_fcbet";
	public static final String ATT_FOLD_TO_CBET = "fcbet";

	public static final String ATT_AF_TURN_BR = "af_turn_br";
	public static final String ATT_AF_TURN_C = "af_turn_c";

	public static final String ATT_SECOND_BARREL_POSSIBLE = "hands_tcbet";
	public static final String ATT_SECOND_BARREL = "tcbet";

	public static final String ATT_FOLD_TO_SECOND_BARREL_POSSIBLE = "hands_ftcbet";
	public static final String ATT_FOLD_TO_SECOND_BARREL = "ftcet";

	public static final String ATT_AF_RIVER_BR = "af_river_br";
	public static final String ATT_AF_RIVER_C = "af_river_c";
	
	public static final String ATT_WIN_TO_SHOWDOWN_WHEN_SEEING_FLOP = "wmtsdf";
	public static final String ATT_WENT_TO_SHOWDOWN = "wtsd";
	public static final String ATT_WIN_TO_SHOWDOWN = "wmtsd";

	
	public static final String[] INT_ATTRIBUTES = new String[] {
		ATT_AGRESSION_FACTOR_GENERAL_BET_RAISE,
		ATT_AGRESSION_FACTOR_GENERAL_CALL, ATT_HANDS, ATT_HANDS_FLOP,
		ATT_HANDS_TURN, ATT_HANDS_RIVER, ATT_HANDS_VPIP,
		ATT_RAISE_PREFLOP, ATT_ATS_POSSIBLE, ATT_ATS,
		ATT_FOLD_TO_ATS_SB_POSSIBLE, ATT_FOLD_TO_ATS_BB_POSSIBLE,
		ATT_FOLD_TO_ATS_SB, ATT_FOLD_TO_ATS_BB, ATT_LIMP,
		ATT_LIMP_THEN_FOLD, ATT_LIMP_THEN_CALL,
		ATT_3BET_POSSIBLE, ATT_3BET,
		ATT_FOLD_TO_3BET_POSSIBLE, ATT_FOLD_TO_3BET, ATT_AF_FLOP_BR, ATT_AF_FLOP_C,
		ATT_CBET_POSSIBLE, ATT_CBET,
		ATT_FOLD_TO_CBET_POSSIBLE, ATT_FOLD_TO_CBET, ATT_AF_TURN_BR, ATT_AF_TURN_C,
		ATT_SECOND_BARREL_POSSIBLE, ATT_SECOND_BARREL,
		ATT_FOLD_TO_SECOND_BARREL_POSSIBLE,
		ATT_FOLD_TO_SECOND_BARREL, ATT_AF_RIVER_BR, ATT_AF_RIVER_C,
		ATT_WIN_TO_SHOWDOWN_WHEN_SEEING_FLOP, ATT_WENT_TO_SHOWDOWN, ATT_WIN_TO_SHOWDOWN };
	
	public void insertPlayerStats(PlayerStats pss) throws TBException;

	public boolean isStatsExists(String playerID, String sessionID) throws TBException;

	public void updatePlayerStats(PlayerStats pss) throws TBException;

	
	public Map<String, PlayerStats> getPlayersWithIndicatorsToUpdate()
			throws TBException;
	/**
	 * Return all aggregated data for all session in database
	 * PRE: playersStats should be has a correct player ID
	 * @param playerStats
	 */
	public void getAggregatedDataForAllSession(PlayerStats playerStats)  throws TBException;

	/**
	 * Set all indicators to zero value for all players
	 * @throws TBException
	 */
	public void resetAllData() throws TBException;
	
}
