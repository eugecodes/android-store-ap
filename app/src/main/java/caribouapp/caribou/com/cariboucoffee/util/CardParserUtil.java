package caribouapp.caribou.com.cariboucoffee.util;

//  Created by Jason Clark on 6/28/16. Ported by jmsmuy on 11/1/18

public final class CardParserUtil {

    private static final int[] AMEX_PREFIXES_RAW = {34, 37};
    private static final int[] DINERS_PREFIXES_RAW = {300, 305, 309, 36, 38, 39};
    private static final int[] DISCOVER_PREFIXES_RAW = {6011, 65, 644, 649, 622126, 622925};
    private static final int[] JCB_PREFIXES_RAW = {3528, 3589};
    private static final int[] MASTER_CARD_PREFIXES_RAW = {51, 55, 2221, 2720};
    private static final int[] VISA_PREFIXES_RAW = {4};


    private static final CardParserPrefix[] AMEX_PREFIXES = new CardParserPrefix[]{
            new CardParserPrefix(AMEX_PREFIXES_RAW[0]),
            new CardParserPrefix(AMEX_PREFIXES_RAW[1])
    };

    private static final CardParserPrefix[] DINERS_PREFIXES = new CardParserPrefix[]{
            new CardParserPrefix(DINERS_PREFIXES_RAW[0], DINERS_PREFIXES_RAW[1]),
            new CardParserPrefix(DINERS_PREFIXES_RAW[2]),
            new CardParserPrefix(DINERS_PREFIXES_RAW[3]),
            new CardParserPrefix(DINERS_PREFIXES_RAW[4]),
            new CardParserPrefix(DINERS_PREFIXES_RAW[5])
    };

    private static final CardParserPrefix[] DISCOVER_PREFIXES = new CardParserPrefix[]{
            new CardParserPrefix(DISCOVER_PREFIXES_RAW[0]),
            new CardParserPrefix(DISCOVER_PREFIXES_RAW[1]),
            new CardParserPrefix(DISCOVER_PREFIXES_RAW[2], DISCOVER_PREFIXES_RAW[3]),
            new CardParserPrefix(DISCOVER_PREFIXES_RAW[4], DISCOVER_PREFIXES_RAW[5])
    };

    private static final CardParserPrefix[] JCB_PREFIXES = new CardParserPrefix[]{
            new CardParserPrefix(JCB_PREFIXES_RAW[0], JCB_PREFIXES_RAW[1])
    };

    private static final CardParserPrefix[] MASTER_CARD_PREFIXES = new CardParserPrefix[]{
            new CardParserPrefix(MASTER_CARD_PREFIXES_RAW[0], MASTER_CARD_PREFIXES_RAW[1]),
            new CardParserPrefix(MASTER_CARD_PREFIXES_RAW[2], MASTER_CARD_PREFIXES_RAW[3])
    };

    private static final CardParserPrefix[] VISA_PREFIXES = new CardParserPrefix[]{
            new CardParserPrefix(VISA_PREFIXES_RAW[0])
    };

    private static final int[] AMEX_LENGTHS = {15};
    private static final int[] DINERS_LENGTHS = {14};
    private static final int[] DISCOVER_LENGTHS = {16};
    private static final int[] JCB_LENGTHS = {16};
    private static final int[] MASTER_CARD_LENGTHS = {16};
    private static final int[] VISA_LENGTHS = {13, 16, 19};

    private static final int AMEX_CCV_LENGTH = 4;
    private static final int DINERS_CCV_LENGTH = 3;
    private static final int DISCOVER_CCV_LENGTH = 3;
    private static final int JCB_CCV_LENGTH = 3;
    private static final int MASTER_CARD_CCV_LENGTH = 3;
    private static final int VISA_CCV_LENGTH = 3;

    private CardParserUtil() {
    }

