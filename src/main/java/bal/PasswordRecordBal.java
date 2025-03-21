package bal;

import models.PasswordRecord;
import dal.IPasswordRecordDao;
import services.ICryptoService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Manages business logic for password record operations.
 */
public class PasswordRecordBal {
  private static final Logger logger = LogManager.getLogger(PasswordRecordBal.class);


  private final IPasswordRecordDao passwordRecordDao;
  private final ICryptoService cryptoService;

  /**
   * Constructs a PasswordRecordBal instance with the provided DAO and crypto service.
   *
   * @param passwordRecordDao The DAO for password record operations.
   * @param cryptoService     The service for cryptographic operations.
   */
  public PasswordRecordBal(IPasswordRecordDao passwordRecordDao, ICryptoService cryptoService) {
    this.passwordRecordDao = passwordRecordDao;
    this.cryptoService = cryptoService;
  }

  /**
   * Adds a new password record for a user.
   *
   * @param userId   The ID of the user.
   * @param resource The resource (e.g., website or service).
   * @param password The plaintext password for the resource.
   * @param userKey  The encryption key for the database.
   * @return The created PasswordRecord, or null if an error occurs.
   */
  public PasswordRecord addPasswordRecord(int userId, String resource, String password, String userKey) {
    try {
      String hashedPassword = cryptoService.encrypt(password, userKey);
      return passwordRecordDao.addNewPasswordRecord(userId, resource, hashedPassword, userKey);
    } catch (Exception e) {
      logger.error("Error hashing password for resource: {}", resource, e);
      return null;
    }
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