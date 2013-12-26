package model.trackerboik.dao;

import java.util.List;

import model.trackerboik.businessobject.PlayerSessionStats;
import model.trackerboik.businessobject.PokerSession;

import com.trackerboik.exception.TBException;

public interface PlayerSessionStatsDAO extends GeneralDBOperationsDAO {

	public static final Integer NB_INTEGER_INDICATORS = 28;
	public static final Integer NB_OTHER_INDICATORS = 4;

	public static final String ATT_WINRATE = "winrate";
	public static final String ATT_BENEFIT = "benefit";
	public static final String ATT_NB_AGRESSION_FACTOR_GENERAL_BET_RAISE = "NB_AF_General_BR";
	public static final String ATT_NB_AGRESSION_FACTOR_GENERAL_CALL = "NB_AF_General_Call";

	public static final String ATT_NB_HANDS = "NB_Hands";
	public static final String ATT_NB_HANDS_VPIP = "NB_Hands_VPIP";
	public static final String ATT_NB_RAISE_PREFLOP = "NB_Hands_PFR";

	public static final String ATT_NB_ATS_POSSIBLE = "NB_Hands_ATS";
	public static final String ATT_NB_ATS = "NB_ATS";

	public static final String ATT_NB_FOLD_TO_ATS_SB_POSSIBLE = "NB_Hands_FATS_SB";
	public static final String ATT_NB_FOLD_TO_ATS_BB_POSSIBLE = "NB_Hands_FATS_BB";
	public static final String ATT_NB_FOLD_TO_ATS_SB = "NB_FATS_SB";
	public static final String ATT_NB_FOLD_TO_ATS_BB = "NB_FATS_BB";

	public static final String ATT_NB_LIMP = "NB_Hands_Limp";
	public static final String ATT_NB_LIMP_THEN_FOLD = "NB_Hands_LTF";
	public static final String ATT_NB_LIMP_THEN_CALL = "NB_Hands_LTC";

	public static final String ATT_NB_3BET_POSSIBLE = "NB_Hands_3Bet";
	public static final String ATT_NB_3BET = "NB_3Bet";

	public static final String ATT_NB_FOLD_TO_3BET_POSSIBLE = "NB_Hands_F3Bet";
	public static final String ATT_NB_FOLD_TO_3BET = "NB_F3Bet";

	public static final String ATT_NB_AF_FLOP_BR = "NB_AF_Flop_BR";
	public static final String ATT_NB_AF_FLOP_C = "NB_AF_Flop_C";

	public static final String ATT_NB_CBET_POSSIBLE = "NB_Hands_CBet";
	public static final String ATT_NB_CBET = "NB_CBet";

	public static final String ATT_NB_FOLD_TO_CBET_POSSIBLE = "NB_Hands_FCBet";
	public static final String ATT_NB_FOLD_TO_CBET = "NB_FCBet";

	public static final String ATT_NB_AF_TURN_BR = "NB_AF_Turn_BR";
	public static final String ATT_NB_AF_TURN_C = "NB_AF_Turn_C";

	public static final String ATT_NB_SECOND_BARREL_POSSIBLE = "NB_Hands_TCBet";
	public static final String ATT_NB_SECOND_BARREL = "NB_TCBet";

	public static final String ATT_NB_FOLD_TO_SECOND_BARREL_POSSIBLE = "NB_Hands_FTCBet";
	public static final String ATT_NB_FOLD_TO_SECOND_BARREL = "NB_FTCBet";

	public static final String ATT_NB_AF_RIVER_BR = "NB_AF_River_BR";
	public static final String ATT_NB_AF_RIVER_C = "NB_AF_River_BR";
	
	public static final String ATT_NB_WIN_TO_SHOWDOWN_WHEN_SEEING_FLOP_POSSIBLE = "NB_Hands_WMTSDWSF";
	public static final String ATT_NB_WIN_TO_SHOWDOWN_WHEN_SEEING_FLOP = "NB_WMTSDWSF";
	public static final String ATT_NB_WENT_TO_SHOWDOWN = "NB_WTSD";
	public static final String ATT_NB_WIN_TO_SHOWDOWN = "NB_WMTSD";

	
	public static final String[] INT_ATTRIBUTES = new String[] {
		ATT_NB_AGRESSION_FACTOR_GENERAL_BET_RAISE,
		ATT_NB_AGRESSION_FACTOR_GENERAL_CALL, ATT_NB_HANDS, ATT_NB_HANDS_VPIP,
		ATT_NB_RAISE_PREFLOP, ATT_NB_ATS_POSSIBLE, ATT_NB_ATS,
		ATT_NB_FOLD_TO_ATS_SB_POSSIBLE, ATT_NB_FOLD_TO_ATS_BB_POSSIBLE,
		ATT_NB_FOLD_TO_ATS_SB, ATT_NB_FOLD_TO_ATS_BB, ATT_NB_LIMP,
		ATT_NB_LIMP_THEN_FOLD, ATT_NB_LIMP_THEN_CALL,
		ATT_NB_3BET_POSSIBLE, ATT_NB_3BET,
		ATT_NB_FOLD_TO_3BET_POSSIBLE, ATT_NB_FOLD_TO_3BET, ATT_NB_AF_FLOP_BR, ATT_NB_AF_FLOP_C,
		ATT_NB_CBET_POSSIBLE, ATT_NB_CBET,
		ATT_NB_FOLD_TO_CBET_POSSIBLE, ATT_NB_FOLD_TO_CBET, ATT_NB_AF_TURN_BR, ATT_NB_AF_TURN_C,
		ATT_NB_SECOND_BARREL_POSSIBLE, ATT_NB_SECOND_BARREL,
		ATT_NB_FOLD_TO_SECOND_BARREL_POSSIBLE,
		ATT_NB_FOLD_TO_SECOND_BARREL, ATT_NB_AF_RIVER_BR, ATT_NB_AF_RIVER_C,
		ATT_NB_WIN_TO_SHOWDOWN_WHEN_SEEING_FLOP, ATT_NB_WENT_TO_SHOWDOWN, ATT_NB_WIN_TO_SHOWDOWN };
	
	public void insertPlayerStats(PlayerSessionStats pss) throws TBException;

	public boolean isStatsExists(String playerID, String sessionID) throws TBException;

	public List<PlayerSessionStats> getPlayersWithIndicatorsToUpdate(PokerSession ps) throws TBException;

	public void updatePlayerStats(PlayerSessionStats pss) throws TBException;

	/**
	 * Return all aggregated data for all session in database
	 * PRE: playersStats should be has a correct player ID
	 * @param playerStats
	 */
	public void getAggregatedDataForAllSession(PlayerSessionStats playerStats)  throws TBException;
	
}
