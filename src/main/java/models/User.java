package models;

/**
 * Represents a user with an ID, password, and encryption key.
 */
public class User {

  private int id;
  private String password;
  private String encryptionKey;

  /**
   * Constructs a new User with the given password.
   *
   * @param password The user's password.
   */
  public User(String password) {
    this.id = 50000; // Default ID value
    this.password = password;
  }

  /**
   * Gets the user's password.
   *
   * @return The password.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets the user's password.
   *
   * @param password The password to set.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Gets the user's ID.
   *
   * @return The ID.
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the user's ID.
   *
   * @param id The ID to set.
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Gets the user's encryption key.
   *
   * @return The encryption key.
   */
  public String getEncryptionKey() {
    return encryptionKey;
  }

  /**
   * Sets the user's encryption key.
   *
   * @param encryptionKey The encryption key to set.
   */
  public void setEncryptionKey(String encryptionKey) {
    this.encryptionKey = encryptionKey;
  }

  @Override
  public String toString() {
    return "User {" + "\n\tid: " + id + "\n\tpassword: " + password + "\n}";
  }
}