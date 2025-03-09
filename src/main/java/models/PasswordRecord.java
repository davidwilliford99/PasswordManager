package models;

/**
 * Represents a password record containing details such as ID, user ID, resource, and password.
 */
public class PasswordRecord {
	
	private int id;
	private int userId;
	private String resource;
	private String password;

	/**
	 * Constructs a new PasswordRecord with the specified details.
	 *
	 * @param id       The unique identifier for the password record.
	 * @param userId   The ID of the user associated with this password record.
	 * @param resource The resource (e.g., website or service) this password is for.
	 * @param password The password for the resource.
	 */
	public PasswordRecord(int id, int userId, String resource, String password) {
		this.setResource(resource);
		this.setPassword(password);
		this.setUserId(userId);
		this.setId(id);
	}

	/**
	 * Gets the password for the resource.
	 *
	 * @return The password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password for the resource.
	 *
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the resource (e.g., website or service) this password is for.
	 *
	 * @return The resource.
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * Sets the resource (e.g., website or service) this password is for.
	 *
	 * @param resource The resource to set.
	 */
	public void setResource(String resource) {
		this.resource = resource;
	}

	/**
	 * Gets the unique identifier for the password record.
	 *
	 * @return The ID.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the unique identifier for the password record.
	 *
	 * @param id The ID to set.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the ID of the user associated with this password record.
	 *
	 * @return The user ID.
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * Sets the ID of the user associated with this password record.
	 *
	 * @param userId The user ID to set.
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
}
