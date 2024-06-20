package caribouapp.caribou.com.cariboucoffee.util;

public class CardParserPrefix {

    private static final String TAG = CardParserPrefix.class.getSimpleName();

    private int mStart;
    private int mFinish;
    private int mPrefixLength;

    public CardParserPrefix(int prefix) {
        mStart = prefix;
        mFinish = prefix;
        mPrefixLength = String.valueOf(prefix).length();
    }

    public CardParserPrefix(int start, int finish) {
        int startLength = String.valueOf(start).length();
        int finishLength = String.valueOf(finish).length();
        mPrefixLength = startLength;
        if (startLength != finishLength) {
            throw new IllegalArgumentException("Different start and finish lengths not supported! " + start + " vs " + finish);
        }
        if (start > finish) {
            mStart = finish;
            mFinish = start;
        } else {
            mStart = start;
            mFinish = finish;
        }
    }

    public boolean validate(String ccNumberAsString) {
        try {
            int ccNumber = Integer.valueOf(ccNumberAsString.substring(0, mPrefixLength));
            return (mStart <= ccNumber) && (ccNumber <= mFinish);
        } catch (RuntimeException ex) {
            Log.e(TAG, new LogErrorException("Error parsing CC number while validating " + ccNumberAsString, ex));
            return false;
        }
    }
}
