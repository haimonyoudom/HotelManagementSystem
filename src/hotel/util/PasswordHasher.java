package hotel.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for hashing and verifying passwords using SHA-256.
 * Provides secure password storage by never keeping plain-text passwords in memory.
 */
public class PasswordHasher {

    private static final String ALGORITHM = "SHA-256";

    /**
     * Hashes a plain-text password using SHA-256 algorithm.
     * 
     * @param plainPassword the plain-text password to hash
     * @return a hexadecimal string representation of the hashed password
     * @throws RuntimeException if SHA-256 algorithm is not available
     */
    public static String hash(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hashBytes = digest.digest(plainPassword.getBytes());
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies if a plain-text password matches its hashed version.
     * 
     * @param plainPassword the plain-text password to verify
     * @param hashedPassword the previously hashed password to compare against
     * @return true if the plain password matches the hash, false otherwise
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        String hashOfPlain = hash(plainPassword);
        return hashOfPlain.equals(hashedPassword);
    }

    /**
     * Converts a byte array to a hexadecimal string representation.
     * 
     * @param bytes the byte array to convert
     * @return hexadecimal string representation
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
