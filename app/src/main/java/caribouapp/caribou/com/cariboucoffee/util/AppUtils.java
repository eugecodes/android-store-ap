package caribouapp.caribou.com.cariboucoffee.util;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import java.util.Locale;

import caribouapp.caribou.com.cariboucoffee.BuildConfig;
import caribouapp.caribou.com.cariboucoffee.common.BrandEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SignInActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.MainActivity;

import static caribouapp.caribou.com.cariboucoffee.AppConstants.ENVIRONMENT_DEVELOPMENT;
import static caribouapp.caribou.com.cariboucoffee.AppConstants.ENVIRONMENT_PRODUCTION;
import static caribouapp.caribou.com.cariboucoffee.AppConstants.ENVIRONMENT_QA;


public final class AppUtils {

    private AppUtils() {
    }

    @SuppressLint("DefaultLocale")
    public static String getAppVersionText() {
        if (isProductionBuild()) {
            return StringUtils.format("%s (%d)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
        } else {
            return StringUtils.format("%s (%d) - %s", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, BuildConfig.FLAVOR);
        }
    }

    public static void restartAppAtActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(cn);
        context.startActivity(mainIntent);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    public static void runOnce(Context context, String key, Runnable runnable) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.getBoolean(key, false)) {
            runnable.run();

            // mark runnable as already executed
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(key, true);
            editor.apply();
        }
    }

    /**
     * This method calls the Play Store app (if installed), if Play Store is not detected we just launch
     * a url to the online playstore
     */
    public static void goToPlayStore(Context context) {
        final String appPackageName = isProductionBuild()
                ? BuildConfig.APPLICATION_ID : BuildConfig.APPLICATION_ID.substring(0, BuildConfig.APPLICATION_ID.lastIndexOf("."));
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static String getAppEnvironment() {
        return AppUtils.isDevBuild() ? ENVIRONMENT_DEVELOPMENT : AppUtils.isQaBuild() || AppUtils.isStagingBuild() ? ENVIRONMENT_QA
                : AppUtils.isProductionBuild() ? ENVIRONMENT_PRODUCTION : null;
    }

    public static boolean isProductionBuild() {
        return BuildConfig.FLAVOR.toLowerCase(Locale.US).contains("productionserver");
    }

    public static boolean isStagingBuild() {
        return BuildConfig.FLAVOR.toLowerCase(Locale.US).contains("stagingserver");
    }

    public static boolean isQaBuild() {
        return BuildConfig.FLAVOR.toLowerCase(Locale.US).contains("qaserver");
    }

    public static boolean isDevBuild() {
        return BuildConfig.FLAVOR.toLowerCase(Locale.US).contains("devserver");
    }


    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    public static BrandEnum getBrand() {
        return BrandEnum.getFromBrand(BuildConfig.FLAVOR_brand);
    }

    public static String getBrandYextValue(BrandEnum brandEnum) {
        switch (brandEnum) {
            case BRU_BRAND:
                return "BRUEGGER'S_BAGELS";
            case CBOU_BRAND:
                return "CARIBOU_COFFEE";
            case EBB_BRAND:
                return "EINSTEINBROSBAGELS";
            case NNYB_BRAND:
                return "NOAH'S_BAGELS";
            case POLAR_BRAND:
                // NOTE same as caribou
                return "CARIBOU_COFFEE";
            default:
                throw new IllegalArgumentException("Unknown brand " + brandEnum.name());
        }
    }

    public static void restartAppSignInActivity(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(cn);
        context.startActivity(mainIntent);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

}
