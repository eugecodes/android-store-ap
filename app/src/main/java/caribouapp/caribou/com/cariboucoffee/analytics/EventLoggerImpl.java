package caribouapp.caribou.com.cariboucoffee.analytics;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.urbanairship.analytics.CustomEvent;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategory;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;

/**
 * Created by jmsmuy on 04/04/18.
 */

public class EventLoggerImpl implements EventLogger {

    private static final String TAG = EventLoggerImpl.class.getSimpleName();

    private static final String USER_PROPERTY_ENVIRONMENT = "Environment";
    private static final String USER_PROPERTY_BRAND = "Brand";

    private static final String EVENT_NAME_MENU_SEARCH = "menu_search";
    private static final String EVENT_NAME_MENU_FILTER_LEVEL_1 = "menu_filter_1";
    private static final String EVENT_NAME_MENU_FILTER_LEVEL_2 = "menu_filter_2";
    private static final String EVENT_NAME_MENU_FILTER_LEVEL_3 = "menu_filter_3";
    private static final String EVENT_NAME_ADDED_PAYMENT_INFO = "added_payment_info";
    private static final String EVENT_NAME_OPENED_CHECK_IN = "opened_checkin";

    private static final String EVENT_NAME_ENROLL_ERROR = "enroll_error";
    private static final String EVENT_PARAM_ENROLL_ERROR_MESSAGE = "error_message";

    private static final String EVENT_NAME_ENROLL_PREFIX = "enroll";
    private static final String EVENT_NAME_ENROLL_STARTED = EVENT_NAME_ENROLL_PREFIX + "_started";
    private static final String EVENT_NAME_ENROLL_FINISHED = EVENT_NAME_ENROLL_PREFIX + "_finished";

    private static final String EVENT_NAME_SIGN_IN_PREFIX = "sign_in";

    private static final String EVENT_PARAM_SCREEN = "screen";
    private static final String EVENT_PARAM_EDITED = "edited";
    private static final String EVENT_PARAM_ASAP = "ASAP";

    private static final String EVENT_NAME_LOCATION_PREFIX = "location";
    private static final String EVENT_NAME_LOCATION_SEARCH = EVENT_NAME_LOCATION_PREFIX + "_search";
    private static final String EVENT_NAME_LOCATION_FILTER_ORDER_AHEAD = EVENT_NAME_LOCATION_PREFIX + "_filter_orderahead";

    private static final String EVENT_NAME_ORDER_PREFIX = "order";
    private static final String EVENT_NAME_ORDER_STARTED = EVENT_NAME_ORDER_PREFIX + "_started";
    private static final String EVENT_NAME_ORDER_COMPLETED = EVENT_NAME_ORDER_PREFIX + "_completed";
    private static final String EVENT_NAME_ORDER_DISCARDED = EVENT_NAME_ORDER_PREFIX + "_discarded";
    private static final String EVENT_NAME_ORDER_CANCELLED = EVENT_NAME_ORDER_PREFIX + "_cancelled";
    private static final String EVENT_NAME_ORDER_WITH_APPLIED_REWARD = EVENT_NAME_ORDER_PREFIX + "_applied_reward";
    private static final String EVENT_NAME_ORDER_WITH_WALKIN = EVENT_NAME_ORDER_COMPLETED + "_walkin";
    private static final String EVENT_NAME_ORDER_WITH_CURBSIDE = EVENT_NAME_ORDER_COMPLETED + "_curbside";
    private static final String EVENT_NAME_ORDER_WITH_DRIVETHRU = EVENT_NAME_ORDER_COMPLETED + "_drivethru";
    private static final String EVENT_NAME_ORDER_WITH_DELIVERY = EVENT_NAME_ORDER_COMPLETED + "_delivery";
    private static final String EVENT_NAME_ORDER_WITH_BULK = "bulk_" + EVENT_NAME_ORDER_PREFIX;

    private static final String EVENT_NAME_ORDER_FUNDS_ADDED = EVENT_NAME_ORDER_PREFIX + "_funds_added";
    private static final String EVENT_NAME_ORDER_NOT_ENOUGH_FUNDS = EVENT_NAME_ORDER_PREFIX + "_not_enough_funds";
    private static final String EVENT_NAME_ORDER_NOT_ASAP = EVENT_NAME_ORDER_PREFIX + "_not_asap";

