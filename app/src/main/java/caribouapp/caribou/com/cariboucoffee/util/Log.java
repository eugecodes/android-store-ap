package caribouapp.caribou.com.cariboucoffee.util;

import com.newrelic.agent.android.NewRelic;

/**
 * Created by asegurola on 5/3/18.
 */

public final class Log {

    private static final boolean PROD_SERVER;

    private static final boolean DEBUG_BUILD;

    static {
        PROD_SERVER = AppUtils.isProductionBuild();
        DEBUG_BUILD = AppUtils.isDebug();
    }

    private Log() {
    }

    public static void d(String tag, String message) {
        android.util.Log.d(tag, message);
    }

    public static void d(String tag, String message, Throwable throwable) {
        android.util.Log.d(tag, message, throwable);
    }

    public static void i(String tag, String message) {
        android.util.Log.i(tag, message);
    }

    public static void i(String tag, String message, Throwable throwable) {
        android.util.Log.i(tag, message, throwable);
    }

    public static void w(String tag, String message) {
        android.util.Log.w(tag, message);
    }

    public static void w(String tag, String message, Throwable throwable) {
        android.util.Log.w(tag, message, throwable);
    }

    public static void e(String tag, Exception logErrorException) {
        if (NewRelic.isStarted()) {
            try {
                NewRelic.recordHandledException(logErrorException);
            } catch (RuntimeException e) {
                android.util.Log.e(tag, logErrorException.getMessage(), logErrorException.getCause());
            }
        } else {
            android.util.Log.e(tag, logErrorException.getMessage(), logErrorException.getCause());
        }
    }
}
