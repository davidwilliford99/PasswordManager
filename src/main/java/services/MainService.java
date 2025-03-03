package services;

import static utils.AppDataDirectory.doesAppDataDirectoryExist;
import static utils.AppDataDirectory.createAppDataDirectory;;


public class MainService {

	/**
	 * @description Checks if application's first start
	 * 
	 * @return
	 */
	public static boolean isFirstStart() {
		return !doesAppDataDirectoryExist();
	}

	/**
	 * @title Bootstrap
	 * 
	 * @description Boot straps the project.
	 * @description Create SQLite tables
	 */
	public static void bootstrap(String userKey) {
		createAppDataDirectory();
		DatabaseService.tableSetup(userKey);
	}
}
