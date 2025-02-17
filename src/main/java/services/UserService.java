package services;

import java.io.UnsupportedEncodingException;

import models.User;


public class UserService {
	
	/**
	 * 
	 * @description  Takes in a password, and checks if user exists
	 * 
	 * @param        password
	 * @return       boolean (designating if authentication was successful
	 * 
	 */
	public static boolean authenticate(String password) {
		User user = null;
		
		try {
			user = DatabaseService.getUser(password, CryptoService.hash(password));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 50000 is the default id for users not stored in the database
		if(user.getId() !=  50000) {    
			return true;
		}
		return false;
	}

}
