package caribouapp.caribou.com.cariboucoffee;


import java.util.Locale;

import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;

/**
 * Created by andressegurola on 10/13/17.
 */

public final class AppConstants {

    private AppConstants() {
    }

    public static final int SNOOZE_HOURS = 24; // Amount of hours the snooze is valid for

    public static final String CHECK_CAPTIVE_PORTAL_URL = "http://clients3.google.com/";
    public static final String ACTION_PASSWORD_RESET = "action_password_reset";

    public static final String CMS_API_PATH = BuildConfig.AZURE_API_PATH + BuildConfig.CMS_PATH;
    public static final String CMS_ORDER_API_PATH = BuildConfig.AZURE_API_PATH + BuildConfig.CMS_ORDER_PATH;
    public static final String AMS_V2_API_PATH = BuildConfig.AZURE_API_PATH + "/account/v2/";
    public static final String YEXT_API_PATH = BuildConfig.AZURE_API_PATH + "/location/v1/";

    public static final String SVMS_V2_API_PATH = BuildConfig.AZURE_API_PATH + "/storedValue/v2/";
    public static final String TRIVIA_V1_API_PATH = BuildConfig.AZURE_API_PATH + "/trivia/v1/";
    public static final String PAY_GATE_V1_API_PATH = BuildConfig.PAY_GATE_API_PATH + "/pay/v1/";
    public static final String FISERV_V1_API_PATH = BuildConfig.FISERV_PAY_API_PATH + "/ucom/v1/";

    public static final String PUBLIC_KEYS_PATH = "/assets/keys/";
    public static final String PAY_GATE_PUBLIC_KEY_PATH = PUBLIC_KEYS_PATH + BuildConfig.PAY_GATE_PUBLIC_KEY_NAME;
    public static final String PAY_GATE_PUBLIC_KEY_ALGORITHM = "RSA/None/OAEPWithSHA256AndMGF1Padding";

    public static final String ZIP_CODE_REGEX = "(^\\d{5}$)|(^\\d{5}-\\d{4}$)";

    public static final int REQUEST_CODE_LOCATION_PERMISSION = 10;
    public static final int REQUEST_CODE_CHECK_PLAY_SERVICES = 11;
    public static final int REQUEST_CODE_LOCATIONS_LIST_SCREEN = 12;
    public static final int REQUEST_CODE_CARIBOU_WEBSITE = 13;
    public static final int REQUEST_CODE_ADD_FUNDS = 14;
    public static final int REQUEST_CODE_ADD_PAYMENT = 15;
    public static final int REQUEST_CODE_AUTO_RELOAD = 16;
    public static final int REQUEST_CODE_GOOGLE_SIGN_IN = 17;
    public static final int REQUEST_CODE_SIGN_UP = 18;
    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 19;
    public static final int REQUEST_CODE_EDIT_PROFILE = 21;
    public static final int REQUEST_CODE_CHANGE_PASSWORD = 22;
    public static final int REQUEST_CODE_FEEDBACK = 23;
    public static final int REQUEST_CODE_NEW_ORDER_LOCATION_DETAILS = 24;
    public static final int REQUEST_CODE_SELECT_OPTION_AND_QUANTITY = 25;
    public static final int REQUEST_CODE_SELECT_MULTIPLE_OPTIONS_AND_QUANTITIES = 26;
    public static final int REQUEST_CODE_ITEM_CUSTOMIZATION = 27;
    public static final int REQUEST_CODE_LOCATION_SERVICES = 28;
    public static final int REQUEST_CODE_ADD_CARD = 29;
    public static final int REQUEST_CODE_SIGN_IN = 30;
    public static final int REQUEST_CODE_UPDATE_PICKUP_TYPE = 31;

    public static final int RESULT_CODE_BACK_TO_DASHBOARD = 10;
    public static final int RESULT_CODE_GO_TO_CART = 11;

    public static final int RESULT_CODE_ONE_TIME_ADD_FUNDS = 12;
    public static final int RESULT_CODE_ADD_FUNDS_FROM_FILE = 13;
    public static final int RESULT_CODE_BOUNCE_REQUIRED = 16;

