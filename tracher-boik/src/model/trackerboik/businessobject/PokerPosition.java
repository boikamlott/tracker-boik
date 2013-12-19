package model.trackerboik.businessobject;

import com.trackerboik.util.AppUtil;

public enum PokerPosition {
	UTG("UTG"),
	MP("MP"),
	MP_1("MP_1"),
	MP_2("MP_2"),
	MP_3("MP_3"),
	CO("Cutoff"),
	BU("Button"),
	SB("Small blind"),
	BB("Big Blind");
	
	private String label;
	
	private PokerPosition(String txtLbl) {
		this.label = txtLbl;
	}
	
	public String toString() {
		return this.label;
	}

	public static PokerPosition getPositionOfPlayer(Integer playerPosition,
			int buttonPosition, int nbPlayers) {
		PokerPosition res = null;
		if (playerPosition == buttonPosition) {
			res = BU;
		} else if (playerPosition == (buttonPosition + 1) % nbPlayers) {
			res = SB;
		} else if(playerPosition == (buttonPosition + 2) % nbPlayers) {
			res = BB;
		} else if(playerPosition == (buttonPosition + 3) % nbPlayers) {
			res = UTG;
		} else if(playerPosition == (buttonPosition + 4) % nbPlayers) {
			res = MP_1;
		} else if((nbPlayers == AppUtil.NB_PLAYER_6_MAX && playerPosition == (buttonPosition + 5) % nbPlayers) || 
					(nbPlayers == AppUtil.NB_PLAYER_FULL_RING && playerPosition == (buttonPosition + 8) % nbPlayers)) {
			res = CO;
		} else if(nbPlayers == AppUtil.NB_PLAYER_FULL_RING && playerPosition == (buttonPosition + 6) % nbPlayers) {
			res = MP_2;
		} else if(nbPlayers == AppUtil.NB_PLAYER_FULL_RING && playerPosition == (buttonPosition + 7) % nbPlayers) {
			res = MP_3;
		}
		
		return res;
	}
}
