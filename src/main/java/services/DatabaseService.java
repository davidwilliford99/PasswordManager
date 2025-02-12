package services;

import java.util.*;
import java.sql.*;

import models.PasswordRecord;
import models.User;



/**
 * @description Static methods for database queries
 */
public class DatabaseService {
	
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/db/database.db";

    
    /**
     * 
     * @description  Connects to database 
     * 
     * @return Connection object
     *
     */
    private static Connection connect(String userKey) throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        
        // Hash the user-provided key to get a 256-bit key
        String hashedKey = CryptoService.hash(userKey);

        // Apply encryption key for SQLCipher
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
     * @description  Set up the database tables needed for the application
     * 
     */
	public static void tableSetup(String userKey) {
		
		String createUsersTable = 
				"CREATE TABLE IF NOT EXISTS users("
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "password TEXT NOT NULL"
				+ ");";
		String createPasswordRecordsTable = 
				"CREATE TABLE IF NOT EXISTS password_records("
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "userId INTEGER NOT NULL,"
				+ "resource TEXT NOT NULL UNIQUE,"
				+ "password TEXT NOT NULL,"
				+ "FOREIGN KEY (user_id) REFERENCES users(id)"
				+ ");";
		
		try 
		{
			Connection connect = connect(userKey);
			Statement stmt = connect.createStatement();
			
			stmt.execute(createUsersTable);
            stmt.execute(createPasswordRecordsTable);
            
            connect.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 
	 * @param        userId
	 * @param        resource
	 * @param        password
	 * 
	 * @description  Add new Password Record to Database
	 * 
	 */
	public static String addNewPasswordRecord(int userId, String resource, String password, String userKey) {
		
		// TODO: encrypt password before storing it 
		
		String addNewPasswordRecord = String.format(
			  "INSERT INTO password_records(userId, resource, password)"
			+ "VALUES(%d, %s, %s);",
			   userId, resource, password
		);
		
		try 
		{
			Connection connect = connect(userKey);
			Statement stmt = connect.createStatement();
			stmt.execute(addNewPasswordRecord);
            connect.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		List<String> newRecord = getPasswordRecord(resource, userKey);
		return newRecord.get(0);   // return id of new record
	}
	
	
	
	/**
	 * 
	 * @description  Getting password records by their resource string
	 * 
	 * @param        resource
	 * @return       List of fields from table 
	 * 
	 */
	public static List<String> getPasswordRecord(String resource, String userKey) {
		
		String getPasswordRecordQuery = String.format(
				"SELECT * from password_records WHERE resource=%s",
				resource
		);
		
		List<String> row = new ArrayList<String>();

		try
		{
			Connection connect = connect(userKey);
			Statement stmt = connect.createStatement();
			ResultSet queryResult = stmt.executeQuery(getPasswordRecordQuery);
			
			// Build query result (a single row) into list of strings
			if(queryResult.next()) {
		        int columnCount = queryResult.getMetaData().getColumnCount();
		        for (int i = 1; i <= columnCount; i++) {
		        	row.add(queryResult.getString(i)); // Convert each column to String
		        }
			}
			
            connect.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return row;
	}
	
	
	
	/**
	 * 
	 * @description  Return all password records for the user
	 *
	 * @return       Array of PasswordRecords   
	 * 
	 */
	public static List<PasswordRecord> getAllPasswordRecords(int userId, String userKey) {
		
		String allPaswordRecordsQuery = String.format(
				"SELECT * from password_records WHERE userId=%d",
				userId
		);
		
		List<PasswordRecord> passwordRecords = new ArrayList<PasswordRecord>();
		
		try
		{
			Connection connect = connect(userKey);
			Statement stmt = connect.createStatement();
			ResultSet queryResult = stmt.executeQuery(allPaswordRecordsQuery);
			
			// Build query result (multiple rows) into list of PasswordRecords
			while (queryResult.next()) {
		        PasswordRecord record = new PasswordRecord(
		            queryResult.getInt("id"),
		            queryResult.getInt("userId"),
		            queryResult.getString("resource"),
		            queryResult.getString("password")
		        );
		        passwordRecords.add(record);
		    }
			
            connect.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		
		return passwordRecords;
	}
	
	
	
	/**
	 * 
	 * @param        password
	 * 
	 * @description  Add new Password Record to DB
	 * 
	 */
	public static void addNewUser(String password, String userKey) {
		
		String hashedPassword = CryptoService.hash(password);
		
		String addNewUser = String.format(
			  "INSERT INTO users(password)"
			+ "VALUES(%s);",
			  hashedPassword
		);
		
		try 
		{
			Connection connect = connect(userKey);
			Statement stmt = connect.createStatement();
			stmt.execute(addNewUser);
            connect.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 
	 * @param        password
	 * 
	 * @description  Add new Password Record to DB
	 * 
	 */
	public static User getUser(String password, String userKey) {
		
		String hashedPassword = CryptoService.hash(password);
		
		String getUserQuery = String.format(
			  "SELECT * FROM users WHERE password = %s",
			   hashedPassword
		);
		
		User user = new User(0, "password");
		
		try 
		{
			Connection connect = connect(userKey);
			Statement stmt = connect.createStatement();
			ResultSet queryResult = stmt.executeQuery(getUserQuery);
			if(queryResult.next()) {
				user.setId(queryResult.getInt("id"));
				user.setPassword(queryResult.getString("password"));
			}
            connect.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return user;
	}
}
