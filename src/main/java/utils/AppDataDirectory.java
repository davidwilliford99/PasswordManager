package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppDataDirectory {
    private static final Logger logger = LogManager.getLogger(AppDataDirectory.class);

    public static final String APP_NAME = "PasswordManager";
    public static final String APP_DATA_PATH = getAppDataPath().toString();
    public static final String DB_PATH = Paths.get(APP_DATA_PATH,"database.db").toString();
    public static final String DB_URL = "jdbc:sqlite:" + DB_PATH;


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

    /**
     * Checks if there is an existing .db file in app data directory
     *
     * @return true if a file with the .db extension exists, false otherwise
     */
    public static boolean doesDbFileExist() {
        if (!doesAppDataDirectoryExist()) {
            return false;
        }

        Path path = getAppDataPath();

        try (Stream<Path> stream = Files.list(path)) {
            return stream
                .filter(Files::isRegularFile)
                .anyMatch(p -> p.toString().endsWith(".db"));
        } catch (IOException e) {
            logger.error("An error occurred while looking for existing database", e);
            return false;
        }

    }

}