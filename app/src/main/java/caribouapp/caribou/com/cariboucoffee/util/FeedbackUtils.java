package caribouapp.caribou.com.cariboucoffee.util;

import android.text.TextUtils;

import caribouapp.caribou.com.cariboucoffee.BuildConfig;
import caribouapp.caribou.com.cariboucoffee.common.ReviewStatusEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;

/**
 * Created by jmsmuy on 2/26/18.
 */

public final class FeedbackUtils {

    private FeedbackUtils() {
    }

    public static boolean shouldShowPopup(SettingsServices settingsServices) {

        boolean isServerAnswerValid = checkServerAnswer(settingsServices);

        if (!isServerAnswerValid) {
            return false;
        }

        String currentAppVersion = BuildConfig.VERSION_NAME;

        settingsServices.incrementLaunchSinceLastReview();
        resetIfVersionChange(settingsServices, currentAppVersion);

        String lastReviewedVersion = settingsServices.getLastReviewedVersion();
        String currentAndroidVersionAPI = settingsServices.getServerAndroidAppReviewVersion();
        String previousAndroidVersionAPI = settingsServices.getPreviousAndroidVersion();

        int launchCounter = settingsServices.getLaunchCounter();
        ReviewStatusEnum reviewResult = settingsServices.getReviewStatus();
        int appDormancy = Integer.valueOf(settingsServices.getServerAppDormancy());
        int appPrompt = Integer.valueOf(settingsServices.getServerAppPrompt());

        boolean result = wrappedShouldShowPopup(lastReviewedVersion,
                previousAndroidVersionAPI, currentAndroidVersionAPI, launchCounter,
                reviewResult, appDormancy, appPrompt, currentAppVersion);

        // Save the current versions as old versions (for next checks)
        settingsServices.setAndroidAppReviewVersion(currentAndroidVersionAPI);
        return result;
    }

    private static boolean checkServerAnswer(SettingsServices settingsServices) {
        return !TextUtils.isEmpty(settingsServices.getServerAndroidAppReviewVersion())
                && !TextUtils.isEmpty(settingsServices.getServerAppPrompt())
                && !TextUtils.isEmpty(settingsServices.getServerAppDormancy());
    }

    public static boolean wrappedShouldShowPopup(String lastReviewedVersion, String previousAndroidVersion,
                                                 String currentAndroidVersion, int launchesSinceLastReview,
                                                 ReviewStatusEnum reviewResult, int appDormancy, int appPrompt, String installedVersion) {
        return (lastReviewedVersion != null
                && VersionUtil.compareVersions(currentAndroidVersion, installedVersion) <= 0)
                && (previousAndroidVersion.equals(currentAndroidVersion)
                && appPrompt <= launchesSinceLastReview && reviewResult == ReviewStatusEnum.NOT_REVIEWED
                || appDormancy <= launchesSinceLastReview && reviewResult == ReviewStatusEnum.ASK_ME_LATER
                || !previousAndroidVersion.equals(currentAndroidVersion)
                && (ReviewStatusEnum.DONT_ASK_ME_AGAIN.equals(reviewResult) || ReviewStatusEnum.REVIEWED.equals(reviewResult)));
    }

    private static void resetLocalVars(SettingsServices settingsServices) {
        settingsServices.setAndroidAppReviewVersion(settingsServices.getServerAndroidAppReviewVersion());
        if (settingsServices.getLastReviewedVersion() == null) {
            settingsServices.setLastReviewedVersion("0");
        }
        settingsServices.setReviewStatus(ReviewStatusEnum.NOT_REVIEWED);
        settingsServices.resetLaunchSinceLastReview();
    }

    private static void resetIfVersionChange(SettingsServices settingsServices, String currentAppVersion) {
        if (!settingsServices.getServerAndroidAppReviewVersion().equals(settingsServices.getPreviousAndroidVersion())
                || VersionUtil.compareVersions(currentAppVersion, settingsServices.getServerAndroidAppReviewVersion()) < 0) {
            resetLocalVars(settingsServices);
        }
    }

    public static void updateStatus(ReviewStatusEnum statusEnum, SettingsServices settingsServices) {
        settingsServices.setReviewStatus(statusEnum);
        if (ReviewStatusEnum.ASK_ME_LATER.equals(statusEnum)) {
            settingsServices.resetLaunchSinceLastReview();
        } else if (ReviewStatusEnum.REVIEWED.equals(statusEnum)) {
            settingsServices.setLastReviewedVersion(settingsServices.getPreviousAndroidVersion());
            // We can use the "previous" since it has already been updated
        }
    }

    public static void signOut(SettingsServices settings) {
        settings.setLastReviewedVersion(null);
        resetLocalVars(settings);
    }
}
