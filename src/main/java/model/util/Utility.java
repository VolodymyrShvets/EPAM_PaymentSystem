package model.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public final class Utility {
    public static long createRandomNumber(long len) {
        if (len > 18)
            throw new IllegalStateException("Too many digits");

        long tLen = (long) Math.pow(10, len - 1) * 9;

        long number = (long) (Math.random() * tLen) + (long) Math.pow(10, len - 1);

        String tVal = number + "";
        if (tVal.length() != len) {
            throw new IllegalStateException("The random number '" + tVal + "' is not '" + len + "' digits");
        }
        return Long.parseLong(tVal);
    }

    public static long createCardNumber() {
        long cardNumber = createRandomNumber(16);

        while (!validateCreditCardNumber(String.valueOf(cardNumber)))
            cardNumber = createRandomNumber(16);

        return cardNumber;
    }

    // Алгоритм Луна для валидации номера банковской карты
    public static boolean validateCreditCardNumber(String value) {
        int sum = Character.getNumericValue(value.charAt(value.length() - 1));
        int parity = value.length() % 2;
        for (int i = value.length() - 2; i >= 0; i--) {
            int summand = Character.getNumericValue(value.charAt(i));
            if (i % 2 == parity) {
                int product = summand * 2;
                summand = (product > 9) ? (product - 9) : product;
            }
            sum += summand;
        }
        return sum % 10 == 0;
    }

    // ---------------------  hardcoded bad practice, but works if input is CYRILLIC ------------------------
    public static String encode(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.ISO_8859_1);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String hash(String password) {
        String strongPasswordHash = null;
        try {
            strongPasswordHash = generateStrongPasswordHash(password);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return strongPasswordHash;
    }

    private static String generateStrongPasswordHash(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] hash = factory.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    private static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);

        int paddingLength = (array.length * 2) - hex.length();

        if (paddingLength > 0)
            return String.format("%0" + paddingLength + "d", 0) + hex;
        else
            return hex;
    }

    public static boolean validatePassword(String originalPassword, String storedPassword) {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);

        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);
        int diff = 0;

        try {
            PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] testHash = factory.generateSecret(spec).getEncoded();

            diff = hash.length ^ testHash.length;

            for (int i = 0; i < hash.length && i < testHash.length; i++) {
                diff |= hash[i] ^ testHash[i];
            }
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }

        return diff == 0;
    }

    private static byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
}