    private static final String EVENT_NAME_REORDER_PREFIX = "reorder";
    private static final String EVENT_NAME_REORDER_STARTED = EVENT_NAME_REORDER_PREFIX + "_started";
    private static final String EVENT_NAME_REORDER_COMPLETED = EVENT_NAME_REORDER_PREFIX + "_completed";
    private static final String EVENT_NAME_REORDER_DISCARDED = EVENT_NAME_REORDER_PREFIX + "_discarded";
    private static final String EVENT_NAME_REORDER_CANCELLED = EVENT_NAME_REORDER_PREFIX + "_cancelled";

    private static final String EVENT_NAME_AUTO_RELOAD_PREFIX = "auto_reload";
    private static final String EVENT_NAME_AUTO_RELOAD_STARTED = EVENT_NAME_AUTO_RELOAD_PREFIX + "_started";
    private static final String EVENT_NAME_AUTO_RELOAD_COMPLETED = EVENT_NAME_AUTO_RELOAD_PREFIX + "_completed";
    private static final String EVENT_NAME_AUTO_RELOAD_TERMS_AND_CONDITIONS = EVENT_NAME_AUTO_RELOAD_PREFIX + "_t_and_c";

    private static final String EVENT_NAME_ADD_FUNDS_PREFIX = "add_funds";
    private static final String EVENT_NAME_ADD_FUNDS_STARTED = EVENT_NAME_ADD_FUNDS_PREFIX + "_started";
    private static final String EVENT_NAME_ADD_FUNDS_COMPLETED = EVENT_NAME_ADD_FUNDS_PREFIX + "_completed";

    private static final String EVENT_NAME_REWARD_PREFIX = "reward";
    private static final String EVENT_NAME_REWARD_REDEEM = EVENT_NAME_REWARD_PREFIX + "_redeemed";

    private static final String EVENT_NAME_TRIVIA_PREFIX = "trivia";
    private static final String EVENT_NAME_TRIVIA_CTA_SHOWN = EVENT_NAME_TRIVIA_PREFIX + "_cta_shown";

    private static final String EVENT_NAME_TRIVIA_STARTED = EVENT_NAME_TRIVIA_PREFIX + "_started";

    private static final String EVENT_NAME_TRIVIA_QUESTION = EVENT_NAME_TRIVIA_PREFIX + "_question";

    private static final String EVENT_NAME_TRIVIA_COMPLETED = EVENT_NAME_TRIVIA_PREFIX + "_completed";

    private static final String EVENT_NAME_TRIVIA_WINNER_USE_IN_STORE = EVENT_NAME_TRIVIA_PREFIX + "_winner_use_in_store";

    private static final String EVENT_NAME_TRIVIA_WINNER_MOBILE_ORDER = EVENT_NAME_TRIVIA_PREFIX + "_winner_start_mobile_order";

    private static final String EVENT_PARAM_NAME_TRIVIA_RESULT = "result";

    private static final String EVENT_NAME_TRIVIA_ALREADY_PLAYED = EVENT_NAME_TRIVIA_PREFIX + "_already_played";

    private static final String EVENT_NAME_IM_HERE_SIGNAL = "im_here_signal";
    private static final String EVENT_PARAM_CURBSIDE_ORDER_ID = "curbside_order_id";
    private static final String EVENT_PARAM_CURBSIDE_ACTUAL_PICKUP_TIME = "curbside_actual_pickup_time";
    private static final String EVENT_PARAM_CURBSIDE_DESIRED_PICKUP_TIME = "curbside_desired_pickup_time";

    private static final String EVENT_NAME_ITEM_PREFIX = "item";
    private static final String EVENT_NAME_ITEM_ADD_TO_CART = EVENT_NAME_ITEM_PREFIX + "_add_to_cart";

    private static final String EVENT_NAME_COMPLETED_REGISTRATION = "fb_mobile_complete_registration";


    private final Context mContext;

    private final FirebaseAnalytics mFirebaseAnalytics;

    public EventLoggerImpl(Application application) {
        mContext = application;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
    }

    @Override
    public void logUserProperties(String brand, String environment) {
        mFirebaseAnalytics.setUserProperty(USER_PROPERTY_BRAND, brand);
        mFirebaseAnalytics.setUserProperty(USER_PROPERTY_ENVIRONMENT, environment);
    }

