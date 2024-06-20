package caribouapp.caribou.com.cariboucoffee.analytics;

import java.math.BigDecimal;
import java.util.Set;

import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategory;

/**
 * Created by jmsmuy on 04/04/18.
 */

public interface EventLogger {

    void logMenuSearch(String searchText);

    void logMenuFilterLevel1(String categoryName);

    void logMenuFilterLevel2(MenuCategory category);

    void logMenuFiltersLevel3(Set<MenuCategory> filters);

    void logAddedPaymentInfo();

    void logOpenedCheckIn();

    void logCompletedRegistration();

    void logEnrollStarted();

    void logEnrollStepCompleted(JoinLoginType method, AppScreen screenName);

    void logEnrollCompleted(JoinLoginType method);

    void logEnrollmentError(AppScreen appScreen, String errorMessage);

    void logEnrollmentError(AppScreen appScreen, int errorMessageId);

    void logSignInCompleted(JoinLoginType method);

    void logOrderStarted(boolean reorder);

    void logAddToCart();

    void logOrderCompleted(boolean reorder, boolean edited, boolean asap, BigDecimal total);

    void logOrderCancelled(AppScreen appScreen, boolean reorder);

    void logOrderDiscarded(AppScreen appScreen, boolean reorder);

    void logOrderNotEnoughFunds();

    void logOrderFundsAdded();

    void logOrderNotAsap();

    void logAutoReloadStarted();

    void logAutoReloadCompleted();

    void logAutoReloadTermsAndConditionsClicked();

    void logAddFundsStarted();

    void logAddFundsCompleted();

    void logOrderWithRewardApplied();

    void logLocationSearch();

    void logLocationFilterOrderAhead();

    void logRewardRedeem();

    void logUserProperties(String brand, String environment);

    void logTriviaCtaShown(TriviaEventSource triviaEventSource);

    void logTriviaStarted(TriviaEventSource triviaEventSource);

    void logTriviaQuestionShown(TriviaEventSource triviaEventSource);

    void logTriviaCompleted(TriviaEventSource triviaEventSource, TriviaEventResult triviaEventResult);

    void logTriviaAlreadyPlayed();

    void logTriviaUseInStore();

    void logTriviaStartMobileOrder();

    void logOrderPickUpType(YextPickupType yextPickupType);

    void logBulkOrder();

    void logImHereSignal(AppScreen appScreen, String orderId, String desiredPickupTime, String iamHereTime);
}
