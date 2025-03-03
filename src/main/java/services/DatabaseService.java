package services;

import java.util.*;
import java.sql.*;
import java.io.*;

import models.PasswordRecord;
import models.User;

import static utils.AppDataDirectory.DB_PATH;
import static utils.AppDataDirectory.DB_URL;

/**
 * @description Static methods for database queries
 */
public class DatabaseService {

	/**
	 * 
	 * @description Connects to database
	 * 
	 * @return Connection object
	 *
	 */
	private static Connection connect(String userKey) throws SQLException {

		Connection conn = DriverManager.getConnection(DB_URL);
		String hashedKey = userKey;

		try {
			Statement stmt = conn.createStatement();
			stmt.execute("PRAGMA key = '" + hashedKey + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return conn;
	}

	/**
	 * 
	 * @description Create and set up the database tables needed for the application
	 * @description Also sets up the encryption
	 * 
	 * @param Database encryption key
	 * 
	 */
	public static void tableSetup(String userKey) {
		try {
			File dbFile = new File(DB_PATH);
			boolean isNewDatabase = !dbFile.exists();

			if (isNewDatabase) {
				dbFile.createNewFile();
			}

			// Connect to the database and apply encryption
			Connection connect = connect(userKey);
			Statement stmt = connect.createStatement();

			// 🔹 Apply encryption key for SQLCipher
			stmt.execute("PRAGMA key = '" + CryptoService.hash(userKey) + "';");

			// 🔹 If the database already exists, migrate it to encryption
			if (!isNewDatabase) {
				stmt.execute("PRAGMA cipher_migrate;");
			}

			// Create tables
			String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "password TEXT NOT NULL"
					+ ");";

			String createPasswordRecordsTable = "CREATE TABLE IF NOT EXISTS password_records ("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "user_id INTEGER NOT NULL, "
					+ "resource TEXT NOT NULL UNIQUE, "
					+ "password TEXT NOT NULL, "
					+ "FOREIGN KEY (user_id) REFERENCES users(id)"
					+ ");";

			stmt.execute(createUsersTable);
			stmt.execute(createPasswordRecordsTable);

			connect.close();
			System.out.println("Database setup complete and encrypted.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param userId
	 * @param resource
	 * @param password
	 * @throws Exception
	 * 
	 * @description Add new Password Record to Database
	 */
	public static PasswordRecord addNewPasswordRecord(int userId, String resource, String password, String userKey)
			throws Exception {

		String addNewPasswordRecord = String.format(
				"INSERT INTO password_records(user_id, resource, password) "
						+ "VALUES(%d, '%s', '%s');", // 🔹 Ensure user_id matches table definition
				userId, resource, CryptoService.encrypt(password, userKey));

		try {
			Connection connect = connect(userKey);
			Statement stmt = connect.createStatement();
			stmt.execute(addNewPasswordRecord);
			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		PasswordRecord newRecord = getPasswordRecord(resource, userKey);
		return newRecord; // return id of new record
	}

	/**
	 * @description Getting password records by their resource string
	 * 
	 * @param resource
	 * @return List of fields from table
	 * @throws Exception
	 * @throws NumberFormatException
	 */
	public static PasswordRecord getPasswordRecord(String resource, String userKey)
			throws NumberFormatException, Exception {

		String getPasswordRecordQuery = String.format(
				"SELECT * from password_records WHERE resource='%s'",
				resource);

		List<String> row = new ArrayList<String>();

		try {
			Connection connect = connect(userKey);
			Statement stmt = connect.createStatement();
			ResultSet queryResult = stmt.executeQuery(getPasswordRecordQuery);

			if (queryResult.next()) {
				int columnCount = queryResult.getMetaData().getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					row.add(queryResult.getString(i));
				}
			}

			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		PasswordRecord record = new PasswordRecord(
				Integer.parseInt(row.get(0)),
				Integer.parseInt(row.get(1)),
				row.get(2),
				row.get(3));

		return record;
	}

	/**
	 * 
	 * @description Return all password records for the user
	 *
	 * @return Array of PasswordRecords
	 * 
	 */
	public static List<PasswordRecord> getAllPasswordRecords(int userId, String userKey) {

		String allPaswordRecordsQuery = String.format(
				"SELECT * from password_records WHERE user_id=%d",
				userId);

		List<PasswordRecord> passwordRecords = new ArrayList<PasswordRecord>();

		try {
			Connection connect = connect(userKey);
			Statement stmt = connect.createStatement();
			ResultSet queryResult = stmt.executeQuery(allPaswordRecordsQuery);

			// Build query result (multiple rows) into list of PasswordRecords
			while (queryResult.next()) {
				PasswordRecord record = new PasswordRecord(
						queryResult.getInt("id"),
						queryResult.getInt("user_id"),
						queryResult.getString("resource"),
						queryResult.getString("password"));
				passwordRecords.add(record);
			}

			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return passwordRecords;
	}

	/**
	 * @description Getting password records by their resource string
	 * 
	 * @param resource
	 * @return List of fields from table
	 * @throws Exception
	 * @throws NumberFormatException
	 */
	public static boolean deletePasswordRecord(String resource, String userKey) {

		String getPasswordRecordQuery = String.format(
				"DELETE FROM password_records WHERE resource = '%s'",
				resource);

		try {
			Connection connect = connect(userKey);
			Statement stmt = connect.createStatement();
			stmt.executeQuery(getPasswordRecordQuery);

			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * @param password
	 * 
	 * @description Add new Password Record to DB
	 */
	public static User addNewUser(String password, String userKey) {

		String hashedPassword = "";
		try {
			hashedPassword = CryptoService.hash(password);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String addNewUser = String.format(
				"INSERT INTO users(password) VALUES('%s');",
				hashedPassword);

		try {
			Connection connect = connect(userKey);
			Statement stmt = connect.createStatement();
			stmt.execute(addNewUser);
			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		User newUser = getUser(password, userKey);
		return newUser;
	}

	/**
	 * @param password
	 * @param userKey
	 * 
	 * @description Get user record from DB
	 */
	public static User getUser(String password, String userKey) {

		String hashedPassword = "";
		try {
			hashedPassword = CryptoService.hash(password);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String getUserQuery = String.format(
				"SELECT * FROM users WHERE password = '%s'",
				hashedPassword);

		User user = new User("password");

		try {
			Connection connect = connect(userKey);
			Statement stmt = connect.createStatement();
			ResultSet queryResult = stmt.executeQuery(getUserQuery);

			if (queryResult.next()) {
				user.setId(queryResult.getInt("id"));
				user.setPassword(queryResult.getString("password"));
			}
			connect.close();
		}

		catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}
}
