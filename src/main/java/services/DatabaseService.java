package services;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.PasswordRecord;
import models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.SqlQueries;

import static utils.AppDataDirectory.DB_PATH;
import static utils.AppDataDirectory.DB_URL;

/**
 * Static methods for database queries
 */
public class DatabaseService {
  private static final Logger logger = LogManager.getLogger(DatabaseService.class);

  /**
   * Connects to the database and applies the encryption key.
   *
   * @param userKey The encryption key for the database.
   * @return A Connection object to the database.
   * @throws SQLException If a database access error occurs.
   */
  private static Connection connect(String userKey) throws SQLException {
    Connection conn = DriverManager.getConnection(DB_URL);
    try (Statement stmt = conn.createStatement()) {
      // PRAGMA statements must be executed as raw SQL, not prepared statements
      stmt.execute("PRAGMA key = '" + userKey + "';");
    } catch (SQLException e) {
      logger.error("An SQL error occurred while fetching data", e);
    }
    return conn;
  }

  /**
   * Creates and sets up the database tables needed for the application. Also sets up the encryption
   * for the database.
   *
   * @param userKey The encryption key for the database.
   */
  public static void tableSetup(String userKey) {
    try {
      File dbFile = new File(DB_PATH);
      boolean isNewDatabase = !dbFile.exists();

      if (isNewDatabase) {
        dbFile.createNewFile();
      }

      try (Connection conn = connect(userKey);
          Statement stmt = conn.createStatement()) {

        stmt.execute("PRAGMA key = '" + CryptoService.hash(userKey) + "';");

        if (!isNewDatabase) {
          stmt.execute(SqlQueries.PRAGMA_CIPHER_MIGRATE);
        }

        stmt.execute(SqlQueries.CREATE_USERS_TABLE);
        stmt.execute(SqlQueries.CREATE_PASSWORD_RECORDS_TABLE);
      }

      logger.info("Database setup complete and encrypted.");

    } catch (Exception e) {
      logger.error("An error occurred while creating the table", e);
    }
  }

  /**
   * Adds a new password record to the database.
   *
   * @param userId   The ID of the user associated with the password record.
   * @param resource The resource (e.g., website or app) for the password.
   * @param password The password to store.
   * @param userKey  The encryption key for the database.
   * @return The newly created PasswordRecord.
   * @throws Exception If an error occurs during the operation.
   */
  public static PasswordRecord addNewPasswordRecord(int userId, String resource, String password,
      String userKey)
      throws Exception {

    try (Connection conn = connect(userKey);
        PreparedStatement pstmt = conn.prepareStatement(SqlQueries.INSERT_PASSWORD_RECORD)) {

      pstmt.setInt(1, userId);
      pstmt.setString(2, resource);
      pstmt.setString(3, CryptoService.encrypt(password, userKey));

      pstmt.executeUpdate();
    } catch (SQLException e) {
      logger.error("An error occurred while adding a new record", e);
    }
    return getPasswordRecord(resource, userKey);
  }

  /**
   * Retrieves a password record by its resource string.
   *
   * @param resource The resource (e.g., website or app) for the password.
   * @param userKey  The encryption key for the database.
   * @return The PasswordRecord associated with the resource.
   * @throws Exception If an error occurs during the operation.
   */
  public static PasswordRecord getPasswordRecord(String resource, String userKey)
      throws Exception {

    PasswordRecord record = null;

    try (Connection conn = connect(userKey);
        PreparedStatement pstmt = conn.prepareStatement(SqlQueries.SELECT_PASSWORD_RECORD)) {

      pstmt.setString(1, resource);

      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          record = new PasswordRecord(
              rs.getInt("id"),
              rs.getInt("user_id"),
              rs.getString("resource"),
              rs.getString("password"));
        }
      }
    } catch (SQLException e) {
      logger.error("An error occurred while getting record {}", record, e);
    }
    return record;
  }

  /**
   * Retrieves all password records for a user.
   *
   * @param userId  The ID of the user.
   * @param userKey The encryption key for the database.
   * @return A list of PasswordRecords for the user.
   */
  public static List<PasswordRecord> getAllPasswordRecords(int userId, String userKey) {

    List<PasswordRecord> passwordRecords = new ArrayList<>();

    try (Connection conn = connect(userKey);
        PreparedStatement pstmt = conn.prepareStatement(SqlQueries.SELECT_ALL_PASSWORD_RECORDS)) {

      pstmt.setInt(1, userId);

      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          PasswordRecord record = new PasswordRecord(
              rs.getInt("id"),
              rs.getInt("user_id"),
              rs.getString("resource"),
              rs.getString("password"));
          passwordRecords.add(record);
        }
      }
    } catch (SQLException e) {
      logger.error("An error occurred getting all password records", e);
    }
    return passwordRecords;
  }

  /**
   * Deletes a password record by its resource string.
   *
   * @param resource The resource (e.g., website or app) for the password.
   * @param userKey  The encryption key for the database.
   * @return True if the deletion was successful, false otherwise.
   */
  public static boolean deletePasswordRecord(String resource, String userKey) {

    try (Connection conn = connect(userKey);
        PreparedStatement pstmt = conn.prepareStatement(SqlQueries.DELETE_PASSWORD_RECORD)) {

      pstmt.setString(1, resource);

      pstmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      logger.error("An error occurred while deleting record {}", resource, e);
      return false;
    }
  }

  /**
   * Adds a new user to the database.
   *
   * @param password The user's password.
   * @param userKey  The encryption key for the database.
   * @return The newly created User.
   */
  public static User addNewUser(String password, String userKey) {

    String hashedPassword = "";
    try {
      hashedPassword = CryptoService.hash(password);
    } catch (UnsupportedEncodingException e) {
      logger.error("An error occurred while hashing password for new user", e);
    }

    try (Connection conn = connect(userKey);
        PreparedStatement pstmt = conn.prepareStatement(SqlQueries.INSERT_USER)) {

      pstmt.setString(1, hashedPassword);

      pstmt.executeUpdate();
    } catch (SQLException e) {
      logger.error("An error occurred while adding new user", e);
    }
    return getUser(password, userKey);
  }

  /**
   * Retrieves a user by their password.
   *
   * @param password The user's password.
   * @param userKey  The encryption key for the database.
   * @return The User associated with the password.
   */
  public static User getUser(String password, String userKey) {

    String hashedPassword = "";
    try {
      hashedPassword = CryptoService.hash(password);
    } catch (UnsupportedEncodingException e) {
      logger.error("An error occurred while hashing user's password", e);
    }

    User user = new User("password");

    try (Connection conn = connect(userKey);
        PreparedStatement pstmt = conn.prepareStatement(SqlQueries.SELECT_USER)) {

      pstmt.setString(1, hashedPassword);

      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          user.setId(rs.getInt("id"));
          user.setPassword(rs.getString("password"));
        }
      }
    } catch (SQLException e) {
      logger.error("An error occurred while getting user", e);
    }
    return user;
  }
}