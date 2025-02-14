package models;

import java.io.UnsupportedEncodingException;

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
		try {this.encryptionKey = CryptoService.hash(password);} 
		catch (UnsupportedEncodingException e) {e.printStackTrace();}
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
	
	
	public String toString() {
		return "{" + "\n\t" + this.id + "\n\t" + this.password + '\n' + '}';
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