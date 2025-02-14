package models;

import services.CryptoService;


public class PasswordRecord {
	
	// Attributes
	private int id;
	private int userId;
	private String resource;
	private String password;
	
	
	// Getters & Setters
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
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
	public PasswordRecord(int id, int userId, String resource, String password) {
		this.setResource(resource);
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
	 * @throws       Exception 
	 * 
	 */
	public void encrypt(String userKey) throws Exception {
		CryptoService.encrypt(this.getPassword(), userKey);
	}
}