    public static final String ENVIRONMENT_DEVELOPMENT = "dev";
    public static final String ENVIRONMENT_QA = "qa";
    public static final String ENVIRONMENT_PRODUCTION = "prod";

    public static final int AMOUNT_DIGITS_TO_SHOW = 4;
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_INTENT = "intent";
    public static final String EXTRA_NEW_BALANCE = "newBalance";
    public static final String EXTRA_AUTO_RELOAD_ADD_AMOUNT = "autoReloadAddAmount";
    public static final String EXTRA_AUTO_THRESHOLD = "autoReloadThreshold";
    public static final String EXTRA_ADD_FUNDS_AMOUNT = "addFundsAmount";
    public static final String EXTRA_FUNDS_NEEDED_TO_ADD = "fundNeededToAdd";
    public static final String EXTRA_ADD_FUNDS_FROM_CHECKOUT = "addFundsFromCheckout";
    public static final String EXTRA_CYBERSOURCE_TOKEN = "add_payment_cybersource_token";

    public static final String EXTRA_ENROLLED_VIA_GOOGLE = "extra_enrolled_via_google";
    public static final String EXTRA_SET_EMAIL = "extra_set_mail";
    public static final String EXTRA_IS_CHANGE_PASSWORD = "change_password";
    public static final String EXTRA_SET_TELEPHONE = "extra_set_telephone";
    public static final String EXTRA_CHECKIN_REWARD_SELECTED = "checkin_reward_selected";

    public static final String EXTRA_LOCATIONS_SEARCH_MODEL = "locationsModel";

    public static final String EXTRA_FEEDBACK_SUCCESS = "extra_feedback_success";

    public static final String EXTRA_SET_BIRTHDAY = "extra_user_has_birthday";

    public static final String EXTRA_IS_EDIT_PERSONAL_INFORMATION = "extra_is_edit_personal_information";
    public static final String EXTRA_SIGN_UP_REWARDS_PROGRAM = "extra_is_update_loyalty_program";
    public static final String PERSONAL_INFORMATION_CONFIGURATOR = "extra_personal_information_configurator";

    public static final String EXTRA_PERSONAL_INFORMATION_MODEL = "extra_personal_information_model";

    public static final String EXTRA_MENU_PRODUCT = "menu_product_extra";
    public static final String EXTRA_ORDER_ITEM = "order_item";

    public static final String EXTRA_MODIFIER_GROUP = "extraModifierGroup";
    public static final String EXTRA_ITEM_MODIFIER = "extraItemModifier";
    public static final String EXTRA_ITEM_OPTION = "extraItemOption";

    public static final String EXTRA_MULTIPLE_MODIFIER_OPTIONS = "extraMultipleModifierOptions";
    public static final String EXTRA_MENU_ORDER_FLOW = "menu_order_flow";
    public static final String EXTRA_ORIGIN = "menu_passthrough_to_cart";
    public static final String EXTRA_PENDING_INTENT_NEXT_SCREEN = "next_screen_intent";
    public static final String EXTRA_IS_FROM_CHECKOUT = "is_from_checkout";
    public static final String EXTRA_MAIN_BUTTON_TEXT = "main_button_text";
    public static final String EXTRA_IS_FROM_SIGN_IN = "is_from_sign_in";

    public static final String APP_SHARED_PREFS = "app_shared_prefs";
    public static final String ORDER_SHARED_PREFS = "order_shared_prefs";
    public static final String USER_SHARED_PREFS = "user_shared_prefs";
    public static final String USER_SHARED_PREFS_API28 = "user_shared_prefs_api_28";
    public static final String RUN_ONCE_KEY_MIGRATE_USER_PREFERENCES = "makeCopyOfSharePreferences";

    public static final String VALID_EMAIL_ADDRESS_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";

    /**
     * Near by threshold in miles
     */
    public static final double LOCATIONS_NEAR_BY = 0.02;
    public static final int METER_TO_MILES_DIVIDER = 1609;

