package controller.trackerboik.main;

import view.trackerboik.main.TrackerBoikApplicationWindows;

public class TrackerBoikController {

	private AtomicDataController atomicDataController;
	private static TrackerBoikController instance;
	
	private TrackerBoikController() {
		atomicDataController = new AtomicDataController(this);
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
	}

	public void exitApplicaiton() {
		//TODO
	}
	
	public AtomicDataController getAtomicDataController() {
		return atomicDataController;
	}
}
