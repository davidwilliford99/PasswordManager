package dal;

import models.PasswordRecord;

import java.util.List;

/**
 * Defines data access operations for password records.
 */
public interface IPasswordRecordDao {

  /**
   * Adds a new password record for a user.
   *
   * @param userId   The ID of the user.
   * @param resource The resource (e.g., website or service).
   * @param hashedPassword The password for the resource.
   * @param userKey  The encryption key.
   * @return The created PasswordRecord.
   */
  PasswordRecord addNewPasswordRecord(int userId, String resource, String hashedPassword, String userKey);

  /**
   * Retrieves a password record by its resource.
   *
   * @param resource The resource (e.g., website or service).
   * @param userKey  The encryption key.
   * @return The PasswordRecord for the resource, or null if not found.
   */
  PasswordRecord getPasswordRecord(String resource, String userKey);

  /**
   * Retrieves all password records for a user.
   *
   * @param userId  The ID of the user.
   * @param userKey The encryption key.
   * @return A list of the user's PasswordRecords.
   */
  List<PasswordRecord> getAllPasswordRecords(int userId, String userKey);

  /**
   * Deletes a password record by its resource.
   *
   * @param resource The resource (e.g., website or service).
   * @param userKey  The encryption key.
   * @return True if the record was deleted, false otherwise.
   */
  boolean deletePasswordRecord(String resource, String userKey);
}