package controller.trackerboik.main;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.trackerboik.exception.TBException;
import com.trackerboik.util.AppUtil;

public class FolderController {

	private static final String COMPUTED_FILES_FOLDER_NM = "computedTrackerBoikFiles";
	private TrackerBoikController parent;
	
	/**
	 * rootHandFolder: PokerSite hands folder
	 * rootTBFolder: The Tracker Boik root folder
	 */
	private String rootHandFolder, rootTBFolder;
	
	public FolderController(TrackerBoikController parent, String rootHandFolder, String rootTBFolder) throws TBException {
		this.parent = parent;
		this.rootHandFolder = rootHandFolder;
		this.rootTBFolder = rootTBFolder;
		checkRootFolder();
		refreshTBFolder();
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
			f.mkdir();
		}	
	}
	
	/**
	 * Copy all new file from HandFolder to local
	 */
	public void refreshTBFolder() throws TBException {
		refreshAllElemsFromHandFolder(new File(rootHandFolder).getAbsolutePath(), new File(rootTBFolder).getAbsolutePath());
	}

	/**
	 * Recursive routine to synchronize app folder
	 * with Hand data folder
	 * @param rootHandFolder2
	 * @throws TBException 
	 */
	private void refreshAllElemsFromHandFolder(String rootFolderRead, String rootFolderWrite) throws TBException {
		File f = new File(rootFolderRead);
		if(!f.exists() || !f.isDirectory()) {
			throw new TBException("Impossible to read Hand Folder from directory '" + rootHandFolder + "' !");
		}
		
		String[] list = f.list();
		if(list != null && list.length != 0) {
			for(String path : list) {
				String completePathRead = AppUtil.createFilePath(new String[]{rootFolderRead, path});
				String completePathWrite = AppUtil.createFilePath(new String[]{rootFolderWrite, path});
				File f2 = new File(completePathRead);
				
				if(f2.isDirectory()) {
					f2 = new File(completePathWrite);
					if(!f2.exists()) { f2.mkdir();}
					refreshAllElemsFromHandFolder(completePathRead, completePathWrite);
				} else if(f2.isFile()){
					f2 = new File(completePathWrite);
					if(!f2.exists()) {
						try {
							Files.copy(Paths.get(completePathRead), Paths.get(completePathWrite), REPLACE_EXISTING);
						} catch (IOException e) {
							throw new TBException("Impossible to copy file '" + completePathRead + "' in '" + 
						completePathWrite + "' because: " + e.getMessage());
						}
					}	
				}
			}
		}
		
	}
	
	/**
	 * Return a list of all absolute path of files that are not store in DB
	 * @return
	 * @throws TBException
	 */
	public List<String> getAllNotComputedFilesPath() throws TBException {
		return getNotComputedFilesPath(new File(rootTBFolder).getAbsolutePath());
		
	}
	
	/**
	 * Recursive routine to collect all Files from root given in parameter
	 * except all files related to computed folder
	 * @param absolutePath
	 * @return
	 */
	private List<String> getNotComputedFilesPath(String absolutePath) {
		List<String> res = new ArrayList<String>();
		File f = new File(absolutePath);
		String[] list = f.list();
		
		for(String s : list) {
			String elemPath = AppUtil.createFilePath(new String[]{absolutePath, s});
			if(!s.contains(COMPUTED_FILES_FOLDER_NM)) {
				File f2 = new File(elemPath);
				if(f2.isDirectory()) {
					res.addAll(getNotComputedFilesPath(elemPath));
				} else if(f2.isFile()) {
					res.add(elemPath);
				}
			}
		}
		
		return res;
	}

	/**
	 * Move file in the Computed directory
	 * @param path
	 * @throws TBException
	 */
	public void markFileAsComputed(String path) throws TBException {
		File f = new File(path);
		File folderRootTB = new File(rootTBFolder);
		try {
			if(f.exists() && !f.getAbsolutePath().contains(COMPUTED_FILES_FOLDER_NM) && 
							  f.getAbsolutePath().contains(folderRootTB.getAbsolutePath())) {
				String newPath = AppUtil.createFilePath(new String[]{folderRootTB.getAbsolutePath(), 
										COMPUTED_FILES_FOLDER_NM, 
										f.getAbsolutePath().split(folderRootTB.getAbsolutePath())[1]});
				File newFile = new File(newPath);
				newFile.createNewFile();
				Files.copy(Paths.get(f.getAbsolutePath()), Paths.get(newFile.getAbsolutePath()), REPLACE_EXISTING);
				f.delete();
			}
		} catch (IOException e) {
			throw new TBException("Impossible to mark file '" + path + "' as read because: " + e.getMessage());
		}
	}
}
