package model.trackerboik.businessobject;

import com.trackerboik.exception.TBException;

/**
 * Class which validate a hand object
 * @author Gaetan
 *
 */
public class HandValidator {

	private Hand h;
	
	public HandValidator(Hand h) {
		this.h = h;
	}
	
	/**
	 * Validate the hand given in parameter
	 * @throws TBException
	 */
	public void validate() throws TBException {
		String error = "";
		if(h == null) {
			error = "Hand is null";
		} else if(h.getId() == null || h.getId().isEmpty()) {
			error = "Hand ID is null or empty";
		} else {
			String errorWithID = "Hand(" + h.getId() + "): ";
			if(h.getAssociatedSession() == null || h.getDateTime() == null || h.getLimitBB() == null ||
					h.getPlayers() == null || h.getPlayers().isEmpty() || h.getPot() == null || h.getSiteRake() == null ||
					h.getTableName() == null) {
				error = errorWithID + " some mandatories attributes are missing";
			} else {
				try {
					validateHandBusinessLogic();
				} catch (TBException e) {
					error = errorWithID + e.getMessage();
				}
			}
		}
		
		if(!error.isEmpty()) {
			throw new TBException(error);
		}
	}

	/**
	 * Validate Hand in a logical point of view
	 * ex: no raises before bet on the Flop
	 * PRE: All basic hand attributes are not null
	 */
	private void validateHandBusinessLogic() throws TBException {
		// TODO Perfectionner le tool et éviter d'importer des erreurs ! Peut être un peu fastisieux
	}
}
