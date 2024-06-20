package caribouapp.caribou.com.cariboucoffee.util;

import android.graphics.Typeface;
import android.view.View;

/**
 * Created by jmsmuy on 9/28/17.
 */
public final class FontUtils {

    private FontUtils() {
    }

    public static Typeface getTypeFont(View view, String typefaceName) {
        return Typeface.createFromAsset(view.getContext().getAssets(), typefaceName);
    }

}
