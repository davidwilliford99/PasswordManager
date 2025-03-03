package utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

public class AppDataDirectory {

    public static final String APP_NAME = "PasswordManager";
    public static final String APP_DATA_PATH = getAppDataPath().toString();
    public static final String PROPERTIES_PATH = Paths.get(APP_DATA_PATH,"properties.txt").toString();
    public static final String DB_PATH = Paths.get(APP_DATA_PATH,"database.db").toString();
    public static final String DB_URL = Paths.get(APP_DATA_PATH,"jdbc:sqlite:", DB_PATH).toString();

    /**
     * Returns the platform-specific application data directory path.
     *
     * @return The path to the application data directory as a Path object.
     */
    private static Path getAppDataPath() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            // Windows: %AppData%\Local\<APP_NAME>
            return Paths.get(System.getenv("AppData"), "Local", APP_NAME);
        } else if (os.contains("mac")) {
            // macOS: ~/Library/Application Support/<APP_NAME>
            return Paths.get(System.getProperty("user.home"), "Library", "Application Support", APP_NAME);
        } else {
            // Linux/Unix: ~/.local/share/<APP_NAME>
            return Paths.get(System.getProperty("user.home"), ".local", "share", APP_NAME);
        }
    }

    /**
     * Creates the application data directory if it doesn't exist.
     *
     * @return The path to the application data directory as a String.
     */
    public static String createAppDataDirectory() {
        Path path = getAppDataPath();
        path.toFile().mkdirs();
        return path.toString();
    }

    /**
     * Checks if the application data directory exists.
     *
     * @return true if the directory exists, false otherwise.
     */
    public static boolean doesAppDataDirectoryExist() {
        Path path = getAppDataPath();
        File dir = path.toFile();
        return dir.exists() && dir.isDirectory();
    }
}