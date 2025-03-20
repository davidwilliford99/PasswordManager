package dal;

import dependencies.sql.ConnectionManager;
import services.ICryptoService;
import models.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.UnsupportedEncodingException;

/**
 * Provides database operations for users.
 */
public class UserDao implements IUserDao {

  private static final Logger logger = LogManager.getLogger(UserDao.class);

  private static final String INSERT_USER = "INSERT INTO users(password, encryption_key) VALUES(?, ?);";
  private static final String SELECT_USER = "SELECT * FROM users WHERE password=?;";

  private final ConnectionManager connection;
  private final ICryptoService cryptoService;

  /**
   * Constructs a UserDao instance with the provided crypto service.
   *
   * @param cryptoService The service for cryptographic operations.
   */
  public UserDao(ICryptoService cryptoService) {
    this.connection = ConnectionManager.getInstance();
    this.cryptoService = cryptoService;
  }

  @Override
  public User addNewUser(String password, String userKey) {
    String hashedPassword = "";
    try {
      hashedPassword = cryptoService.hash(password);
    } catch (UnsupportedEncodingException e) {
      logger.error("An error occurred while hashing password for new user", e);
      return null;
    }

    try (Connection conn = connection.connect(userKey);
        PreparedStatement pstmt = conn.prepareStatement(INSERT_USER)) {

      pstmt.setString(1, hashedPassword);
      pstmt.setString(2, userKey);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      logger.error("An error occurred while adding new user", e);
      return null;
    }
    return getUser(password, userKey);
  }

  @Override
  public User getUser(String password, String userKey) {
    String hashedPassword = "";
    try {
      hashedPassword = cryptoService.hash(password);
    } catch (UnsupportedEncodingException e) {
      logger.error("An error occurred while hashing user's password", e);
      return null;
    }

    User user = new User("password");

    try (Connection conn = connection.connect(userKey);
        PreparedStatement pstmt = conn.prepareStatement(SELECT_USER)) {

      pstmt.setString(1, hashedPassword);

      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          user.setId(rs.getInt("id"));
          user.setPassword(rs.getString("password"));
          user.setEncryptionKey(rs.getString("encryption_key"));
        }
      }
    } catch (SQLException e) {
      logger.error("An error occurred while getting user", e);
      return null;
    }
    return user;
  }
}