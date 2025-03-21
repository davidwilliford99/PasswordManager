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
   * Retrieves a user by their plaintext password and encryption key.
   * The password is hashed before being passed to the DAL.
   *
   * @param password The user's plaintext password.
   * @param userKey       The encryption key.
   * @return The User object, or null if not found or an error occurs.
   */
  public User getUser(String password, String userKey) {
    try {
      String hashedPassword = cryptoService.hash(password);
      return userDao.getUser(hashedPassword, userKey);
    } catch (UnsupportedEncodingException e) {
      logger.error("Error hashing password", e);
      return null;
    }
  }

  /**
   * Creates a new user with the given plaintext password.
   * The password is hashed before being passed to the DAL.
   *
   * @param password The user's plaintext password.
   * @return The newly created User, or null if an error occurs.
   */
  public User createUser(String password) {
    try {
      String hashedPassword = cryptoService.hash(password);
      return userDao.addNewUser(hashedPassword, hashedPassword);
    } catch (UnsupportedEncodingException e) {
      logger.error("Error hashing password", e);
      return null;
    }
  }

  /**
   * Authenticates a user by checking if the provided plaintext password matches a user in the database.
   * The password is hashed before being passed to the DAL.
   *
   * @param password The plaintext password to authenticate.
   * @return True if authentication succeeds, false otherwise.
   */
  public boolean authenticateUser(String password) {
    try {
      String hashedPassword = cryptoService.hash(password);
      User user = userDao.getUser(hashedPassword, hashedPassword);
      return user != null && user.getId() != 50000; // 50000 is the default ID for non-database users
    } catch (UnsupportedEncodingException e) {
      logger.error("Error hashing the user's password", e);
      return false;
    }
  }
}