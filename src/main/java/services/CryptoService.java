package services;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Arrays;

/**
 * Provides cryptographic operations such as encryption, decryption, and hashing.
 * Uses AES-256 for encryption and SHA-256 for hashing.
 */
public class CryptoService {

	/**
	 * Encrypts a string using the provided key.
	 *
	 * @param input The string to encrypt.
	 * @param key   The encryption key as a hex string (SHA-256 hashed password).
	 * @return The encrypted string in Base64 encoding.
	 * @throws Exception If encryption fails.
	 */
	public static String encrypt(String input, String key) throws Exception {
		byte[] keyBytes = hexToBytes(key);
		byte[] adjustedKey = Arrays.copyOf(keyBytes, 32);

		SecretKeySpec secretKey = new SecretKeySpec(adjustedKey, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);

		byte[] encryptedBytes = cipher.doFinal(input.getBytes("UTF-8"));
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	/**
	 * Decrypts an encrypted string using the provided key.
	 *
	 * @param encryptedString The encrypted string in Base64 encoding.
	 * @param key             The decryption key as a hex string (SHA-256 hashed password).
	 * @return The original decrypted string.
	 * @throws Exception If decryption fails.
	 */
	public static String decrypt(String encryptedString, String key) throws Exception {
		byte[] keyBytes = hexToBytes(key);
		byte[] adjustedKey = Arrays.copyOf(keyBytes, 32);

		SecretKeySpec secretKey = new SecretKeySpec(adjustedKey, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);

		byte[] decodedBytes = Base64.getDecoder().decode(encryptedString);
		byte[] decryptedBytes = cipher.doFinal(decodedBytes);

		return new String(decryptedBytes, "UTF-8");
	}

	/**
	 * Hashes a string using the SHA-256 algorithm.
	 *
	 * @param input The string to hash.
	 * @return The hashed string as a hex-decimal string.
	 * @throws UnsupportedEncodingException If UTF-8 encoding is not supported.
	 */
	public static String hash(String input) throws UnsupportedEncodingException {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
			StringBuilder hexString = new StringBuilder();

			for (byte b : hashBytes) {
				hexString.append(String.format("%02x", b));
			}

			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 algorithm not found!", e);
		}
	}

	/**
	 * Converts a hex string to a byte array.
	 *
	 * @param hex The hex string to convert.
	 * @return The corresponding byte array.
	 */
	private static byte[] hexToBytes(String hex) {
		int len = hex.length();
		byte[] data = new byte[len / 2];

		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
					+ Character.digit(hex.charAt(i + 1), 16));
		}

		return data;
	}
}