    /**
     * Returns the enum for the cc provided
     * null if no card is found!
     *
     * @param ccNumber cc number (mandatory)
     * @param ccv      ccv number (can be null to indicate no ccv)
     * @return
     */
    public static CardType getCardType(String ccNumber, String ccv) throws IllegalArgumentException {
        // First we perform a luhn check on the cc
        if (!luhnCheck(ccNumber)) {
            return null;
        }

        // Now we look within our card types for this prefix/ccv/length combo
        for (CardType cardType : CardType.values()) {
            if (cardType.isValid(ccNumber, ccv)) {
                return cardType;
            }
        }
        return null;
    }

    /**
     * Algorithm sourced from
     * https://www.fivecentnickel.com/how-do-you-know-if-a-credit-card-number-is-valid/
     *
     * @param ccNumberString
     * @return
     */
    private static boolean luhnCheck(String ccNumberString) throws IllegalArgumentException {
        int[] ccNumber = getCcNumberAsIntArray(ccNumberString);

        int sum = 0;
        int offset = ccNumber.length % 2 == 0 ? 0 : 1;
        for (int i = 0; i < ccNumber.length; i++) {
            if ((i + offset) % 2 == 0) {
                ccNumber[i] *= 2;
                if (ccNumber[i] >= 10) {
                    ccNumber[i] = ccNumber[i] % 10 + ccNumber[i] / 10;
                }
            }
            sum += ccNumber[i];
        }

        return sum % 10 == 0;
    }

    private static int[] getCcNumberAsIntArray(String ccNumberString) throws IllegalArgumentException {
        int counter = 0;
        int[] ccNumberArray = new int[ccNumberString.length()];
        for (char charNumber : ccNumberString.toCharArray()) {
            if (charNumber > '9' || charNumber < '0') {
                throw new IllegalArgumentException("Invalid credit card digit: " + charNumber);
            }
            ccNumberArray[counter++] = charNumber - '0';
        }

        return ccNumberArray;
    }

    public enum CardType {
        AMEX(AMEX_PREFIXES, AMEX_LENGTHS, AMEX_CCV_LENGTH),
        DINERS(DINERS_PREFIXES, DINERS_LENGTHS, DINERS_CCV_LENGTH),
        DISCOVER(DISCOVER_PREFIXES, DISCOVER_LENGTHS, DISCOVER_CCV_LENGTH),
        JCB(JCB_PREFIXES, JCB_LENGTHS, JCB_CCV_LENGTH),
        MASTER_CARD(MASTER_CARD_PREFIXES, MASTER_CARD_LENGTHS, MASTER_CARD_CCV_LENGTH),
        VISA(VISA_PREFIXES, VISA_LENGTHS, VISA_CCV_LENGTH);

        private CardParserPrefix[] mPrefixes;
        private int[] mLengths;
        private int mCcvLength;

        CardType(CardParserPrefix[] prefixes, int[] lengths, int ccvLength) {
            mPrefixes = prefixes;
            mLengths = lengths;
            mCcvLength = ccvLength;
        }

        /**
         * This function checks if this particular enum fits the ccNumber passed as an argument
         *
         * @param ccNumber
         * @return
         */
        public boolean isValid(String ccNumber) {
            boolean validLength = false;
            // First we do a length check
            for (int length : mLengths) {
                if (length == ccNumber.length()) {
                    validLength = true;
                }
            }

            if (!validLength) {
                return false;
            }

            // Now we check for prefixes
            for (CardParserPrefix prefix : mPrefixes) {
                if (prefix.validate(ccNumber)) {
                    return true;
                }
            }

            return false;
        }

        /**
         * This function checks if this particular enum fits the ccNumber passed as an argument
         * as well as a ccv
         *
         * @param ccNumber
         * @param ccv
         * @return
         */
        public boolean isValid(String ccNumber, String ccv) {
            // First we do a length check on the ccv
            if (ccv != null && ccv.length() != mCcvLength) {
                return false;
            }

            // Now we use the previous function
            return isValid(ccNumber);
        }
    }

}
