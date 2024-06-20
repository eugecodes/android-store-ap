package caribouapp.caribou.com.cariboucoffee.util;

import android.content.Context;

import caribouapp.caribou.com.cariboucoffee.BuildConfig;
import caribouapp.caribou.com.cariboucoffee.R;

public final class DeeplinkUtil {

    private DeeplinkUtil() {
    }

    public static String buildSourceAppFinishActivity(Context context) {
        return context.getString(R.string.applink_scheme) + BuildConfig.FINISH_ACTIVITY_REDIRECT;
    }
}
