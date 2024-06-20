package caribouapp.caribou.com.cariboucoffee.mvp.authentication;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsBillingInformation;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsPaymentCard;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsPersonalInfo;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsPreferences;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResult;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsStoredValueCard;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderStatus;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.SVmsResponse;
import caribouapp.caribou.com.cariboucoffee.common.BrandEnum;
import caribouapp.caribou.com.cariboucoffee.common.StateEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.model.GuestInfoModel;

/**
 * Created by jmsmuy on 11/2/17.
 */

public interface UserServices {

    void addSignoutListener(UserSignoutListener userListener);

    void signOut();

    String getAuthToken();

    void setAuthToken(String token);

    String getAnonAuthToken();

    void setAnonAuthToken(String token);

    String getFiservToken();

    void setFiservToken(String token);

    void setPaymentCard(AmsPaymentCard amsPaymentCard);

    void setCaribouCard(AmsStoredValueCard amsStoredValueCard);

    void setPersonalInfo(AmsPersonalInfo personalInfo);

    void setPreferences(AmsPreferences preferences);

    void setWallet(SVmsResponse body);

    void setUser(AmsResult result);

    String getLastName();

    String getFirstName();

    String getEmail();

    LocalDate getBirthday();

    String getCity();

    StateEnum getState();

    String getZip();

    String getPhoneNumber();

    String getCaribouCardNumber();

    void setCaribouCardNumber(String cardNumber);

    Boolean getAutoreloadEnabled();

    String getAutoReloadCurrency();

    BigDecimal getAutoReloadIncrementAmount();

    BigDecimal getAutoReloadThresholdAmount();

    Integer getAutoReloadErrorCount();

    String getPaymentCardId();

    String getPaymentCardFirstName();

    String getPaymentCardLastName();

    String getPaymentCardCity();

    String getPaymentCardState();

    String getPaymentCardZip();

    String getPaymentCardAddressLine1();

    String getPaymentCardAddressLine2();

    String getUid();

    void setUid(String uid);

    String getGuestUid();

    void setGuestUid(String uid);

    BigDecimal getMoneyBalance();

    void setMoneyBalance(BigDecimal balance);

    void setBillingInformation(AmsBillingInformation amsBillingInformation);

    AmsPaymentCard getCreditCard();

    boolean hasCreditCardBasicInfo();

    boolean hasCreditCardAddress();

    BigDecimal getPointsBalance();

    boolean isMissingUserData();

    boolean isSubscribedToRewardsProgram(BrandEnum brand);

    boolean isUsersFirstTimeOnDashboard();

    void setUsersFirstTimeOnDashboard(boolean firstTimeOnDashboard);

    boolean isUserLoggedIn();

    boolean isGuestUserFlowStarted();

    void setGuestUserFlowStarted(boolean flowStarted);

    LocalDate getLastPlayedTrivia();

    void setLastPlayedTrivia(LocalDate lastPlayedTrivia);

    boolean isShowShareAPerkOnboarding();

    void setShowShareAPerkOnboarding(boolean firstTime);

    String getDeliveryAddressLine1();

    //Delivery user data
    void setDeliveryAddressLine1(String addressLine1);

    String getDeliveryAddressLine2();

    void setDeliveryAddressLine2(String addressLine2);

    String getDeliveryZipCode();

    void setDeliveryZipCode(String zipCode);

    String getDeliveryContact();

    void setDeliveryContact(String contact);

    String getDeliveryInstructions();

    void setDeliveryInstructions(String instructions);

    void updateCurbsideDataStatus(NcrOrderStatus status);

    void saveCurbsideOrderData(CurbsideOrderData curbsideOrderData);

    DateTime getCurbsideLatestPickupTime();

    void saveCurbsideLatestPickupTime(DateTime latestCurbsidePickupTime);

    CurbsideOrderData getCurbsideOrderData();

    void saveCurbsideLocationPhone(String phone);

    String getCurbsideLocationPhone();

    void saveCurbsideLocationMessage(String message);

    String getCurbsideLocationMessage();

    int getCurbsideHintDisplayCounter();

    void incrementCurbsideHintDisplayCounter();

    int getCurbSideImHereTeachingCounter();

    void incrementCurbSideImHereTeachingCounter();

    int getNationalShortageDialogCounter();

    void incrementNationalShortageDialogCounter();

    CurbsidePickupData getCurbSidePickupData();

    void saveCurbsidePickupData(CurbsidePickupData curbsidePickupData);

    /**
     * Stored and retrieved data from UserServices
     */
    GuestInfoModel getGuestUser();

    void setGuestUser(GuestInfoModel guestUser);

    interface UserSignoutListener {
        void signout();
    }

}
