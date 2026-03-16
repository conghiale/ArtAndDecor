package org.artanddecor.utils;

import java.security.SecureRandom;

/**
 * Password utility class for generating secure random passwords
 */
public class PasswordUtils {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARS;
    
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generate a secure random password
     * 
     * @param length Password length (minimum 8 characters)
     * @return Generated password
     */
    public static String generateRandomPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }

        StringBuilder password = new StringBuilder(length);

        // Ensure at least one character from each group
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));

        // Fill the rest randomly
        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        // Shuffle the characters
        return shuffleString(password.toString());
    }

    /**
     * Generate a random password with default length of 12 characters
     * 
     * @return Generated password
     */
    public static String generateRandomPassword() {
        return generateRandomPassword(12);
    }

    /**
     * Shuffle characters in a string
     */
    private static String shuffleString(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    /**
     * Validate password strength
     * 
     * @param password Password to validate
     * @return true if password meets strength requirements
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = password.chars().anyMatch(c -> UPPERCASE.indexOf(c) >= 0);
        boolean hasLower = password.chars().anyMatch(c -> LOWERCASE.indexOf(c) >= 0);
        boolean hasDigit = password.chars().anyMatch(c -> DIGITS.indexOf(c) >= 0);
        boolean hasSpecial = password.chars().anyMatch(c -> SPECIAL_CHARS.indexOf(c) >= 0);

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}