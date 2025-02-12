package models;

import services.CryptoService;
import services.DatabaseService;


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
	public String getEncryptionKey() {
		return encryptionKey;
	}
	

	// constructor
	public User(String password) {
		this.setId(50000);               // default value
		this.setPassword(password);
		this.setEncryptionKey(password);
	}
	
	
	// add User object to database
	// also updates id to match database value
	public void save() {
		User newUser = DatabaseService.addNewUser(
			this.getPassword(),
			this.getEncryptionKey()
		);
		this.setId(newUser.getId());
	}

}