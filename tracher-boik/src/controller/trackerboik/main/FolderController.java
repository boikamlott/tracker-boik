package controller.trackerboik.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import com.trackerboik.exception.TBException;
import com.trackerboik.util.AppUtil;

public class FolderController {

	private static final String COMPUTED_FILES_FOLDER_NM = "computed";
	private TrackerBoikController parent;
	
	/**
	 * rootHandFolder: PokerSite hands folder
	 * rootTBFolder: The Tracker Boik root folder
	 */
	private String rootHandFolfer, rootTBFolder;
	
	public FolderController(TrackerBoikController parent, String rootHandFolder, String rootTBFolder) throws TBException {
		this.parent = parent;
		this.rootHandFolfer = rootHandFolder;
		this.rootTBFolder = rootTBFolder;
		checkRootFolder();
	}

	/**
	 * Check the current folder by creating subfolder
	 * @throws TBException 
	 */
	private void checkRootFolder() throws TBException {
		File f = new File(rootTBFolder);
		if(!f.exists()) {
			f.mkdir();
			f = new File(AppUtil.createFilePath(new String[]{f.getAbsolutePath(), COMPUTED_FILES_FOLDER_NM}));
			if(!f.exists()) {f.mkdir();}
		}	
	}
	
	/**
	 * Copy all new file from HandFolder to local
	 */
	public void refreshTBFolder() {
		Files.copy(source, target, options);
	}
	
}
