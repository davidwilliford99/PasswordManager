package services;

import java.io.UnsupportedEncodingException;

/**
 * Defines cryptographic operations such as encryption, decryption, and hashing.
 */
public interface ICryptoService {

  /**
   * Encrypts the input string using the provided key.
   *
   * @param input The string to encrypt.
   * @param key   The encryption key.
   * @return The encrypted string.
   * @throws Exception If an error occurs during encryption.
   */
  String encrypt(String input, String key) throws Exception;

  /**
   * Decrypts the encrypted string using the provided key.
   *
   * @param encryptedString The string to decrypt.
   * @param key             The decryption key.
   * @return The decrypted string.
   * @throws Exception If an error occurs during decryption.
   */
  String decrypt(String encryptedString, String key) throws Exception;

  /**
   * Hashes the input string using a cryptographic hash function.
   *
   * @param input The string to hash.
   * @return The hashed string.
   * @throws UnsupportedEncodingException If the encoding is not supported.
   */
  String hash(String input) throws UnsupportedEncodingException;
}