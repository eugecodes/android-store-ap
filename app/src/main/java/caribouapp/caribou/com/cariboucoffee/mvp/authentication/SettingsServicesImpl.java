package caribouapp.caribou.com.cariboucoffee.mvp.authentication;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.urbanairship.UAirship;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.common.ReviewStatusEnum;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;
import caribouapp.caribou.com.cariboucoffee.util.Log;

/**
 * Created by jmsmuy on 11/2/17.
 */

public class SettingsServicesImpl implements SettingsServices {

    private static final String SNOOZE_DATETIME = "snooze_datetime";
    private static final String LAUNCHES_SINCE_LAST_REVIEW = "launches_since_last_review";
    private static final String REVIEW_STATUS = "review_status";
    private static final String PREVIOUS_ANDROID_VERSION_API = "previous_android_version";
    private static final String LAST_REVIEWED_VERSION = "last_reviewed_version";
    private static final String TAG = SettingsServicesImpl.class.getSimpleName();
    private final Application mContext;
    private final FirebaseRemoteConfig mConfig;

    public SettingsServicesImpl(Application application) {
        mContext = application;
        mConfig = FirebaseRemoteConfig.getInstance();
        mConfig.setDefaultsAsync(R.xml.default_firebase_config).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Props Loaded successfully");

            } else {
                Log.d(TAG, "Props Loaded failed");
            }
        });
    }

    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(AppConstants.APP_SHARED_PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public boolean getGoogleSignIn() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_GOOGLE_SIGN_IN_ENABLED);
    }

    @Override
    public boolean isPointsDisplay() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_POINTS_DISPLAY_ENABLED);
    }

    @Override
    public boolean isDisplayMenu() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_DISPLAY_MENU_ENABLED);
    }

    @Override
    public boolean isLocations() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_LOCATIONS_ENABLED);
    }

    @Override
    public boolean isEgift() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_EGIFT_ENABLED);
    }

    @Override
    public boolean isNews() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_NEWS_ENABLED);
    }

    @Override
    public boolean isFaqs() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_FAQS_ENABLED);
    }

    @Override
    public boolean isOrderAhead() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_ORDER_AHEAD_ENABLED);
    }

    @Override
    public boolean isOpenToCheckIn() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_OPEN_TO_CHECK_IN_ENABLED);
    }

    @Override
    public boolean isTrivia() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_TRIVIA_ENABLED);
    }

    @Override
    public DateTime getSnoozeDateTime() {
        String jsonDate = getSharedPreferences().getString(SNOOZE_DATETIME, null);
        return jsonDate == null ? null : GsonUtil.defaultGson().fromJson(jsonDate, DateTime.class);
    }

    @Override
    public void setSnoozeDatetime(DateTime snoozeTime) {
        getSharedPreferences().edit().putString(SNOOZE_DATETIME, GsonUtil.defaultGson().toJson(snoozeTime)).apply();
    }

    @Override
    public void clear() {
        getSharedPreferences().edit().clear().apply();
    }

    @Override
    public void incrementLaunchSinceLastReview() {
        int launches = getSharedPreferences().getInt(LAUNCHES_SINCE_LAST_REVIEW, 0);
        getSharedPreferences().edit().putInt(LAUNCHES_SINCE_LAST_REVIEW, launches + 1).apply();
    }

    @Override
    public void resetLaunchSinceLastReview() {
        getSharedPreferences().edit().putInt(LAUNCHES_SINCE_LAST_REVIEW, 0).apply();
    }

    @Override
    public String getLastReviewedVersion() {
        return getSharedPreferences().getString(LAST_REVIEWED_VERSION, null);
    }

    @Override
    public void setLastReviewedVersion(String version) {
        getSharedPreferences().edit().putString(LAST_REVIEWED_VERSION, version).apply();
    }

    @Override
    public String getPreviousAndroidVersion() {
        return getSharedPreferences().getString(PREVIOUS_ANDROID_VERSION_API, null);
    }

    @Override
    public int getLaunchCounter() {
        return getSharedPreferences().getInt(LAUNCHES_SINCE_LAST_REVIEW, 0);
    }

    @Override
    public ReviewStatusEnum getReviewStatus() {
        String reviewString = getSharedPreferences().getString(REVIEW_STATUS, null);
        return reviewString != null ? ReviewStatusEnum.valueOf(reviewString) : null;
    }

    @Override
    public void setReviewStatus(ReviewStatusEnum reviewStatus) {
        getSharedPreferences().edit().putString(REVIEW_STATUS, reviewStatus.name()).apply();
    }

    @Override
    public void setAndroidAppReviewVersion(String version) {
        getSharedPreferences().edit().putString(PREVIOUS_ANDROID_VERSION_API, version).apply();
    }

    @Override
    public boolean isReorder() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_REORDER_ENABLED);
    }

    @Override
    public String getMinimumSupportedVersion() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_MINIMUM_SUPPORTED_VERSION);
    }

    @Override
    public String getRecommendedVersion() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_RECOMMENDED_VERSION);
    }

    @Override
    public String getChooseLocationHeaderText() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_CHOOSE_LOCATION_HEADER_TEXT,
                mContext.getString(R.string.choose_location_header_text));
    }

    @Override
    public String getServerAndroidAppReviewVersion() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_APP_REVIEW_VERSION);
    }

    @Override
    public String getServerAppDormancy() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_APP_REVIEW_DORMANCY);
    }

    @Override
    public String getServerAppPrompt() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_APP_REVIEW_PROMPT);
    }

    @Override
    public boolean isShareAPerkEnabled() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_SHARE_A_PERK_ENABLED);
    }

    @Override
    public boolean isShowItemCustomizationModifiers() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_SHOW_ITEM_CUSTOMIZATION_MODIFIERS);
    }

    @Override
    public int getOrderAheadCheckStatusMaxAttempts() {
        return getConfigInteger(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_ORDER_AHEAD_CHECK_STATUS_MAX_ATTEMPTS, 8);
    }

    @Override
    public int getBulkPrepTimeInMins() {
        return getConfigInteger(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_BULK_PREP_TIME_IN_MINS, 20);
    }

    @Override
    public boolean isBulkOrderingEnabled() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_BULK_ORDERING_ENABLED);

    }

    @Override
    public double getBarcodeScreenBrightness() {
        return getConfigDouble(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_BARDCODE_SCREEN_BRIGHTNESS);
    }

    @Override
    public boolean isPickupTypeSelectionEnabled() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_SELECTION_ENABLED);
    }

    @Override
    public boolean isOrderAheadRewardsEnabled() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_ORDER_AHEAD_REWARDS_ENABLED);
    }

    @Override
    public boolean isOrderAheadRewardsDefaultTab() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_ORDER_AHEAD_REWARDS_DEFAULT_TAB);
    }

    @Override
    public boolean isTippingEnabled() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_TIPPING_ENABLED);
    }

    private List<String> getCommaSeparatedList(String propKey) {
        List<String> stringList = new ArrayList<>();
        String[] splitList = parseFireBaseCommaSeparated(propKey);
        for (String carType : splitList) {
            stringList.add(carType.trim());
        }
        return stringList;
    }

    private List<BigDecimal> getCommaSeparatedListBigDecimal(String propKey) {
        List<BigDecimal> bigDecimals = new ArrayList<>();
        String[] splitList = parseFireBaseCommaSeparated(propKey);
        for (String carType : splitList) {
            bigDecimals.add(new BigDecimal(carType.trim()));
        }
        return bigDecimals;
    }

    private String[] parseFireBaseCommaSeparated(String propKey) {
        String listString = getConfigString(mConfig, propKey);
        if (TextUtils.isEmpty(listString)) {
            return new String[0];
        }

        return listString.split("[,]");
    }

    @Override
    public List<String> getPickupCurbsideCarTypes() {
        return getCommaSeparatedList(AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_CAR_TYPES);
    }

    @Override
    public List<BigDecimal> getTippingMainOptions() {
        return getCommaSeparatedListBigDecimal(AppConstants.FIREBASE_REMOTE_CONFIG_KEY_TIPPING_MAIN_OPTION);
    }

    @Override
    public List<String> getPickupCurbsideCarColors() {
        return getCommaSeparatedList(AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_CAR_COLORS);
    }

    @Override
    public String getPickupCurbsideTipMessage() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_TIP_MESSAGE);
    }

    @Override
    public String getPickupDeliveryPrepMessage() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_DELIVERY_PREPARATION_MESSAGE);
    }

    @Override
    public int getOrderMinutesBeforeClosing() {
        return getConfigInteger(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_ORDER_MINUTES_BEFORE_CLOSING, 15);
    }

    @Override
    public int getPickupTimeFirstOptionOffset() {
        return getConfigInteger(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_TIME_FIRST_OPTION_OFFSET, 10);
    }

    @Override
    public String getPickupASAPMessage() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_ASAP_MESSAGE);
    }

    @Override
    public String getPickupScheduleMessage() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_SCHEDULE_MESSAGE);
    }

    @Override
    public String getMenuItemNotAvailableMessage() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_MENU_ITEM_NOT_AVAILABLE_MESSAGE);
    }

    @Override
    public boolean isImHereCurbSideFeatureEnabled() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_IM_HERE_ENABLED);
    }

    @Override
    public int getCurbSideImHereTeachingMessageMaxAttempts() {
        return getConfigInteger(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_IM_HERE_TEACHING_MAX_ATTEMPTS, 10);
    }

    @Override
    public String getCurbSideImHereTeachingMessage() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_IM_HERE_TEACHING_MESSAGE);
    }

    @Override
    public String getCurbSideImHereMessage() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_IM_HERE_MESSAGE);
    }

    @Override
    public int getCurbsideImHereMinutesToStart() {
        return getConfigInteger(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_IM_HERE_MINUTES_TO_START, 5);
    }

    @Override
    public int getCurbsideImHereMinutesToEnd() {
        return getConfigInteger(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_IM_HERE_MINUTES_TO_END, 15);
    }

    @Override
    public int getCurbsideCheckFinishedAttempts() {
        return getConfigInteger(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_CHECK_FINISHED_ATTEMPTS, 6);
    }

    @Override
    public int getCurbsideCheckFinishedDelay() {
        return getConfigInteger(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_CHECK_FINISHED_DELAY, 10);
    }

    @Override
    public String getCurbsideCarMakesOptions() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_CAR_MAKER_OPTIONS);
    }

    @Override
    public String getCurbSidePickupDescription() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_DESCRIPTION);
    }

    @Override
    public String getWalkInPickupDescription() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_WALK_IN_DESCRIPTION);
    }

    @Override
    public String getDriveThruPickupDescription() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_DRIVE_THRU_DESCRIPTION);
    }

    @Override
    public String getDeliveryPickupDescription() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PICKUP_DELIVERY_DESCRIPTION);
    }

    @Override
    public double getAddFundsCheckoutMinAmount() {
        return getConfigDouble(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_ADD_FUNDS_CHECKOUT_MIN_AMOUNT);
    }

    @Override
    public boolean isNationalOutageDialogEnabled() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_NATIONAL_OUTAGE_ENABLED);
    }

    @Override
    public String getNationalOutageDialogTitle() {
        String title = getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_NATIONAL_OUTAGE_TITLE);
        if (title != null && !title.isEmpty()) {
            return title;
        } else {
            return mContext.getString(R.string.national_outage_title);
        }
    }

    @Override
    public String getNationalOutageDialogMessage() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_NATIONAL_OUTAGE_MESSAGE,
                mContext.getString(R.string.national_outage_message));
    }

    @Override
    public String getZeroTotalOrderErrorMessage() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_ZERO_TOTAL_ORDER_ERROR_MESSAGE,
                mContext.getString(R.string.zero_cost_order_message));
    }

    @Override
    public String getTokenExpiredMessageTitle() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_TOKEN_EXPIRED_MESSAGE_TITLE,
                mContext.getString(R.string.you_have_been_logout));
    }

    @Override
    public String getTokenExpiredMessage() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_TOKEN_EXPIRED_MESSAGE,
                mContext.getString(R.string.token_expired_message));
    }

    @Override
    public String getDeleteAccountTitle() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_DELETE_ACCOUNT_TITLE,
                "");
    }

    @Override
    public String getAccountBalanceTitle() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_ACCOUNT_BALANCE_TITLE,
                "");
    }

    @Override
    public String getConfirmationTitle() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_DELETE_ACCOUNT_CONFIRMATION_TITLE,
                "");
    }

    @Override
    public String getDeleteAccountMessage() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_DELETE_ACCOUNT_MESSAGE,
                "");
    }

    @Override
    public String getAccountBalanceMessage() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_ACCOUNT_BALANCE_MESSAGE,
                "");
    }

    @Override
    public String getConfirmationMessage() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_DELETE_ACCOUNT_CONFIRMATION_MESSAGE,
                "");
    }

    @Override
    public boolean isDeleteAccountFeatureEnabled() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_DELETE_ACCOUNT_FLAG);
    }

    @Override
    public String getNoahContactUsURL() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_NOAHS_CONTACTUS_URL,
                "");
    }

    @Override
    public String getEBBContactUsURL() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_EBB_CONTACTUS_URL,
                "");
    }

    @Override
    public String getBRUEGGERSContactUsURL() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_BRUEGGERS_CONTACTUS_URL,
                "");
    }

    @Override
    public boolean isGuestCheckoutEnabledFromDashboard() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_ENABLED_GUEST_CHECKOUT_FROM_DASHBOARD);
    }

    @Override
    public boolean isContinueAsGuestCheckEnabled() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_ENABLED_CONTINUE_AS_GUEST_BUTTON);
    }

    @Override
    public boolean isGuestNotificationEnabled() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_GUEST_NOTIFICATION_ENABLED);
    }

    @Override
    public String getLetsGetStartedMessage() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_LETS_GET_STARTED,
                mContext.getString(R.string.lets_get_started));
    }

    @Override
    public Boolean showAllBillingInfoFieldsOnUI() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_SHOW_ALL_BILLING_INFO_ON_UI);
    }

    @Override
    public String getPGCommonErrorMsg() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PG_COMMON_ERROR,
                mContext.getString(R.string.pg_common_error));
    }

    @Override
    public String getCardDeclinedMsg() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_CARD_DECLINED,
                mContext.getString(R.string.card_declined));
    }

    @Override
    public String getCardValidationFailureMsg() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_CARD_VALIDATION_FAILURE,
                mContext.getString(R.string.card_validation_failure));
    }

    @Override
    public String getPriceOverLimitMsg() {
        return getConfigString(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_PRICE_OVER_LIMIT,
                mContext.getString(R.string.price_over_limit));
    }


    @Override
    public int getNationalOutageDialogMaxAttempts() {
        return 1;
    }

    private Boolean getConfigBoolean(FirebaseRemoteConfig config, String configKey) {
        Boolean configValue = config.getBoolean(configKey);
        Log.d(TAG, "Config Prop: " + configKey + " -> " + configValue);
        return configValue;
    }

    private Integer getConfigInteger(FirebaseRemoteConfig config, String configKey, int defaultValue) {
        String sConfigValue = config.getString(configKey);

        Integer configValue = null;
        try {
            configValue = Integer.parseInt(sConfigValue);
        } catch (RuntimeException e) {
            // No op. We ignore values that are not integers.
        }
        Log.d(TAG, "Config Prop: " + configKey + " -> " + configValue);
        return configValue == null ? defaultValue : configValue;
    }

    private String getConfigString(FirebaseRemoteConfig config, String configKey) {
        return getConfigString(config, configKey, null);
    }

    private String getConfigString(FirebaseRemoteConfig config, String configKey, String defaultString) {
        String configValue = config.getString(configKey);
        Log.d(TAG, "Config Prop: " + configKey + " -> " + configValue);
        String message = formmatNewLineMessage(configValue);
        if (message != null && !message.isEmpty()) {
            return message;
        } else {
            return defaultString;
        }
    }

    private String formmatNewLineMessage(String stringMessage) {
        return stringMessage.replace("\\n", "\n");
    }

    private double getConfigDouble(FirebaseRemoteConfig config, String configKey) {
        double configValue = config.getDouble(configKey);
        Log.d(TAG, "Config Prop: " + configKey + " -> " + configValue);
        return configValue;
    }

    @Override
    public String getDeviceId() {
        return UAirship.shared().getChannel().getId();
    }

    @Override
    public int getTimeoutDefault() {
        return getConfigInteger(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_TIMEOUT_DEFAULT, 16);
    }

    @Override
    public int getTimeoutAddFunds() {
        return getConfigInteger(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_TIMEOUT_ADD_FUNDS, getTimeoutDefault());
    }

    @Override
    public boolean isGooglePayEnabled() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_ENABLE_GOOGLE_PAY);
    }

    @Override
    public boolean isPayPalEnabled() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_ENABLE_PAYPAL);
    }

    @Override
    public boolean isVenmoEnabled() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_ENABLE_VENMO);
    }

    @Override
    public boolean isPaymentSelectionScreenEnabled() {
        return getConfigBoolean(mConfig, AppConstants.FIREBASE_REMOTE_CONFIG_KEY_ENABLE_PAYMENT_SCREEN);
    }
}