    public static final double LOCATIONS_DEFAULT_SEARCH_RADIUS_IN_MILES = 30;
    public static final double LOCATIONS_STATE_WIDE_SEARCH_RADIUS_IN_MILES = 400;
    public static final int LOCATIONS_PAGE_SIZE = 50;

    public static final SizeEnum DEFAULT_DRINK_SIZE = SizeEnum.MEDIUM;
    // TODO we should ask API Guys for all the possible return labels for the CC
    public static final String VISA_API_NAMING = "visa";
    public static final String MASTER_API_NAMING = "mast";
    public static final String AMEX_API_NAMING = "amex";
    public static final String DISCOVER_API_NAMING = "disc";
    public static final int DEFAULT_DIGITS_AFTER_COMMA = 2;
    public static final String FILTER_DIALOG_FRAGMENT = "filter_dialog";
    public static final String PICK_UP_TIME_DIALOG_FRAGMENT = "pick_up_time_dialog";
    public static final String SELECT_CUSTOM_TIP_DIALOG_FRAGMENT = "select_tip_dialog";
    public static final String LETS_GET_STARTED_DIALOG_FRAGMENT = "lets_get_started_dialog";

    public static final float DEFAULT_MAP_ZOOM = 13f;
    public static final float DEFAULT_NO_COORDINATES_MAP_ZOOM = 3.2f;

    public static final Integer NO_DIGITS_AFTER_COMMA = 0;
    public static final int DEFAULT_TELEPHONE_LENGHT = 10;
    public static final String CURRENCY_USD = "USD";

    public static final String CC_MASKING_SYMBOL = "X";
    public static final String US_COUNTRY_ABREV = "US";
    public static final String GOOGLE_PROVIDER = "Google";

    public static final double[] DEFAULT_CARIBOU_LOCATION = new double[]{45.043726, -93.330331};
    public static final double[] DEFAULT_UNITE_STATES_LOCATION = new double[]{36.0416255, -94.3410642};

    public static final int[] TIP_OPTION_PERCENTAGE = new int[]{10, 15, 20, 25};

    public static final String OMS_SIZE_NAME_SMALL = "Small";
    public static final String OMS_SIZE_NAME_MEDIUM = "Medium";
    public static final String OMS_SIZE_NAME_LARGE = "Large";
    public static final String OMS_NAME_OF_NOT_GUARANTEED_PRODUCTS = "Roast Preference";
    public static final String OMS_SIZE_NAME_EXTRA_LARGE = "Extra";

    public static final int ORDER_AHEAD_MAX_ITEM_QUANTITY = 50;

    public static final Locale DEFAULT_LOCALE = Locale.US; // I prefered to do this (@juanma),
    // since we might need to change it if Caribou buys a store in Mexico or Turkey we might
    // want this handy (Locale.getDefaultLocale())

    public static final long ORDER_AHEAD_CHECK_STATUS_WAIT_IN_MILISECONDS = 2000;

    public static final int AMOUNT_DAYS_RECENT_ORDERS = 60;

