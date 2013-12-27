package controller.trackerboik.main;

import java.io.File;

public class FolderController {

	private static final String COMPUTED_FILE_FOLDER_NM = "computed";
	private TrackerBoikController parent;
	
	/**
	 * rootHandFolder: PokerSite hands folder
	 * rootTBFolder: The Tracker Boik root folder
	 */
	private String rootHandFolfer, rootTBFolder;
	
	public FolderController(TrackerBoikController parent, String rootHandFolder, String rootTBFolder) {
		this.parent = parent;
		this.rootHandFolfer = rootHandFolder;
		this.rootTBFolder = rootTBFolder;
		checkRootFolder();
	}

	/**
	 * Check the current folder by creating subfolder
	 */
	private void checkRootFolder() {
		File f = new File(rootTBFolder);
		if(!f.exists()) {
			f.createNewFile();
		}
		
	}
	
	
}