    private void logCustomEvent(String customEventKey) {
        mFirebaseAnalytics.logEvent(customEventKey, null);
        logUrbanAirship(customEventKey);
    }

    private void logUrbanAirship(String key) {
        try {
            (new CustomEvent.Builder(key)).build().track();
        } catch (RuntimeException ex) {
            Log.e(TAG, new LogErrorException("Error logging on urban airship event: " + key, ex));
        }
    }

    private void logCustomEventWithProperties(String key, Map<String, Object> properties) {
        CustomEvent.Builder builder = new CustomEvent.Builder(key);
        Bundle bundle = new Bundle();
        for (HashMap.Entry<String, Object> entry : properties.entrySet()) {
            if (entry.getValue() instanceof String) {
                builder.addProperty(entry.getKey(), (String) entry.getValue());
                bundle.putString(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof Boolean) {
                builder.addProperty(entry.getKey(), (Boolean) entry.getValue());
                bundle.putBoolean(entry.getKey(), (Boolean) entry.getValue());
            } else if (entry.getValue() instanceof Integer) {
                builder.addProperty(entry.getKey(), (Integer) entry.getValue());
                bundle.putInt(entry.getKey(), (Integer) entry.getValue());
            } else if (entry.getValue() instanceof Long) {
                builder.addProperty(entry.getKey(), (Long) entry.getValue());
                bundle.putLong(entry.getKey(), (Long) entry.getValue());
            } else {
                builder.addProperty(entry.getKey(), "" + entry.getValue());
                bundle.putString(entry.getKey(), "" + entry.getValue());
            }
        }
        mFirebaseAnalytics.logEvent(key, bundle);
        builder.build().track();
    }

    private void logSelectContent(String contentProperty, String itemId) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(FirebaseAnalytics.Param.ITEM_ID, itemId);
        properties.put(FirebaseAnalytics.Param.CONTENT_TYPE, contentProperty);
        logCustomEventWithProperties(FirebaseAnalytics.Event.SELECT_CONTENT, properties);
    }

