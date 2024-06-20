package caribouapp.caribou.com.cariboucoffee.mvp.authentication;

import androidx.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.R;

/**
 * Created by andressegurola on 1/3/18.
 */

public final class PasswordUtil {

    private PasswordUtil() {
    }

    public enum PasswordHint {
        CHARS_8(R.string.password_hint_8_chars, "^.{8,}$"),
        UPPERCASE_1(R.string.password_hint_1_uppercase, "^.*[A-Z].*$"),
        LOWERCASE_1(R.string.password_hint_1_lowercase, "^.*[a-z].*$"),
        NUMBER_OR_SPECIAL_1(R.string.password_hint_1_number_or_special, "^.*(\\d|[^A-Za-z0-9]).*$"),
        TWO_EQUALS_CHARACTERS(R.string.password_hint_2_consecutive_character_in_a_row, ".*(.)\\1\\1+.*");

        @StringRes
        private int mStringId;

        private String mRegex;

        PasswordHint(@StringRes int hintRes, String regex) {
            mStringId = hintRes;
            mRegex = regex;
        }

        @StringRes
        public int getHintRes() {
            return mStringId;
        }

        public String getRegex() {
            return mRegex;
        }
    }

    /**
     * Calculates what hints show be displayed to the user in order to comply with the password requirements.
     *
     * @param password
     * @return the set of hints that need to be addressed
     */
    public static List<PasswordHint> validatePassword(String password, boolean checkConsecutiveCharacters) {
        if (password == null) {
            password = "";
        }
        List<PasswordHint> hints = new ArrayList<>();
        if (!password.matches(PasswordHint.CHARS_8.getRegex())) {
            hints.add(PasswordHint.CHARS_8);
        }
        if (!password.matches(PasswordHint.UPPERCASE_1.getRegex())) {
            hints.add(PasswordHint.UPPERCASE_1);
        }
        if (!password.matches(PasswordHint.LOWERCASE_1.getRegex())) {
            hints.add(PasswordHint.LOWERCASE_1);
        }
        if (!password.matches(PasswordHint.NUMBER_OR_SPECIAL_1.getRegex())) {
            hints.add(PasswordHint.NUMBER_OR_SPECIAL_1);
        }
        if (checkConsecutiveCharacters && password.matches(PasswordHint.TWO_EQUALS_CHARACTERS.getRegex())) {
            hints.add(PasswordHint.TWO_EQUALS_CHARACTERS);
        }
        return hints;
    }

    /**
     * @param password
     * @param passwordConfirm
     * @return T/F IF {@param password} equals {@param passwordConfirm}
     */
    public static boolean validatePasswordConfirm(String password, String passwordConfirm) {
        return password != null && password.equals(passwordConfirm);
    }
}
