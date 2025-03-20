package dependencies.sql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static utils.AppDataDirectory.DB_URL;

/**
 * Manages database connections and applies encryption keys.
 */
public class ConnectionManager {

  private static final Logger logger = LogManager.getLogger(ConnectionManager.class);

  private static ConnectionManager instance;
  private Connection conn;

  private ConnectionManager() {
  }

  /**
   * Returns the singleton instance of ConnectionManager.
   *
   * @return The ConnectionManager instance.
   */
  public static synchronized ConnectionManager getInstance() {
    if (instance == null) {
      instance = new ConnectionManager();
    }
    return instance;
  }

  /**
   * Connects to the database and applies the encryption key.
   *
   * @param userKey The encryption key for the database.
   * @return A Connection object to the database.
   * @throws SQLException If a database access error occurs.
   */
  public Connection connect(String userKey) throws SQLException {
    if (conn == null || conn.isClosed()) {
      conn = DriverManager.getConnection(DB_URL);
      try (Statement stmt = conn.createStatement()) {
        stmt.execute("PRAGMA key = '" + userKey + "';");
      } catch (SQLException e) {
        logger.error("SQL error while applying encryption key", e);
      }
    }
    return conn;
  }

  /**
   * Closes the database connection.
   */
  public void closeConnection() {
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        logger.error("Error closing database connection", e);
      }
    }
  }
}