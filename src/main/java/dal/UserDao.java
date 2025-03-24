package dal;

import dependencies.sql.connection.IConnectionManager;
import models.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides database operations for users.
 */
public class UserDao implements IUserDao {

  private static final Logger logger = LogManager.getLogger(UserDao.class);

  private static final String INSERT_USER = "INSERT INTO users(password, encryption_key) VALUES(?, ?);";
  private static final String SELECT_USER = "SELECT * FROM users WHERE password=?;";

  private final IConnectionManager connection;

  /**
   * Constructs a UserDao instance
   **/
  public UserDao(IConnectionManager connectionManager) {
    this.connection = connectionManager;
  }

  @Override
  public User addNewUser(String hashedPassword, String userKey) {
    try (Connection conn = connection.connect(userKey);
        PreparedStatement pstmt = conn.prepareStatement(INSERT_USER)) {

      pstmt.setString(1, hashedPassword);
      pstmt.setString(2, userKey);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      logger.error("An error occurred while adding new user", e);
      return null;
    }
    return getUser(hashedPassword, userKey);
  }

  @Override
  public User getUser(String hashedPassword, String userKey) {
    try (Connection conn = connection.connect(userKey);
        PreparedStatement pstmt = conn.prepareStatement(SELECT_USER)) {

      pstmt.setString(1, hashedPassword);

      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToUser(rs);
        }
      }
    } catch (SQLException e) {
      logger.error("An error occurred while getting user", e);
    }
    return null;
  }

  /**
   * Maps a ResultSet row to a User object.
   *
   * @param rs The ResultSet containing user data.
   * @return A User object populated with data from the ResultSet.
   * @throws SQLException If a database access error occurs.
   */
  private User mapResultSetToUser(ResultSet rs) throws SQLException {
    User user = new User(rs.getString("password"));
    user.setId(rs.getInt("id"));
    user.setEncryptionKey(rs.getString("encryption_key"));
    return user;
  }
}