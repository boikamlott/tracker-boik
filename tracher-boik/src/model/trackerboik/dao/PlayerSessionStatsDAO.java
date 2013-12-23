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
	public static final String ATT_NB_AGRESSION_FACTOR_BET_RAISE = "nb_hand_af_bet_raise";
	public static final String ATT_NB_AGRESSION_FACTOR_CALL = "nb_hand_af_call";

	public static final String ATT_NB_HANDS = "nb_hands";
	public static final String ATT_NB_HANDS_VPIP = "nb_hands_vpip";
	public static final String ATT_NB_RAISE_PREFLOP = "nb_hands_preflop_raise";

	public static final String ATT_NB_ATS_POSSIBLE = "nb_hands_ats_possible";
	public static final String ATT_NB_ATS = "nb_hands_ats";

	public static final String ATT_NB_FOLD_TO_ATS_SB_POSSIBLE = "nb_hands_fold_to_ats_sb_possible";
	public static final String ATT_NB_FOLD_TO_ATS_BB_POSSIBLE = "nb_hands_fold_to_ats_bb_possible";
	public static final String ATT_NB_FOLD_TO_ATS_SB = "nb_hands_fold_to_ats_sb";
	public static final String ATT_NB_FOLD_TO_ATS_BB = "nb_hands_fold_to_ats_bb";

	public static final String ATT_NB_LIMP = "nb_hands_limp_total";
	public static final String ATT_NB_LIMP_THEN_FOLD = "nb_hands_limp_then_fold";
	public static final String ATT_NB_LIMP_THEN_CALL = "nb_hands_limp_then_call";

	public static final String ATT_NB_3BET_POSSIBLE = "nb_hands_3bet_possible";
	public static final String ATT_NB_3BET = "nb_3bet";

	public static final String ATT_NB_FOLD_TO_3BET_POSSIBLE = "nb_hands_fold_to_3bet_possible";
	public static final String ATT_NB_FOLD_TO_3BET = "nb_hands_fold_to_3bet";

	public static final String ATT_NB_CBET_POSSIBLE = "nb_cbet_possible";
	public static final String ATT_NB_CBET = "nb_cbet";

	public static final String ATT_NB_FOLD_TO_CBET_POSSIBLE = "nb_fold_to_cbet_possible";
	public static final String ATT_NB_FOLD_TO_CBET = "nb_fold_to_cbet";

	public static final String ATT_NB_SECOND_BARREL_POSSIBLE = "nb_second_barrel_possible";
	public static final String ATT_NB_SECOND_BARREL = "nb_second_barrel";

	public static final String ATT_NB_FOLD_TO_SECOND_BARREL_POSSIBLE = "nb_fold_to_second_barrel_possible";
	public static final String ATT_NB_FOLD_TO_SECOND_BARREL = "nb_fold_to_second_barrel";

	public static final String ATT_NB_WENT_TO_SHOWDOWN = "nb_went_to_showdown";
	public static final String ATT_NB_WIN_TO_SHOWDOWN = "nb_win_to_showdown";

	
	public static final String[] INT_ATTRIBUTES = new String[] {
		ATT_NB_AGRESSION_FACTOR_BET_RAISE,
		ATT_NB_AGRESSION_FACTOR_CALL, ATT_NB_HANDS, ATT_NB_HANDS_VPIP,
		ATT_NB_RAISE_PREFLOP, ATT_NB_ATS_POSSIBLE, ATT_NB_ATS,
		ATT_NB_FOLD_TO_ATS_SB_POSSIBLE, ATT_NB_FOLD_TO_ATS_BB_POSSIBLE,
		ATT_NB_FOLD_TO_ATS_SB, ATT_NB_FOLD_TO_ATS_BB, ATT_NB_LIMP,
		ATT_NB_LIMP_THEN_FOLD, ATT_NB_LIMP_THEN_CALL,
		ATT_NB_3BET_POSSIBLE, ATT_NB_3BET,
		ATT_NB_FOLD_TO_3BET_POSSIBLE, ATT_NB_FOLD_TO_3BET,
		ATT_NB_CBET_POSSIBLE, ATT_NB_CBET,
		ATT_NB_FOLD_TO_CBET_POSSIBLE, ATT_NB_FOLD_TO_CBET,
		ATT_NB_SECOND_BARREL_POSSIBLE, ATT_NB_SECOND_BARREL,
		ATT_NB_FOLD_TO_SECOND_BARREL_POSSIBLE,
		ATT_NB_FOLD_TO_SECOND_BARREL,
		ATT_NB_WENT_TO_SHOWDOWN, ATT_NB_WIN_TO_SHOWDOWN };
	
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