    public static final int FIREBASE_REMOTE_CONFIG_CACHE_TIME_IN_SECONDS = 1800;
    public static final String FIREBASE_REMOTE_CONFIG_KEY_GOOGLE_SIGN_IN_ENABLED = "GoogleSignInEnabled";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_POINTS_DISPLAY_ENABLED = "PointsDisplayEnabled";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_CHOOSE_LOCATION_HEADER_TEXT = "ChooseLocationHeaderText";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_REORDER_ENABLED = "QuickReorderEnabled";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_DISPLAY_MENU_ENABLED = "DisplayMenuEnabled";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_LOCATIONS_ENABLED = "LocationsEnabled";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_EGIFT_ENABLED = "EgiftEnabled";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_FAQS_ENABLED = "FaqsEnabled";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_ORDER_AHEAD_ENABLED = "OrderAheadEnabled";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_NEWS_ENABLED = "NewsEnabled";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_OPEN_TO_CHECK_IN_ENABLED = "OpenToCheckInEnabled";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_TRIVIA_ENABLED = "TriviaEnabled";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_APP_REVIEW_VERSION = "AppReviewVersion";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_APP_REVIEW_DORMANCY = "AppReviewDormancy";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_MENU_ITEM_NOT_AVAILABLE_MESSAGE = "MenuItemNotAvailableMessage";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_APP_REVIEW_PROMPT = "AppReviewPrompt";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_MINIMUM_SUPPORTED_VERSION = "MinimumSupportedVersion";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_RECOMMENDED_VERSION = "RecommendedVersion";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_SHARE_A_PERK_ENABLED = "ShareAPerkEnabled";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_SHOW_ITEM_CUSTOMIZATION_MODIFIERS = "ShowItemCustomizationModifiers";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_ORDER_AHEAD_CHECK_STATUS_MAX_ATTEMPTS = "OrderAheadCheckStatusMaxAttempts";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_BULK_PREP_TIME_IN_MINS = "BulkPrepTimeInMins";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_BULK_ORDERING_ENABLED = "BulkOrderingEnabled";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_BARDCODE_SCREEN_BRIGHTNESS = "BarcodeScreenBrightness";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_SELECTION_ENABLED = "PickupTypeSelectionEnabled";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_ORDER_AHEAD_REWARDS_ENABLED = "OrderAheadRewardsEnabled";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_ORDER_AHEAD_REWARDS_DEFAULT_TAB = "OrderAheadRewardsDefaultTab";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_TIPPING_ENABLED = "TippingEnabled";

    public static final String FIREBASE_REMOTE_CONFIG_KEY_TIPPING_MAIN_OPTION = "TippingMainOptions";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_ORDER_MINUTES_BEFORE_CLOSING = "OrderMinutesBeforeClosing";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_DELIVERY_PREPARATION_MESSAGE = "PickupDeliveryTimeMessage";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_TIME_FIRST_OPTION_OFFSET = "PickupTimeFirstSlotOffset";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_ASAP_MESSAGE = "PickupAsapTimeMessage";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_SCHEDULE_MESSAGE = "PickupScheduledTimeMessage";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_ADD_FUNDS_CHECKOUT_MIN_AMOUNT = "AddFundsCheckoutMinAmount";

    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_CAR_TYPES = "PickupCurbsideCarTypes";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_CAR_COLORS = "PickupCurbsideCarColors";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_CAR_MAKER_OPTIONS = "PickupCurbsideCarMakes";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_TIP_MESSAGE = "PickupCurbsideTipMessage";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_IM_HERE_ENABLED = "PickupCurbsideImHereFeature";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_IM_HERE_MESSAGE = "PickupCurbsideImHereMessage";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_IM_HERE_TEACHING_MAX_ATTEMPTS = "PickupCurbsideImHereTeachingMaxAttempts";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_IM_HERE_TEACHING_MESSAGE = "PickupCurbsideImHereTeachingMessage";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_IM_HERE_MINUTES_TO_START = "PickupCurbsideImHereStart";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_IM_HERE_MINUTES_TO_END = "PickupCurbsideImHereEnd";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_CHECK_FINISHED_ATTEMPTS = "PickupCurbsideCheckFinishedAttempts";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_CHECK_FINISHED_DELAY = "PickupCurbsideCheckFinishedDelay";

    public static final String FIREBASE_REMOTE_CONFIG_KEY_NATIONAL_OUTAGE_ENABLED = "NationalOutageDialogEnabled";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_NATIONAL_OUTAGE_TITLE = "NationalOutageDialogTitle";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_NATIONAL_OUTAGE_MESSAGE = "NationalOutageDialogMessage";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_ZERO_TOTAL_ORDER_ERROR_MESSAGE = "CheckoutAttemptZeroTotalOrderErrorMessage";

