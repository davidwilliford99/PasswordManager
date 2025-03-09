package services;

import java.io.UnsupportedEncodingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import models.User;

/**
 * Provides services related to user authentication and management.
 */
public class UserService {
	private static final Logger logger = LogManager.getLogger(UserService.class);

	/**
	 * Authenticates a user by checking if the provided password matches a user in the database.
	 *
	 * @param password The password to authenticate.
	 * @return True if authentication is successful, false otherwise.
	 */
	public static boolean authenticate(String password) {
		User user = null;

		try {
			user = DatabaseService.getUser(password, CryptoService.hash(password));
		} catch (UnsupportedEncodingException e) {
			logger.error("An error occurred while hashing the user's password", e);
		}

		// 50000 is the default ID for users not stored in the database
		return user != null && user.getId() != 50000;
	}
}