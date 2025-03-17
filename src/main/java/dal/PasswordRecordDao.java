package dal;

import dependencies.sql.ConnectionManager;
import models.PasswordRecord;
import services.ICryptoService;

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

  private final ConnectionManager connection;
  private final ICryptoService cryptoService;

  /**
   * Constructs a PasswordRecordDao instance with the provided crypto service.
   *
   * @param cryptoService The service for cryptographic operations.
   */
  public PasswordRecordDao(ICryptoService cryptoService) {
    this.connection = ConnectionManager.getInstance();
    this.cryptoService = cryptoService;
  }

  @Override
  public PasswordRecord addNewPasswordRecord(int userId, String resource, String password,
      String userKey) {
    try (Connection conn = connection.connect(userKey);
        PreparedStatement pstmt = conn.prepareStatement(INSERT_PASSWORD_RECORD)) {

      pstmt.setInt(1, userId);
      pstmt.setString(2, resource);
      pstmt.setString(3, cryptoService.encrypt(password, userKey));

      pstmt.executeUpdate();
    } catch (Exception e) {
      logger.error("An error occurred while adding a new record", e);
    }
    return getPasswordRecord(resource, userKey);
  }

  @Override
  public PasswordRecord getPasswordRecord(String resource, String userKey) {
    PasswordRecord record = null;

    try (Connection conn = connection.connect(userKey);
        PreparedStatement pstmt = conn.prepareStatement(SELECT_PASSWORD_RECORD)) {

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
    } catch (Exception e) {
      logger.error("An error occurred while getting record", e);
    }
    return record;
  }

  @Override
  public List<PasswordRecord> getAllPasswordRecords(int userId, String userKey) {
    List<PasswordRecord> passwordRecords = new ArrayList<>();

    try (Connection conn = connection.connect(userKey);
        PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_PASSWORD_RECORDS)) {

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
    } catch (Exception e) {
      logger.error("An error occurred getting all password records", e);
    }
    return passwordRecords;
  }

  @Override
  public boolean deletePasswordRecord(String resource, String userKey) {
    try (Connection conn = connection.connect(userKey);
        PreparedStatement pstmt = conn.prepareStatement(DELETE_PASSWORD_RECORD)) {

      pstmt.setString(1, resource);
      pstmt.executeUpdate();
      return true;
    } catch (Exception e) {
      logger.error("An error occurred while deleting record", e);
      return false;
    }
  }
}