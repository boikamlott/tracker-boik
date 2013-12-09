package controller.trackerboik.main;

import java.util.ArrayList;
import java.util.List;

import model.trackerboik.businessobject.PokerSession;

import com.trackerboik.exception.TBException;

import view.trackerboik.main.TBExceptionFrame;
import view.trackerboik.main.TrackerBoikApplicationWindows;

public class TrackerBoikController {

	private AtomicDataController atomicDataController;
	private ConfigurationController configurationController;
	private List<PokerSession> sessionsInMemory;
	private static TrackerBoikController instance;
	
	private TrackerBoikController() {}
	
	private void initialize() {
		try {
			configurationController = new ConfigurationController();
			atomicDataController = new AtomicDataController(this);
			sessionsInMemory = new ArrayList<>();
		} catch (TBException e) {
			printException("Erreur au démarrage: '" + e.getMessage() + "' Vérifier votre environnement et consultez les logs pour plus de détails");
		} catch (Exception e) {
			printException("Erreur inconnue. Redemarrez l'application.");
		}
	}
	
	/**
	 * Return all sessions load in memory
	 * @return
	 */
	public List<PokerSession> getSessions() {
		return sessionsInMemory;
	}
	
	private void printException(String errorMsg) {
		TBExceptionFrame errorView = new TBExceptionFrame();
		errorView.setErrorText(errorMsg);
		errorView.setVisible(true);
	}

	public static TrackerBoikController getInstance() {
		if(instance == null) {
			instance = new TrackerBoikController();
		}
		
		return instance;
	}
	
	public static void main(String[] args) {
		getInstance().startApplication();
	}
	
	public void startApplication() {
		TrackerBoikApplicationWindows.main(new String[]{});
		initialize();
	}

	public void exitApplicaiton() {
		//TODO
	}
	
	public AtomicDataController getAtomicDataController() {
		return atomicDataController;
	}

	public ConfigurationController getConfigurationController() {
		return configurationController;
	}

	public void addSession(PokerSession s) throws TBException {
		if(sessionsInMemory.contains(new PokerSession(s.getId()))) {
			throw new TBException("Session with ID: " + s.getId() + " already load in memeory !");
		}
		sessionsInMemory.add(s);
	}
}
