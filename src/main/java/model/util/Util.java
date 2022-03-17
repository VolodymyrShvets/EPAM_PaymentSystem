package model.util;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public final class Util {
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

    public static String generateHashedPassword(String password) {
        String salt = "1234";
        int iterations = 1000;
        int keyLength = 512;
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes();

        byte[] hashedBytes = hashPassword(passwordChars, saltBytes, iterations, keyLength);
        return hashedBytes.toString();
        //return Hex.encodeHexString(hashedBytes);
    }

    private static byte[] hashPassword(final char[] password, final byte[] salt, final int iterations, final int keyLength) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return res;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    // ---------------------  hardcoded bad practice, but works if input is CYRILLIC ------------------------
    public static String encode(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.ISO_8859_1);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
