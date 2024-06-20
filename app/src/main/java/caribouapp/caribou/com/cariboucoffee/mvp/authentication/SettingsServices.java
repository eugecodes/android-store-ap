package caribouapp.caribou.com.cariboucoffee.mvp.authentication;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.common.ReviewStatusEnum;

/**
 * Created by jmsmuy on 11/7/17.
 */

public interface SettingsServices {

    boolean getGoogleSignIn();

    boolean isPointsDisplay();

    DateTime getSnoozeDateTime();

    void setSnoozeDatetime(DateTime snoozeTime);

    void clear();

    void incrementLaunchSinceLastReview();

    void resetLaunchSinceLastReview();

    String getLastReviewedVersion();

    void setLastReviewedVersion(String version);

    String getPreviousAndroidVersion();

    int getLaunchCounter();

    ReviewStatusEnum getReviewStatus();

    void setReviewStatus(ReviewStatusEnum reviewStatus);

    void setAndroidAppReviewVersion(String version);

    String getServerAndroidAppReviewVersion();

    String getServerAppDormancy();

    String getServerAppPrompt();

    boolean isDisplayMenu();

    boolean isLocations();

    boolean isEgift();

    boolean isNews();

    boolean isFaqs();

    boolean isOrderAhead();

    boolean isReorder();

    String getMinimumSupportedVersion();

    String getRecommendedVersion();

    String getChooseLocationHeaderText();

    boolean isOpenToCheckIn();

    boolean isTrivia();

    boolean isShareAPerkEnabled();

    boolean isShowItemCustomizationModifiers();

    int getOrderAheadCheckStatusMaxAttempts();

    int getBulkPrepTimeInMins();

    boolean isBulkOrderingEnabled();

    double getBarcodeScreenBrightness();

    boolean isPickupTypeSelectionEnabled();

    boolean isTippingEnabled();

    List<String> getPickupCurbsideCarTypes();

    List<BigDecimal> getTippingMainOptions();

    List<String> getPickupCurbsideCarColors();

    String getPickupCurbsideTipMessage();

    String getPickupDeliveryPrepMessage();

    int getOrderMinutesBeforeClosing();

    int getPickupTimeFirstOptionOffset();

    String getPickupASAPMessage();

    String getPickupScheduleMessage();

    String getMenuItemNotAvailableMessage();

    boolean isImHereCurbSideFeatureEnabled();

    int getCurbSideImHereTeachingMessageMaxAttempts();

    String getCurbSideImHereTeachingMessage();

    String getCurbSideImHereMessage();

    int getCurbsideImHereMinutesToStart();

    int getCurbsideImHereMinutesToEnd();

    int getCurbsideCheckFinishedAttempts();

    int getCurbsideCheckFinishedDelay();

    boolean isOrderAheadRewardsEnabled();

    boolean isOrderAheadRewardsDefaultTab();

    double getAddFundsCheckoutMinAmount();

    String getCurbsideCarMakesOptions();

    String getCurbSidePickupDescription();

    String getWalkInPickupDescription();

    String getDriveThruPickupDescription();

    String getDeliveryPickupDescription();

    boolean isNationalOutageDialogEnabled();

    String getNationalOutageDialogTitle();

    String getNationalOutageDialogMessage();

    int getNationalOutageDialogMaxAttempts();

    String getZeroTotalOrderErrorMessage();

    String getTokenExpiredMessageTitle();

    String getTokenExpiredMessage();

    String getDeleteAccountTitle();

    String getAccountBalanceTitle();

    String getConfirmationTitle();

    String getDeleteAccountMessage();

    String getAccountBalanceMessage();

    String getConfirmationMessage();

    boolean isDeleteAccountFeatureEnabled();

    String getNoahContactUsURL();

    String getEBBContactUsURL();

    String getBRUEGGERSContactUsURL();

    boolean isGuestCheckoutEnabledFromDashboard();

    boolean isContinueAsGuestCheckEnabled();

    /**
     * Header for Let's Get Started guest details popup
     */
    String getLetsGetStartedMessage();

    /**
     * Flag for showing the all billing information on UI
     * if True then will show all fields
     * if False then will show minimum fields
     * https://bagels.atlassian.net/wiki/spaces/BA/pages/8236204046/Guest+Flow+Requirements
     *
     * @return
     */
    Boolean showAllBillingInfoFieldsOnUI();

    String getPGCommonErrorMsg();

    String getCardDeclinedMsg();

    String getCardValidationFailureMsg();

    String getPriceOverLimitMsg();


    String getDeviceId();

    boolean isGuestNotificationEnabled();

    int getTimeoutDefault();

    int getTimeoutAddFunds();

    boolean isGooglePayEnabled();

    boolean isPayPalEnabled();

    boolean isVenmoEnabled();

    boolean isPaymentSelectionScreenEnabled();
}
