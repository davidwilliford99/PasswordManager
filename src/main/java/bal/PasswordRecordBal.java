package bal;

import models.PasswordRecord;
import dal.IPasswordRecordDao;

import java.util.List;

/**
 * Manages business logic for password record operations.
 */
public class PasswordRecordBal {

  private final IPasswordRecordDao passwordRecordDao;

  /**
   * Constructs a PasswordRecordBal instance with the provided DAO.
   *
   * @param passwordRecordDao The DAO for password record operations.
   */
  public PasswordRecordBal(IPasswordRecordDao passwordRecordDao) {
    this.passwordRecordDao = passwordRecordDao;
  }

  /**
   * Adds a new password record for a user.
   *
   * @param userId   The ID of the user.
   * @param resource The resource (e.g., website or service).
   * @param password The password for the resource.
   * @param userKey  The encryption key for the database.
   * @return The created PasswordRecord.
   */
  public PasswordRecord addPasswordRecord(int userId, String resource, String password,
      String userKey) {
    return passwordRecordDao.addNewPasswordRecord(userId, resource, password, userKey);
  }

  /**
   * Retrieves a password record by its resource.
   *
   * @param resource The resource (e.g., website or service).
   * @param userKey  The encryption key for the database.
   * @return The PasswordRecord for the resource.
   */
  public PasswordRecord getPasswordRecord(String resource, String userKey) {
    return passwordRecordDao.getPasswordRecord(resource, userKey);
  }

  /**
   * Retrieves all password records for a user.
   *
   * @param userId  The ID of the user.
   * @param userKey The encryption key for the database.
   * @return A list of the user's PasswordRecords.
   */
  public List<PasswordRecord> getAllPasswordRecords(int userId, String userKey) {
    return passwordRecordDao.getAllPasswordRecords(userId, userKey);
  }

  /**
   * Deletes a password record by its resource.
   *
   * @param resource The resource (e.g., website or service).
   * @param userKey  The encryption key for the database.
   * @return True if deleted, false otherwise.
   */
  public boolean deletePasswordRecord(String resource, String userKey) {
    return passwordRecordDao.deletePasswordRecord(resource, userKey);
  }
}