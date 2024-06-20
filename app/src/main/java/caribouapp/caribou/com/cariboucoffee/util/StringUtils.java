package caribouapp.caribou.com.cariboucoffee.util;

import android.content.Context;
import android.util.Base64;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;

/**
 * Created by andressegurola on 10/11/17.
 */

public final class StringUtils {

    private static final int PREFIX_END = 6;
    private static final int SUFFIX_LENGTH = 4;

    private StringUtils() {
    }

    public static String appendWithNewLine(String... parts) {
        return appendWith('\n', parts);
    }

    public static String appendWith(char separator, String... parts) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part != null && !part.trim().isEmpty()) {
                builder.append(part);

                if (i < parts.length - 1) {
                    builder.append(separator);
                }
            }
        }
        return builder.toString();
    }

    public static String joinWith(Collection<String> strings, String joinText) {
        return joinWith(strings, joinText, false);
    }

    public static String joinWith(Collection<String> strings, String joinText, boolean ignoreNulls) {
        StringBuilder csv = new StringBuilder();
        Iterator<String> it = strings.iterator();
        while (it.hasNext()) {
            String nextString = it.next();
            if (it.hasNext() && nextString == null && ignoreNulls) {
                continue;
            }
            csv.append(nextString);
            if (it.hasNext()) {
                csv.append(joinText);
            }

        }
        return csv.toString();
    }

    public static String capitalize(String text) {
        if (text == null) {
            return null;
        }

        return text.substring(0, 1).toUpperCase(Locale.US) + text.substring(1);
    }

    /**
     * Returns the last length chars (into a String) from the string provided
     *
     * @param string
     * @param length
     * @return
     */
    public static String getSuffix(String string, int length) {
        if (string == null) {
            return null;
        }

        if (length >= string.length()) {
            return string;
        }

        int startingIndex = string.length() - length;
        int endingIndex = string.length();

        return string.substring(startingIndex, endingIndex);
    }

    public static String formatMonth(String sMonth) {
        return format("%02d", Integer.parseInt(sMonth));
    }

    public static String formatYear(String sYear) {
        int year = Integer.parseInt(sYear);
        return year > 999 ? String.valueOf(year) : year < 100
                ? String.valueOf(year + 2000) : null; // This last condition doesn't make sense, that's why it returns null
    }

    public static String toPhoneNumberWithoutSymbols(String phoneNumberWithSymbols) {
        return isEmpty(phoneNumberWithSymbols) ? "" : phoneNumberWithSymbols.replaceAll("[^0-9]", "");
    }

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    /**
     * Masks everything but the first 6 digits and the last 4 (this is what is returned from the API)
     *
     * @param ccNumber
     * @return
     */
    public static String maskCC(String ccNumber) {
        if (ccNumber.length() < PREFIX_END + SUFFIX_LENGTH + 1) {
            return ccNumber;
        }
        String prefix = ccNumber.substring(0, PREFIX_END);
        String suffix = getSuffix(ccNumber, SUFFIX_LENGTH);
        StringBuilder builder = new StringBuilder();
        builder.append(prefix);
        for (int i = PREFIX_END; i < ccNumber.length() - SUFFIX_LENGTH; i++) {
            builder.append(AppConstants.CC_MASKING_SYMBOL);
        }
        builder.append(suffix);

        return builder.toString();
    }

    public static String formatPhoneNumber(String phone) {
        if (phone == null) {
            return null;
        }
        if (phone.length() == 12) {
            phone = phone.substring(2);
        }
        if (phone.length() == 10 && phone.matches("[0-9]+")) {
            return format("(%s) %s-%s", phone.substring(0, 3), phone.substring(3, 6), phone.substring(6, 10));
        } else {
            return phone;
        }
    }

    public static String mapToString(Map<String, String> stringMap) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            stringBuilder.append(format("<%s> %s", entry.getKey(), entry.getValue()));
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }

    public static String format(String stringToFormat, Object... objects) {
        return String.format(AppConstants.DEFAULT_LOCALE, stringToFormat, objects);
    }

    public static String fromClasspathResource(String classpathResourcePath) throws IOException {
        return fromReader(new InputStreamReader(StringUtils.class.getResourceAsStream(classpathResourcePath)));
    }

    private static String fromReader(Reader reader) throws IOException {
        ByteArrayOutputStream buf = toByteArrayOutputStream(reader);
        return buf.toString("UTF-8");
    }

    public static byte[] bytesFromInputStream(InputStream istream) throws IOException {
        ByteArrayOutputStream buf = toByteArrayOutputStream(istream);
        return buf.toByteArray();
    }

    @NonNull
    private static ByteArrayOutputStream toByteArrayOutputStream(InputStream istream) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = istream.read();
        while (result != -1) {
            buf.write((byte) result);
            result = istream.read();
        }
        return buf;
    }

    @NonNull
    private static ByteArrayOutputStream toByteArrayOutputStream(Reader reader) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = reader.read();
        while (result != -1) {
            buf.write((byte) result);
            result = reader.read();
        }
        return buf;
    }

    public static boolean compareSimilarStrings(String stringSimilar, String stringToCompare) {
        return stringSimilar.replace(" ", "")
                .equalsIgnoreCase(
                        stringToCompare.replace(" ", ""));
    }


    public static boolean containsSimilarString(String string1, String string2) {
        if (string1 == null) {
            return false;
        }
        return toSimplifiedString(string1).contains(toSimplifiedString(string2));
    }

    public static String toSimplifiedString(String searchTerms) {
        return searchTerms == null ? null
                : searchTerms.replace(" ", "").toLowerCase(Locale.US);
    }

    public static String formatMoneyAmount(Context context, BigDecimal balance, Integer digitsAfterComma) {
        if (digitsAfterComma == null) {
            digitsAfterComma = AppConstants.DEFAULT_DIGITS_AFTER_COMMA;
        }
        return balance != null
                ? format(context.getString(R.string.balance), balance.setScale(digitsAfterComma, RoundingMode.DOWN).toString())
                : context.getString(R.string.empty_balance);
    }

    public static String formatPointsAmount(Context context, BigDecimal balance, Integer digitsAfterComma) {
        if (digitsAfterComma == null) {
            digitsAfterComma = AppConstants.DEFAULT_DIGITS_AFTER_COMMA;
        }
        return balance != null
                ? format(context.getString(R.string.points_balance), balance.setScale(digitsAfterComma, RoundingMode.DOWN).toString())
                : context.getString(R.string.empty_points_balance);
    }

    public static String formatMoneyAmount(Context context, BigDecimal amount) {
        int digitsAfterComma = amount.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0
                ? AppConstants.NO_DIGITS_AFTER_COMMA : AppConstants.DEFAULT_DIGITS_AFTER_COMMA;
        return StringUtils.formatMoneyAmount(context, amount, digitsAfterComma);
    }


    public static String contentDescriptionMoneyAmount(Context context, BigDecimal moneyAmount) {
        return context.getString(R.string.money_amount, StringUtils
                .formatMoneyAmount(context, moneyAmount, null));
    }

    public static boolean isInteger(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int i = Integer.parseInt(strNum.trim());
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String repeating(String input, int count) {
        return new String(new char[count]).replace("\0", input);
    }

    // Masks email address: John.Doe@gmail.com -> J••••••e@gmail.com
    public static String maskEmail(String input) {
        if (input == null) {
            return null;
        }

        if (input.contains("@")) {
            int domainAtIndex = input.lastIndexOf("@");
            String username = input.substring(0, domainAtIndex);
            String domain = input.substring(domainAtIndex);

            // Handle degenerate case with username two chars or less, ed@gmail.com -> ••@gmail.com
            if (username.length() <= 2) {
                return repeating("•", username.length()) + domain;
            }

            // Mask all but first and last, abcd -> a••d
            String maskedUsername = username.charAt(0)
                    + repeating("•", Math.max(0, username.length() - 2))
                    + username.charAt(username.length() - 1);
            return maskedUsername + domain;
        } else {
            // No domain symbol means invalid email address, just return the string fully masked
            return repeating("•", input.length());
        }
    }

    public static boolean validateNumber(String ccNumber) {
        return ccNumber.matches("[0-9]+");
    }

    /**
     * Remove white spaces from String
     */
    public static String removeWhiteSpace(String value) {
        if (value.contains(" ")) {
            return value.replace(" ", "");
        } else {
            return value;
        }
    }

    public static RSAPublicKey readPublicKey(String key) throws Exception {
        byte[] encoded = android.util.Base64.decode(key, Base64.DEFAULT);
        return readPublicKey(encoded);
    }

    public static RSAPublicKey readPublicKey(byte[] key) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    public static byte[] encrypt(PublicKey key, byte[] plaintext,
                                 String cipherAlgorithm)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext);
    }

    public static String encryptToBase64(PublicKey key, byte[] plaintext, String cipherAlgorithm) {
        try {
            byte[] data = encrypt(key, plaintext, cipherAlgorithm);
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            throw new IllegalArgumentException("Public key failed to encrypt provided parameter!", e);
        }
    }
}
