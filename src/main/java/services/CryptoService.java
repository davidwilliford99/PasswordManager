package services;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Arrays;

/**
 * Provides cryptographic operations such as encryption, decryption, and hashing. Uses AES-256 for
 * encryption and SHA-256 for hashing.
 */
public class CryptoService implements ICryptoService {

  private final Cipher cipher;
  private final MessageDigest digest;

  /**
   * Constructs a new CryptoService with the required dependencies.
   *
   * @param cipher The Cipher instance for encryption/decryption.
   * @param digest The MessageDigest instance for hashing.
   */
  public CryptoService(Cipher cipher, MessageDigest digest) {
    this.cipher = cipher;
    this.digest = digest;
  }

  @Override
  public String encrypt(String input, String key) throws Exception {
    byte[] keyBytes = hexToBytes(key);
    byte[] adjustedKey = Arrays.copyOf(keyBytes, 32);

    SecretKeySpec secretKey = new SecretKeySpec(adjustedKey, "AES");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);

    byte[] encryptedBytes = cipher.doFinal(input.getBytes("UTF-8"));
    return Base64.getEncoder().encodeToString(encryptedBytes);
  }

  @Override
  public String decrypt(String encryptedString, String key) throws Exception {
    byte[] keyBytes = hexToBytes(key);
    byte[] adjustedKey = Arrays.copyOf(keyBytes, 32);

    SecretKeySpec secretKey = new SecretKeySpec(adjustedKey, "AES");
    cipher.init(Cipher.DECRYPT_MODE, secretKey);

    byte[] decodedBytes = Base64.getDecoder().decode(encryptedString);
    byte[] decryptedBytes = cipher.doFinal(decodedBytes);

    return new String(decryptedBytes, "UTF-8");
  }

  @Override
  public String hash(String input) throws UnsupportedEncodingException {
    byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
    StringBuilder hexString = new StringBuilder();

    for (byte b : hashBytes) {
      hexString.append(String.format("%02x", b));
    }

    return hexString.toString();
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