    public static final String CUSTOM_METRIC_ID_POSITOUCH_ORDER_BOUNCE = "posi_order_bounce";
    public static final String CUSTOM_METRIC_ID_POSITOUCH_ORDER_PLACE = "posi_order_place";
    public static final String CUSTOM_METRIC_ID_NCR_ORDER_BOUNCE = "ncr_order_bounce";
    public static final String CUSTOM_METRIC_ID_NCR_ORDER_PLACE = "ncr_order_place";

    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_CURBSIDE_DESCRIPTION = "PickupCurbsideDescription";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_WALK_IN_DESCRIPTION = "PickupWalkInDescription";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_DRIVE_THRU_DESCRIPTION = "PickupDriveThruDescription";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PICKUP_DELIVERY_DESCRIPTION = "PickupDeliveryDescription";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_TOKEN_EXPIRED_MESSAGE = "TokenExpiredMessage";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_TOKEN_EXPIRED_MESSAGE_TITLE = "TokenExpiredMessageTitle";

    public static final String FIREBASE_REMOTE_CONFIG_KEY_DELETE_ACCOUNT_FLAG = "EnableDeleteAccount";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_DELETE_ACCOUNT_TITLE = "DeleteAccountTitle";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_ACCOUNT_BALANCE_TITLE = "AccountBalanceTitle";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_DELETE_ACCOUNT_CONFIRMATION_TITLE = "ConfirmationTitle";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_DELETE_ACCOUNT_MESSAGE = "DeleteAccountMessage";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_ACCOUNT_BALANCE_MESSAGE = "AccountBalanceMessage";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_DELETE_ACCOUNT_CONFIRMATION_MESSAGE = "ConfirmationMessage";

    public static final String FIREBASE_REMOTE_CONFIG_KEY_NOAHS_CONTACTUS_URL = "NOAHSContactUsURL";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_EBB_CONTACTUS_URL = "EEBContactUsURL";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_BRUEGGERS_CONTACTUS_URL = "BRUEGGERSContactUsURL";

    public static final String FIREBASE_REMOTE_CONFIG_KEY_ENABLED_GUEST_CHECKOUT_FROM_DASHBOARD = "EnableGuestFlowFromDashboard";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_ENABLED_CONTINUE_AS_GUEST_BUTTON = "EnableContinueAsGuestButton";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_LETS_GET_STARTED = "LetsGetStarted";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_SHOW_ALL_BILLING_INFO_ON_UI = "ShowAllBillingFieldsOnMobileUI";

    public static final String FIREBASE_REMOTE_CONFIG_KEY_PG_COMMON_ERROR = "CommonErrorMessageForPG";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_CARD_DECLINED = "ErrorCardDeclined";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_CARD_VALIDATION_FAILURE = "ErrorCardValidationFailure";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_PRICE_OVER_LIMIT = "ErrorPriceOverLimit";

    public static final String FIREBASE_REMOTE_CONFIG_KEY_ENABLE_GOOGLE_PAY = "EnableGCGooglePay";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_ENABLE_PAYPAL = "EnableGCPaypal";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_ENABLE_VENMO = "EnableGCVenmo";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_ENABLE_PAYMENT_SCREEN = "EnableGuestCheckoutPaymentSelectionScreen";

    public static final String FIREBASE_REMOTE_CONFIG_KEY_TIMEOUT_DEFAULT = "MobileTimeoutDefault";
    public static final String FIREBASE_REMOTE_CONFIG_KEY_TIMEOUT_ADD_FUNDS = "MobileTimeoutAddFunds";

    public static final String FIREBASE_REMOTE_CONFIG_KEY_GUEST_NOTIFICATION_ENABLED = "GuestNotificationEnabled";

    public static final int METERS_IN_A_MILE = 1600;

    public static final String FILE_PROVIDER_ID = BuildConfig.APPLICATION_ID + ".provider";

    public static final String AIRSHIP_SCREEN_NAME_MENU = "MenuScreen";

    public static final int PERSONAL_INFORMATION_INVALID_PARAMS = 1;

    public static final String BROADCAST_INTENT_ACTION_NEW_PRESELECTED_REWARD_SET = "action_new_preselected_reward_set";

    public static final int HTTP_TOO_MANY_ATTEMPTS = 429;

    public static final String ASAP = "ASAP";
    public static final String GPAY = "GOOGLE_PAY";
    public static final String PAYPAL = "PAYPAL";
    public static final String VENMO = "VENMO";
    public static final String CREDIT = "CREDIT";

}
