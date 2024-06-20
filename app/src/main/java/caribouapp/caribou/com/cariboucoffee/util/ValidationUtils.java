package caribouapp.caribou.com.cariboucoffee.util;

import caribouapp.caribou.com.cariboucoffee.AppConstants;

import static caribouapp.caribou.com.cariboucoffee.AppConstants.DEFAULT_TELEPHONE_LENGHT;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static boolean isValidZipCode(String zipCode) {
        return zipCode != null && zipCode.matches(AppConstants.ZIP_CODE_REGEX);
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return StringUtils.toPhoneNumberWithoutSymbols(phoneNumber).length() == DEFAULT_TELEPHONE_LENGHT;
    }
}
