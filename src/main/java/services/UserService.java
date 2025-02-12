package services;

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
		User user = DatabaseService.getUser(password, CryptoService.hash(password));
		if(user.getPassword() != null) {
			return true;
		}
		return false;
	}

}
