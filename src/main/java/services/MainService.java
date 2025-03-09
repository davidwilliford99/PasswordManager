package services;

import static utils.AppDataDirectory.doesAppDataDirectoryExist;
import static utils.AppDataDirectory.createAppDataDirectory;
import static utils.AppDataDirectory.doesDbFileExist;

/**
 * Handles application setup and initialization tasks.
 */
public class MainService {

	/**
	 * Checks if application's first start
	 * 
	 * @return False if either the app directory or .db file doesn't exist
	 */
	public static boolean isFirstStart() {
		return !doesAppDataDirectoryExist() || !doesDbFileExist();
	}

	/**
	 * Initializes the project by setting up necessary directories and database tables.
	 *
	 * @param userKey The user key used for database encryption or setup.
	 */
	public static void bootstrap(String userKey) {
		createAppDataDirectory();
		DatabaseService.tableSetup(userKey);
	}
}
