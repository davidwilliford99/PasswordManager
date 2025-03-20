package dal;

import models.User;

/**
 * Defines data access operations for users.
 */
public interface IUserDao {

  /**
   * Adds a new user with the provided password and encryption key.
   *
   * @param password The user's password.
   * @param userKey  The encryption key.
   * @return The created User.
   */
  User addNewUser(String password, String userKey);

  /**
   * Retrieves a user by their password and encryption key.
   *
   * @param password The user's password.
   * @param userKey  The encryption key.
   * @return The User object, or null if not found.
   */
  User getUser(String password, String userKey);
}