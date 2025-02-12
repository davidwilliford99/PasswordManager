package models;

public class PasswordRecord {
	
	// Attributes
	private int id;
	private int userId;
	private String service;
	private String password;
	
	
	// Getters & Setters
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	
	
	// Constructor
	public PasswordRecord(int id, int userId, String service, String password) {
		this.setService(service);
		this.setPassword(password);
		this.setUserId(userId);
		this.setId(id);
	}
	
	
	
	/**
	 * 
	 * @param        userPassword
	 * @param        servicePassword
	 * 
	 * @description  Encrypt service password using your user password as the key
	 * 
	 * @return       Encrypted service password
	 * 
	 */
	public String encrypt(String userPassword, String servicePassword) {
		return "Encrypted service password";
	}
	
	
	/**
	 * 
	 * @param        userPassword
	 * @param        encryptedString
	 * 
	 * @description  Decrypt service password using your user password as the key
	 * 
	 * @return       Decrypted service password
	 * 
	 */
	public String decrypt(String userPassword, String encryptedString) {
		return "Decrypted service password";
	}
}
