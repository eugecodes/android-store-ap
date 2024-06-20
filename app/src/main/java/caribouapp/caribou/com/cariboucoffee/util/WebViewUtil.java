package caribouapp.caribou.com.cariboucoffee.util;

import android.webkit.CookieManager;

import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;

public final class WebViewUtil {

    private static final String TAG = UserServices.class.getSimpleName();

    private WebViewUtil() {
    }

    //Inspired in https://stackoverflow.com/questions/28998241/how-to-clear-cookies-and-cache-of-webview-on-android-when-not-in-webview
    public static void clearCookies() {
        try {
            CookieManager cookieManager;
            cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookies(null);
            cookieManager.flush();
        } catch (RuntimeException e) {
            Log.e(TAG, new LogErrorException("Cookie cleaning failed", e));
        }
    }
}
