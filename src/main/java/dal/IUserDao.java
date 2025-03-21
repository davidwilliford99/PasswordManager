package dal;

import models.User;

/**
 * Defines data access operations for users.
 */
public interface IUserDao {

  /**
   * Adds a new user with the provided hashed password and encryption key.
   *
   * @param hashedPassword The user's hashed password.
   * @param userKey  The encryption key.
   * @return The created User.
   */
  User addNewUser(String hashedPassword, String userKey);

  /**
   * Retrieves a user by their hashed password and encryption key.
   *
   * @param hashedPassword The user's hashed password.
   * @param userKey  The encryption key.
   * @return The User object, or null if not found.
   */
  User getUser(String hashedPassword, String userKey);
}