package caribouapp.caribou.com.cariboucoffee.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by andressegurola on 10/11/17.
 */

public final class IntentUtil {

    private static final String TAG = IntentUtil.class.getSimpleName();

    private IntentUtil() {
    }

    public static void showDirectionsTo(Context context, LatLng destination) {

        Intent googleMapsIntent =
                new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + destination.latitude + "," + destination.longitude));

        if (startActivityIfPossible(context, Intent.createChooser(googleMapsIntent, "Select an application"))) {
            return;
        }

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:" + destination.latitude + "," + destination.longitude));
        startActivityIfPossible(context, intent);
    }

    public static void callPhoneNumber(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(StringUtils.format("tel:%s", phoneNumber)));
        startActivityIfPossible(context, intent);
    }

    public static boolean startActivityIfPossible(Context context, Intent intent) {
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Log.e(TAG, new LogErrorException("Unable to start Activity", e));
            return false;
        }
    }

    public static void openWebsite(Context context, String websiteURL, boolean noHistory) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String fullURL = websiteURL.startsWith("http://") || websiteURL.startsWith("https://") ? websiteURL : "http://" + websiteURL;
        intent.setData(Uri.parse(fullURL));
        if (noHistory) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        }
        startActivityIfPossible(context, intent);
    }
}
