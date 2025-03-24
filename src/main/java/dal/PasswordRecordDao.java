package dal;

import dependencies.sql.connection.IConnectionManager;
import models.PasswordRecord;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides database operations for password records.
 */
public class PasswordRecordDao implements IPasswordRecordDao {

  private static final Logger logger = LogManager.getLogger(PasswordRecordDao.class);

  private static final String INSERT_PASSWORD_RECORD = "INSERT INTO password_records(user_id, resource, password) VALUES(?, ?, ?);";
  private static final String SELECT_PASSWORD_RECORD = "SELECT * FROM password_records WHERE resource=?;";
  private static final String SELECT_ALL_PASSWORD_RECORDS = "SELECT * FROM password_records WHERE user_id=?;";
  private static final String DELETE_PASSWORD_RECORD = "DELETE FROM password_records WHERE resource=?;";

  private final IConnectionManager connectionManager;

  /**
   * Constructs a PasswordRecordDao instance.
   *
   * @param connectionManager The connection manager for database operations.
   */
  public PasswordRecordDao(IConnectionManager connectionManager) {
    this.connectionManager = connectionManager;
  }

  @Override
  public PasswordRecord addNewPasswordRecord(int userId, String resource, String hashedPassword, String userKey) {
    try (Connection conn = connectionManager.connect(userKey);
        PreparedStatement pstmt = conn.prepareStatement(INSERT_PASSWORD_RECORD)) {

      pstmt.setInt(1, userId);
      pstmt.setString(2, resource);
      pstmt.setString(3, hashedPassword);
      pstmt.executeUpdate();

      logger.info("Added new password record for resource: {}", resource);
      return getPasswordRecord(resource, userKey);
    } catch (Exception e) {
      logger.error("An error occurred while adding a new password record for resource: {}", resource, e);
      return null;
    }
  }

  @Override
  public PasswordRecord getPasswordRecord(String resource, String userKey) {
    try (Connection conn = connectionManager.connect(userKey);
        PreparedStatement pstmt = conn.prepareStatement(SELECT_PASSWORD_RECORD)) {

      pstmt.setString(1, resource);

      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return mapResultSetToPasswordRecord(rs);
        }
      }
    } catch (Exception e) {
      logger.error("An error occurred while getting password record for resource: {}", resource, e);
    }
    return null;
  }

  @Override
  public List<PasswordRecord> getAllPasswordRecords(int userId, String userKey) {
    List<PasswordRecord> passwordRecords = new ArrayList<>();

    try (Connection conn = connectionManager.connect(userKey);
        PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_PASSWORD_RECORDS)) {

      pstmt.setInt(1, userId);

      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          passwordRecords.add(mapResultSetToPasswordRecord(rs));
        }
      }
    } catch (Exception e) {
      logger.error("An error occurred while getting all password records for user ID: {}", userId, e);
    }
    return passwordRecords;
  }

  @Override
  public boolean deletePasswordRecord(String resource, String userKey) {
    try (Connection conn = connectionManager.connect(userKey);
        PreparedStatement pstmt = conn.prepareStatement(DELETE_PASSWORD_RECORD)) {

      pstmt.setString(1, resource);
      pstmt.executeUpdate();

      logger.info("Deleted password record for resource: {}", resource);
      return true;
    } catch (Exception e) {
      logger.error("An error occurred while deleting password record for resource: {}", resource, e);
      return false;
    }
  }

  /**
   * Maps a ResultSet row to a PasswordRecord object.
   *
   * @param rs The ResultSet containing password record data.
   * @return A PasswordRecord object populated with data from the ResultSet.
   * @throws Exception If a database access error occurs.
   */
  private PasswordRecord mapResultSetToPasswordRecord(ResultSet rs) throws Exception {
    return new PasswordRecord(
        rs.getInt("id"),
        rs.getInt("user_id"),
        rs.getString("resource"),
        rs.getString("password"));
  }
}