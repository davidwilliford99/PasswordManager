package dependencies.sql;

import dependencies.sql.connection.IConnectionManager;
import services.ICryptoService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.io.UnsupportedEncodingException;

import static utils.AppDataDirectory.DB_PATH;
import static utils.AppDataDirectory.createAppDataDirectory;

/**
 * Handles database initialization, table creation, and encryption setup.
 */
public class DatabaseInitializer {

  private static final Logger logger = LogManager.getLogger(DatabaseInitializer.class);

  private static final String PRAGMA_CIPHER_MIGRATE = "PRAGMA cipher_migrate;";
  private static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS users ("
      + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
      + "password TEXT NOT NULL, "
      + "encryption_key TEXT NOT NULL"
      + ");";
  private static final String CREATE_PASSWORD_RECORDS_TABLE =
      "CREATE TABLE IF NOT EXISTS password_records ("
          + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
          + "user_id INTEGER NOT NULL, "
          + "resource TEXT NOT NULL UNIQUE, "
          + "password TEXT NOT NULL, "
          + "FOREIGN KEY (user_id) REFERENCES users(id)"
          + ");";

  private final IConnectionManager connectionManager;
  private final ICryptoService cryptoService;

  /**
   * Constructs a DatabaseInitializer instance with the provided crypto service and connection
   * manager.
   *
   * @param cryptoService     The service for cryptographic operations.
   * @param connectionManager The connection manager for database operations.
   */
  public DatabaseInitializer(ICryptoService cryptoService, IConnectionManager connectionManager) {
    this.cryptoService = cryptoService;
    this.connectionManager = connectionManager;
  }

  /**
   * Initializes the project by setting up necessary directories and database tables.
   *
   * @param userKey The encryption key for the database.
   */
  public void bootstrap(String userKey) {
    createAppDataDirectory();
    tableSetup(userKey);
  }

  /**
   * Initializes the database, sets up encryption, and creates required tables.
   *
   * @param userKey The encryption key for the database.
   */
  private void tableSetup(String userKey) {
    try {
      File dbFile = new File(DB_PATH);
      boolean isNewDatabase = !dbFile.exists();

      if (isNewDatabase) {
        dbFile.createNewFile();
      }

      try (Connection conn = connectionManager.connect(userKey);
          Statement stmt = conn.createStatement()) {

        String hashedUserKey = cryptoService.hash(userKey);
        stmt.execute("PRAGMA key = '" + hashedUserKey + "';");

        if (!isNewDatabase) {
          stmt.execute(PRAGMA_CIPHER_MIGRATE);
        }

        stmt.execute(CREATE_USERS_TABLE);
        stmt.execute(CREATE_PASSWORD_RECORDS_TABLE);
      }

      logger.info("Database setup complete and encrypted.");

    } catch (UnsupportedEncodingException e) {
      logger.error("Error hashing the user key for database encryption", e);
    } catch (Exception e) {
      logger.error("Error during database setup", e);
    }
  }
}