    private void logLogin(String eventName, JoinLoginType loginType) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(FirebaseAnalytics.Param.METHOD, loginType.getLoginTypeValue());
        logCustomEventWithProperties(eventName, properties);
    }

    @Override
    public void logMenuSearch(String searchText) {
        logSelectContent(EVENT_NAME_MENU_SEARCH, searchText);
    }

    @Override
    public void logMenuFilterLevel1(String categoryName) {
        logSelectContent(EVENT_NAME_MENU_FILTER_LEVEL_1, categoryName);
    }

    @Override
    public void logMenuFilterLevel2(MenuCategory category) {
        logSelectContent(EVENT_NAME_MENU_FILTER_LEVEL_2, category.getName());
    }

    @Override
    public void logMenuFiltersLevel3(Set<MenuCategory> filters) {
        for (MenuCategory category : filters) {
            logSelectContent(EVENT_NAME_MENU_FILTER_LEVEL_3, category.getName());
        }
    }

    @Override
    public void logAddedPaymentInfo() {
        logCustomEvent(EVENT_NAME_ADDED_PAYMENT_INFO);
    }

    @Override
    public void logOpenedCheckIn() {
        logCustomEvent(EVENT_NAME_OPENED_CHECK_IN);
    }

    @Override
    public void logCompletedRegistration() {
        logCustomEvent(EVENT_NAME_COMPLETED_REGISTRATION);
    }

    @Override
    public void logEnrollStarted() {
        logCustomEvent(EVENT_NAME_ENROLL_STARTED);
    }

    @Override
    public void logEnrollStepCompleted(JoinLoginType method, AppScreen appScreen) {
        String eventName =
                StringUtils
                        .format("%s_%s_%s_completed",
                                EVENT_NAME_ENROLL_PREFIX, method.getLoginTypeValue(),
                                appScreen.getScreenAnalyticsValue());
        Map<String, Object> properties = new HashMap<>();
        properties.put(FirebaseAnalytics.Param.METHOD, method.getLoginTypeValue());
        properties.put(EVENT_PARAM_SCREEN, appScreen.getScreenAnalyticsValue());
        logCustomEventWithProperties(eventName, properties);

    }

    @Override
    public void logEnrollCompleted(JoinLoginType method) {
        logLogin(EVENT_NAME_ENROLL_FINISHED, method);
        logLogin(FirebaseAnalytics.Event.SIGN_UP, method);
    }

    @Override
    public void logSignInCompleted(JoinLoginType method) {
        logLogin(StringUtils.format("%s_%s", EVENT_NAME_SIGN_IN_PREFIX, method.getLoginTypeValue()), method);
        logLogin(FirebaseAnalytics.Event.LOGIN, method);
    }

    @Override
    public void logEnrollmentError(AppScreen appScreen, String errorMessage) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(EVENT_PARAM_SCREEN, appScreen.getScreenAnalyticsValue());
        properties.put(EVENT_PARAM_ENROLL_ERROR_MESSAGE, errorMessage);
        logCustomEventWithProperties(EVENT_NAME_ENROLL_ERROR, properties);
    }

    @Override
    public void logEnrollmentError(AppScreen appScreen, int errorMessageId) {
        logEnrollmentError(appScreen, mContext.getString(errorMessageId));
    }

    @Override
    public void logOrderStarted(boolean reorder) {
        String event = reorder ? EVENT_NAME_REORDER_STARTED : EVENT_NAME_ORDER_STARTED;
        logCustomEvent(event);
    }

    @Override
    public void logAddToCart() {
        logCustomEvent(EVENT_NAME_ITEM_ADD_TO_CART);
    }

    @Override
    public void logOrderCompleted(boolean reorder, boolean edited, boolean asap, BigDecimal total) {
        String event = reorder ? EVENT_NAME_REORDER_COMPLETED : EVENT_NAME_ORDER_COMPLETED;
        Map<String, Object> properties = new HashMap<>();
        if (reorder) {
            properties.put(EVENT_PARAM_EDITED, edited);
        }
        properties.put(EVENT_PARAM_ASAP, asap);
        logCustomEventWithProperties(event, properties);
    }

    @Override
    public void logOrderCancelled(AppScreen appScreen, boolean reorder) {
        String event = reorder ? EVENT_NAME_REORDER_CANCELLED : EVENT_NAME_ORDER_CANCELLED;
        Map<String, Object> properties = new HashMap<>();
        if (appScreen != null) {
            properties.put(EVENT_PARAM_SCREEN, appScreen.getScreenAnalyticsValue());
        }
        logCustomEventWithProperties(event, properties);
    }

    @Override
    public void logOrderDiscarded(AppScreen appScreen, boolean reorder) {
        Map<String, Object> properties = new HashMap<>();
        if (appScreen != null) {
            properties.put(EVENT_PARAM_SCREEN, appScreen.getScreenAnalyticsValue());
        }
        String event = reorder ? EVENT_NAME_REORDER_DISCARDED : EVENT_NAME_ORDER_DISCARDED;
        logCustomEventWithProperties(event, properties);
    }

    @Override
    public void logOrderNotEnoughFunds() {
        logCustomEvent(EVENT_NAME_ORDER_NOT_ENOUGH_FUNDS);
    }

    @Override
    public void logOrderFundsAdded() {
        logCustomEvent(EVENT_NAME_ORDER_FUNDS_ADDED);
    }

    @Override
    public void logOrderNotAsap() {
        logCustomEvent(EVENT_NAME_ORDER_NOT_ASAP);
    }

    @Override
    public void logAutoReloadStarted() {
        logCustomEvent(EVENT_NAME_AUTO_RELOAD_STARTED);
    }

    @Override
    public void logAutoReloadCompleted() {
        logCustomEvent(EVENT_NAME_AUTO_RELOAD_COMPLETED);
    }

    @Override
    public void logAutoReloadTermsAndConditionsClicked() {
        logCustomEvent(EVENT_NAME_AUTO_RELOAD_TERMS_AND_CONDITIONS);
    }

    @Override
    public void logAddFundsStarted() {
        logCustomEvent(EVENT_NAME_ADD_FUNDS_STARTED);
    }

    @Override
    public void logAddFundsCompleted() {
        logCustomEvent(EVENT_NAME_ADD_FUNDS_COMPLETED);
    }

    @Override
    public void logOrderWithRewardApplied() {
        logCustomEvent(EVENT_NAME_ORDER_WITH_APPLIED_REWARD);
    }

    @Override
    public void logLocationSearch() {
        logCustomEvent(EVENT_NAME_LOCATION_SEARCH);
    }

    @Override
    public void logLocationFilterOrderAhead() {
        logCustomEvent(EVENT_NAME_LOCATION_FILTER_ORDER_AHEAD);
    }

    @Override
    public void logRewardRedeem() {
        logCustomEvent(EVENT_NAME_REWARD_REDEEM);
    }


    @Override
    public void logTriviaCtaShown(TriviaEventSource triviaEventSource) {
        logCustomEvent(EVENT_NAME_TRIVIA_CTA_SHOWN);

        switch (triviaEventSource) {
            case Design1:
            case Design2:
                logCustomEvent(EVENT_NAME_TRIVIA_CTA_SHOWN + "_" + triviaEventSource.getAnalyticsName());
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void logTriviaStarted(TriviaEventSource triviaEventSource) {
        logCustomEvent(EVENT_NAME_TRIVIA_STARTED);
        switch (triviaEventSource) {
            case Design1:
            case Design2:
            case Deeplink:
                logCustomEvent(EVENT_NAME_TRIVIA_STARTED + "_" + triviaEventSource.getAnalyticsName());
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void logTriviaQuestionShown(TriviaEventSource triviaEventSource) {
        logCustomEvent(EVENT_NAME_TRIVIA_QUESTION);
        switch (triviaEventSource) {
            case Design1:
            case Design2:
            case Deeplink:
                logCustomEvent(EVENT_NAME_TRIVIA_QUESTION + "_" + triviaEventSource.getAnalyticsName());
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void logTriviaUseInStore() {
        logCustomEvent(EVENT_NAME_TRIVIA_WINNER_USE_IN_STORE);
    }

    @Override
    public void logTriviaStartMobileOrder() {
        logCustomEvent(EVENT_NAME_TRIVIA_WINNER_MOBILE_ORDER);
    }

    @Override
    public void logOrderPickUpType(YextPickupType yextPickupType) {
        String pickUpTypeEvent = "";
        switch (yextPickupType) {
            case WalkIn:
                pickUpTypeEvent = EVENT_NAME_ORDER_WITH_WALKIN;
                break;
            case Delivery:
                pickUpTypeEvent = EVENT_NAME_ORDER_WITH_DELIVERY;
                break;
            case Curbside:
                pickUpTypeEvent = EVENT_NAME_ORDER_WITH_CURBSIDE;
                break;
            case DriveThru:
                pickUpTypeEvent = EVENT_NAME_ORDER_WITH_DRIVETHRU;
                break;
        }
        logCustomEvent(pickUpTypeEvent);
    }

    @Override
    public void logBulkOrder() {
        logCustomEvent(EVENT_NAME_ORDER_WITH_BULK);
    }

    @Override
    public void logTriviaCompleted(TriviaEventSource triviaEventSource, TriviaEventResult triviaEventResult) {
        Map<String, Object> props = new HashMap<>();
        props.put(EVENT_PARAM_NAME_TRIVIA_RESULT, triviaEventResult.getAnalyticsName());
        logCustomEventWithProperties(EVENT_NAME_TRIVIA_COMPLETED, props);
        switch (triviaEventSource) {
            case Design1:
            case Design2:
            case Deeplink:
                logCustomEvent(EVENT_NAME_TRIVIA_COMPLETED + "_" + triviaEventSource.getAnalyticsName());
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void logTriviaAlreadyPlayed() {
        logCustomEvent(EVENT_NAME_TRIVIA_ALREADY_PLAYED);
    }

    @Override
    public void logImHereSignal(AppScreen appScreen, String orderId, String desiredPickupTime, String actualPickupTime) {
        final Map<String, Object> properties = new HashMap<>();
        properties.put(EVENT_PARAM_SCREEN, appScreen.getScreenAnalyticsValue());
        properties.put(EVENT_PARAM_CURBSIDE_ORDER_ID, orderId);
        properties.put(EVENT_PARAM_CURBSIDE_ACTUAL_PICKUP_TIME, actualPickupTime);
        properties.put(EVENT_PARAM_CURBSIDE_DESIRED_PICKUP_TIME, desiredPickupTime);
        logCustomEventWithProperties(EVENT_NAME_IM_HERE_SIGNAL, properties);
    }

}
