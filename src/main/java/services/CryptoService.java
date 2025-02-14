package services;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Arrays;


public class CryptoService {
	
	
	// Encryption Keys are the SHA-256 hashed password
	
	
	
	/**
	 * 
	 * @param        input
	 * @param        key   (hex string representation of hashed password)
	 * 
	 * @description  Encrypt string using the key
	 * 
	 * @return       Encrypted string
	 * 
	 */
	public static String encrypt(String input, String key) throws Exception {
		
		// System.out.println("Encryption Key: "  + key);
	
		byte[] keyBytes = hexToBytes(key);                               // turn key into bytes array
	    byte[] adjustedKey = Arrays.copyOf(keyBytes, 32);                // ✅ Ensure exactly 32 bytes for AES-256
		
		SecretKeySpec secretKey = new SecretKeySpec(adjustedKey, "AES");
	    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	    
	    // Return encrypted string
	    byte[] encryptedBytes = cipher.doFinal(input.getBytes("UTF-8"));
	    return Base64.getEncoder().encodeToString(encryptedBytes);        // ✅ Ensure Base64 encoding
	}
	
	
	

	/**
	 * 
	 * @param        encryptedString
	 * @param        key     (hex string representation of hashed password)
	 * 
	 * @description  Undo encryption on string using the key
	 * 
	 * @return       Original input string before encryption
	 * 
	 */
	public static String decrypt(String encryptedString, String key) throws Exception {
		
		// System.out.println("Dencryption Key: "  + key);
		  
        byte[] keyBytes = hexToBytes(key);                                 // Convert hashed hex key to bytes
        byte[] adjustedKey = Arrays.copyOf(keyBytes, 32);                  // Ensure exactly 32 bytes for AES-256
      
        SecretKeySpec secretKey = new SecretKeySpec(adjustedKey, "AES");   // Create secret key instance
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
      
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedString); // Decode Base64 first
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
      
        return new String(decryptedBytes, "UTF-8");                        // Ensure correct UTF-8 decoding
	}
	
	
	
	
	/**
	 * 
	 * @description  Hashing a string via SHA-256
	 * 
	 * @param        input
	 * @return       hashed input (hex-decimal string)
	 * 
	 * @throws       UnsupportedEncodingException 
	 * 
	 */
	public static String hash(String input) throws UnsupportedEncodingException {
		try 
		{
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
	        StringBuilder hexString = new StringBuilder();
	        
	        for (byte b : hashBytes) {
	            hexString.append(String.format("%02x", b));
	        }
	        
	        return hexString.toString();
	    } 
		catch (NoSuchAlgorithmException e) 
		{
	        throw new RuntimeException("SHA-256 algorithm not found!", e);
	    }
	}
	
	
	
	
	/**
	 * 
	 * @description  Transfer hex string to byte array
	 * @description  Slightly more complex than necessary, to avoid unnecessary conversions.
	 * 
	 * @param        hex
	 * @return       Byte array
	 * 
	 */
	private static byte[] hexToBytes(String hex) {
		
	    int len = hex.length();
	    byte[] data = new byte[len / 2];
	    
	    for (int i = 0; i < len; i += 2) {
		    data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
		                         + Character.digit(hex.charAt(i+1), 16));
	    }
	    
	    return data;
	}

}
