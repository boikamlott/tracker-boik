package controller.trackerboik.main;

import java.util.ArrayList;
import java.util.List;

import model.trackerboik.businessobject.Hand;
import model.trackerboik.businessobject.PlayerStats;
import model.trackerboik.businessobject.PokerPlayer;
import view.trackerboik.main.TBExceptionFrame;
import view.trackerboik.main.TrackerBoikApplicationWindows;

import com.trackerboik.exception.TBException;
import com.trackerboik.util.AppUtil;

public class TrackerBoikController {

	private AtomicDataController atomicDataController;
	private AggregateDataController aggregateDataController;
	private ConfigurationController configurationController;
	private FolderController folderController;
	
	private List<Hand> handsInMemory;
	private List<PokerPlayer> playersInMemory;
	private List<PlayerStats> playersSessionStats;
	private static TrackerBoikController instance;
	
	private TrackerBoikController() {}
	
	private void initialize() {
		try {
			configurationController = new ConfigurationController();
			atomicDataController = new AtomicDataController(this);
			aggregateDataController = new AggregateDataController(this);
			folderController = new FolderController(this, configurationController.getProperty(AppUtil.HAND_FOLDER_PROPERTY), 
					configurationController.getProperty(AppUtil.TB_FOLDER_PROPERTY));
			
			handsInMemory = new ArrayList<Hand>();
			playersInMemory = new ArrayList<PokerPlayer>();
			playersSessionStats = new ArrayList<PlayerStats>();
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
	public List<Hand> getHands() {
		return handsInMemory;
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
	public List<PlayerStats> getPlayerSessionsStats() {
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
	
	public FolderController getFolderController() {
		return 
	}
	
	public AtomicDataController getAtomicDataController() {
		return atomicDataController;
	}

	public ConfigurationController getConfigurationController() {
		return configurationController;
	}

	public void addHand(Hand h) throws TBException {
		if(handsInMemory.contains(new Hand(h.getId()))) {
			throw new TBException("Session with ID: " + h.getId() + " already load in memeory !");
		}
		handsInMemory.add(h);
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
