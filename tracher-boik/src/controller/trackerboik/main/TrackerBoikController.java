package controller.trackerboik.main;

import java.util.ArrayList;
import java.util.List;

import model.trackerboik.businessobject.PlayerSessionStats;
import model.trackerboik.businessobject.PokerPlayer;
import model.trackerboik.businessobject.PokerSession;

import com.trackerboik.exception.TBException;

import view.trackerboik.main.TBExceptionFrame;
import view.trackerboik.main.TrackerBoikApplicationWindows;

public class TrackerBoikController {

	private AtomicDataController atomicDataController;
	private AggregateDataController aggregateDataController;
	private ConfigurationController configurationController;
	private List<PokerSession> sessionsInMemory;
	private List<PokerPlayer> playersInMemory;
	private List<PlayerSessionStats> playersSessionStats;
	private static TrackerBoikController instance;
	
	private TrackerBoikController() {}
	
	private void initialize() {
		try {
			configurationController = new ConfigurationController();
			atomicDataController = new AtomicDataController(this);
			aggregateDataController = new AggregateDataController(this);
			sessionsInMemory = new ArrayList<>();
			playersInMemory = new ArrayList<PokerPlayer>();
			playersSessionStats = new ArrayList<PlayerSessionStats>();
		} catch (TBException e) {
			printException("Erreur au démarrage: '" + e.getMessage() + "' Vérifier votre environnement et consultez les logs pour plus de détails");
		} catch (Exception e) {
			printException("Erreur inconnue. Redemarrez l'application.");
		}
	}
	
	public void refreshAggregatedData() {
		try {
			//Refresh Hand Data
			atomicDataController.refreshCurrentFolder();
			atomicDataController.loadNewAtomicData();
			aggregateDataController.refreshIndicatorsData();
		} catch (TBException e) {
			//TODO
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * Return all sessions load in memory
	 * @return
	 */
	public List<PokerSession> getSessions() {
		return sessionsInMemory;
	}
	
	/**
	 * Return all players in memory
	 * @return
	 */
	public List<PokerPlayer> getPlayers() {
		return playersInMemory;
	}
	
	/**
	 * Return the player session stats
	 * @return
	 */
	public List<PlayerSessionStats> getPlayerSessionsStats() {
		return playersSessionStats;
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

	/**
	 * If player is registred in the list return it else create it and
	 * add it in the central list
	 * @param string
	 * @return
	 */
	public PokerPlayer getPlayerOrCreateIt(String id) {
		PokerPlayer pp = new PokerPlayer(id);
		if(!playersInMemory.contains(pp)) {
			playersInMemory.add(pp);
		}
		
		return pp;
	}
}
