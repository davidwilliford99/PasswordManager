package utils;

public final class SqlQueries {

  private SqlQueries() {
    throw new UnsupportedOperationException("Utility class");
  }

  // Database setup queries
  public static final String PRAGMA_CIPHER_MIGRATE = "PRAGMA cipher_migrate;";
  public static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS users ("
      + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
      + "password TEXT NOT NULL"
      + ");";
  public static final String CREATE_PASSWORD_RECORDS_TABLE =
      "CREATE TABLE IF NOT EXISTS password_records ("
          + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
          + "user_id INTEGER NOT NULL, "
          + "resource TEXT NOT NULL UNIQUE, "
          + "password TEXT NOT NULL, "
          + "FOREIGN KEY (user_id) REFERENCES users(id)"
          + ");";

  // User queries
  public static final String INSERT_USER = "INSERT INTO users(password) VALUES(?);";
  public static final String SELECT_USER = "SELECT * FROM users WHERE password=?;";

  // Password record queries
  public static final String INSERT_PASSWORD_RECORD = "INSERT INTO password_records(user_id, resource, password) VALUES(?, ?, ?);";
  public static final String SELECT_PASSWORD_RECORD = "SELECT * FROM password_records WHERE resource=?;";
  public static final String SELECT_ALL_PASSWORD_RECORDS = "SELECT * FROM password_records WHERE user_id=?;";
  public static final String DELETE_PASSWORD_RECORD = "DELETE FROM password_records WHERE resource=?;";
}