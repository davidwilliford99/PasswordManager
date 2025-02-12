package models;

import services.CryptoService;

public class User {
	
	// properties
	private int id;
	private String password;
	private String encryptionKey;
	
	
	// getters & setters
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	private void setEncryptionKey(String password) {
		this.encryptionKey = CryptoService.hash(password);
	}
	

	// constructor
	public User(int id, String password) {
		this.setId(id);
		this.setPassword(password);
		this.setEncryptionKey(password);
	}

}