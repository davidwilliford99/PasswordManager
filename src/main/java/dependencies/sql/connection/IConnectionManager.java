package dependencies.sql.connection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages database connections, including setting up encryption and connection pooling.
 */
public interface IConnectionManager {

  /**
   * Connects to the database and applies the encryption key.
   *
   * @param userKey The encryption key for the database.
   * @return A Connection object to the database.
   * @throws SQLException If a database access error occurs.
   */
  Connection connect(String userKey) throws SQLException;

  /**
   * Closes the connection pool and releases all resources.
   */
  void closePool();
}