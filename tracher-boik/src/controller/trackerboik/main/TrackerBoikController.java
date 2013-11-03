package controller.trackerboik.main;

import com.trackerboik.exception.TBException;

import view.trackerboik.main.TBExceptionFrame;
import view.trackerboik.main.TrackerBoikApplicationWindows;

public class TrackerBoikController {

	private AtomicDataController atomicDataController;
	private ConfigurationController configurationController;
	private static TrackerBoikController instance;
	
	private TrackerBoikController() {}
	
	private void initialize() {
		try {
			configurationController = new ConfigurationController();
			atomicDataController = new AtomicDataController(this);
		} catch (TBException e) {
			printException("Erreur au d�marrage: '" + e.getMessage() + "' V�rifier votre environnement et consultez les logs pour plus de d�tails");
		} catch (Exception e) {
			printException("Erreur inconnue. Redemarrez l'application.");
		}
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
}
