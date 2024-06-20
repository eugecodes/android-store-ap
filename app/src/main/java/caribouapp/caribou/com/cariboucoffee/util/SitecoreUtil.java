package caribouapp.caribou.com.cariboucoffee.util;

import android.text.TextUtils;

/**
 * Created by asegurola on 2/6/18.
 */

public final class SitecoreUtil {

    private SitecoreUtil() {
    }

    public static String getImageUrlForMaxHeight(String imageUrl, int maxHeight) {
        if (TextUtils.isEmpty(imageUrl)) {
            return null;
        }

        if (imageUrl.contains("?")) {
            return imageUrl;
        }
        return imageUrl + "?h=" + maxHeight;
    }
}
