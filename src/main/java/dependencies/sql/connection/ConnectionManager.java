package dependencies.sql.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static utils.AppDataDirectory.DB_URL;

/**
 * Manages a connection pool for the database using HikariCP.
 */
public class ConnectionManager implements IConnectionManager {

  private static final Logger logger = LogManager.getLogger(ConnectionManager.class);

  private HikariDataSource dataSource;
  private final Object lock = new Object();

  /**
   * Initializes the connection pool if it hasn't been initialized yet.
   */
  private void initializePool() {
    synchronized (lock) {
      if (dataSource != null) {
        return;
      }

      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(DB_URL);
      config.setMaximumPoolSize(5);
      config.setMinimumIdle(1);
      config.setIdleTimeout(30000);
      config.setMaxLifetime(1800000);
      config.setConnectionTimeout(10000);
      config.setLeakDetectionThreshold(5000);

      dataSource = new HikariDataSource(config);
      logger.info("Connection pool initialized successfully.");
    }
  }

  @Override
  public Connection connect(String userKey) throws SQLException {
    if (dataSource == null) {
      initializePool();
    }

    Connection conn = dataSource.getConnection();
    try (Statement stmt = conn.createStatement()) {
      stmt.execute("PRAGMA key = '" + userKey + "';");
    } catch (SQLException e) {
      logger.error("SQL error while applying encryption key", e);
      throw e;
    }
    return conn;
  }

  @Override
  public void closePool() {
    synchronized (lock) {
      if (dataSource != null) {
        dataSource.close();
        dataSource = null;
        logger.info("Connection pool closed successfully.");
      }
    }
  }
}