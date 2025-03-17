package bal;

import models.User;
import dal.IUserDao;
import services.ICryptoService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;

/**
 * Manages business logic for user-related operations.
 */
public class UserBal {

  private static final Logger logger = LogManager.getLogger(UserBal.class);

  private final IUserDao userDao;
  private final ICryptoService cryptoService;

  /**
   * Constructs a UserBal instance with the provided DAO and crypto service.
   *
   * @param userDao       The DAO for user operations.
   * @param cryptoService The service for cryptographic operations.
   */
  public UserBal(IUserDao userDao, ICryptoService cryptoService) {
    this.userDao = userDao;
    this.cryptoService = cryptoService;
  }

  /**
   * Retrieves a user by their password and encryption key.
   *
   * @param password The user's password.
   * @param userKey  The encryption key.
   * @return The User object, or null if not found.
   */
  public User getUser(String password, String userKey) {
    return userDao.getUser(password, userKey);
  }

  /**
   * Creates a new user with the given password.
   *
   * @param password The user's password.
   * @return The newly created User, or null if an error occurs.
   */
  public User createUser(String password) {
    try {
      String encryptionKey = cryptoService.hash(password);
      User user = new User(password);
      user.setEncryptionKey(encryptionKey);
      return userDao.addNewUser(password, encryptionKey);
    } catch (UnsupportedEncodingException e) {
      logger.error("Error hashing password", e);
      return null;
    }
  }

  /**
   * Authenticates a user by checking if the provided password matches a user in the database.
   *
   * @param password The password to authenticate.
   * @return True if authentication succeeds, false otherwise.
   */
  public boolean authenticateUser(String password) {
    try {
      String encryptionKey = cryptoService.hash(password);
      User user = userDao.getUser(password, encryptionKey);
      return user != null
          && user.getId() != 50000; // 50000 is the default ID for non-database users
    } catch (UnsupportedEncodingException e) {
      logger.error("Error hashing the user's password", e);
      return false;
    }
  }
}