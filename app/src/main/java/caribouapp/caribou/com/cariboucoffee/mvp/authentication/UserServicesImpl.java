package caribouapp.caribou.com.cariboucoffee.mvp.authentication;

import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.ANON_AUTH_TOKEN;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.AUTH_TOKEN;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.AUTO_RELOAD_CURRENCY;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.AUTO_RELOAD_ENABLED;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.AUTO_RELOAD_ERROR_COUNT;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.AUTO_RELOAD_INCREMENT_AMOUNT;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.AUTO_RELOAD_PAYMENT_CARD_ID;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.AUTO_RELOAD_THRESHOLD_AMOUNT;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CARIBOU_CARD_NUMBER;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CREDIT_CARD_ADDRESS_LINE_1;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CREDIT_CARD_ADDRESS_LINE_2;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CREDIT_CARD_CITY;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CREDIT_CARD_COUNTRY;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CREDIT_CARD_EXP_MONTH;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CREDIT_CARD_EXP_YEAR;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CREDIT_CARD_FIRST_NAME;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CREDIT_CARD_ID;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CREDIT_CARD_LAST_NAME;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CREDIT_CARD_NUMBER_PARTIAL;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CREDIT_CARD_PRIMARY;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CREDIT_CARD_STATE;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CREDIT_CARD_STATUS;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CREDIT_CARD_TOKEN;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CREDIT_CARD_TYPE;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CREDIT_CARD_ZIP;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CURBSIDE_HINT_DISPLAY_COUNTER;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CURBSIDE_LATEST_PICKUP_TIME;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CURBSIDE_LOCATION_MESSAGE;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CURBSIDE_LOCATION_PHONE;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CURBSIDE_ORDER_DATA;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CURBSIDE_PICKUP_DATA;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.CURBSIDE_TEACHING_DISPLAY_COUNTER;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.DELIVERY_ADDRESS_LINE_1;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.DELIVERY_ADDRESS_LINE_2;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.DELIVERY_CONTACT;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.DELIVERY_INSTRUCTIONS;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.DELIVERY_ZIP_CODE;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.FIRST_TIME_ON_DASHBOARD;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.FISERV_TOKEN;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.GUEST_UID;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.GUEST_USER_EMAIL_ID;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.GUEST_USER_FIRST_NAME;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.GUEST_USER_FLOW_STARTED;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.GUEST_USER_LAST_NAME;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.GUEST_USER_PHONE_NUMBER;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.MONEY_BALANCE;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.NATIONAL_SHORTAGE_DISPLAY_COUNTER;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.PERSONAL_INFO_ADDRESS_LINE_1;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.PERSONAL_INFO_ADDRESS_LINE_2;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.PERSONAL_INFO_CITY;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.PERSONAL_INFO_COUNTRY;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.PERSONAL_INFO_DATE_OF_BIRTH;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.PERSONAL_INFO_EMAIL_ADDRESS;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.PERSONAL_INFO_FIRST_NAME;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.PERSONAL_INFO_GENDER;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.PERSONAL_INFO_LAST_NAME;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.PERSONAL_INFO_PHONE;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.PERSONAL_INFO_STATE;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.PERSONAL_INFO_ZIP;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.POINTS_BALANCE;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.PREFERENCES_BRU_PROGRAM;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.PREFERENCES_EBB_PROGRAM;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.PREFERENCES_NNYB_PROGRAM;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.PREFERENCES_PERKS_PROGRAM;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.SHOW_SHARE_A_PERK_ONBOARDING;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.TRIVIA_LAST_PLAYED;
import static caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserConstants.UID;
import static caribouapp.caribou.com.cariboucoffee.util.StringUtils.toPhoneNumberWithoutSymbols;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.securepreferences.SecurePreferences;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.BuildConfig;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsAddress;
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
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.CardEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.model.GuestInfoModel;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import caribouapp.caribou.com.cariboucoffee.util.WebViewUtil;

/**
 * Created by jmsmuy on 11/2/17.
 */

public class UserServicesImpl implements UserServices {

    private static final String TAG = UserServices.class.getSimpleName();
    //Take from library default iteration count
    private static final int ORIGINAL_ITERATION_COUNT = 10000;
    private static final String NULL_SUFFIX = "NULL";
    private final Context mContext;
    private final FirebaseIdProvider mFirebaseIdProvider;
    private final List<UserSignoutListener> mListeners = new ArrayList<>();
    private SecurePreferences mSecureStore;

    public UserServicesImpl(Application application, FirebaseIdProvider firebaseIdProvider) {
        mContext = application;
        mFirebaseIdProvider = firebaseIdProvider;
    }

    private SecurePreferences getSecurePreferences() {
        if (mSecureStore == null && mFirebaseIdProvider.getFirebaseId() != null) {
            mSecureStore = new SecurePreferences(mContext, BuildConfig.USER_ENCRYPTION_KEY,
                    mFirebaseIdProvider.getFirebaseId(), AppConstants.USER_SHARED_PREFS_API28, ORIGINAL_ITERATION_COUNT);
        }
        return mSecureStore;
    }

    @Override
    public void addSignoutListener(UserSignoutListener userListener) {
        mListeners.add(userListener);
    }

    @Override
    public void signOut() {
        clear();
        fireSignOut();
        WebViewUtil.clearCookies();
    }


    private void fireSignOut() {
        for (UserSignoutListener userSignoutListener : mListeners) {
            userSignoutListener.signout();
        }
    }

    private void clear() {
        // Note: This needs to be commit, I detected a bug in a usecase, in which a securePreferences clearing with apply
        // took too long and I was able to close the app and open it again and stayed logged on
        if (getSecurePreferences() != null) {
            getSecurePreferences().edit().clear().commit();
        }
    }

    private void eraseKeys(String... keys) {
        SecurePreferences.Editor editor = getSecurePreferences().edit();
        for (String key : keys) {
            editor.remove(key).remove(buildNullKey(key));
        }
        editor.apply();
    }

    private void saveNullValue(String key) {
        getSecurePreferences().edit().putBoolean(buildNullKey(key), true).apply();
    }

    private String buildNullKey(String key) {
        return key + "_" + NULL_SUFFIX;
    }

    private void saveString(String key, String value) {
        if (value == null) {
            saveNullValue(key);
            return;
        }
        getSecurePreferences().edit().putString(key, value).apply();
    }

    private void saveBoolean(String key, Boolean value) {
        if (value == null) {
            saveNullValue(key);
            return;
        }
        getSecurePreferences().edit().putBoolean(key, value).apply();
    }

    private void saveInteger(String key, Integer value) {
        if (value == null) {
            saveNullValue(key);
            return;
        }
        getSecurePreferences().edit().putInt(key, value).apply();
    }

    private void saveBigDecimal(String key, BigDecimal value) {
        if (value == null) {
            saveNullValue(key);
            return;
        }
        getSecurePreferences().edit().putString(key, GsonUtil.defaultGson().toJson(value)).apply();
    }

    private void saveLocalDate(String key, LocalDate value) {
        if (value == null) {
            saveNullValue(key);
            return;
        }
        getSecurePreferences().edit().putString(key, GsonUtil.defaultGson().toJson(value)).apply();
    }

    private void saveStateEnum(String key, StateEnum value) {
        if (value == null) {
            saveNullValue(key);
            return;
        }
        getSecurePreferences().edit().putString(key, GsonUtil.defaultGson().toJson(value)).apply();
    }

    private String getString(String key, String defaultValue) {
        return getSecurePreferences().getString(key, defaultValue);
    }

    private String getString(String key) {
        return getString(key, null);
    }

    private Boolean getBoolean(String key, Boolean defaultValue) {
        return getSecurePreferences().getBoolean(key, defaultValue);
    }

    private Boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    private Integer getInteger(String key, Integer defaultValue) {
        Integer foundValue = GsonUtil.defaultGson().fromJson(getString(key, null), Integer.class);
        if (foundValue == null) {
            return defaultValue;
        }
        return foundValue;
    }

    private Integer getInteger(String key) {
        return getInteger(key, 0);
    }

    private BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        BigDecimal foundValue = GsonUtil.defaultGson().fromJson(getString(key, null), BigDecimal.class);
        if (foundValue == null) {
            return defaultValue;
        }
        return foundValue;
    }

    private BigDecimal getBigDecimal(String key) {
        return getBigDecimal(key, null);
    }

    private LocalDate getLocalDate(String key, LocalDate defaultValue) {
        LocalDate foundValue = GsonUtil.defaultGson().fromJson(getString(key, null), LocalDate.class);
        if (foundValue == null) {
            return defaultValue;
        }
        return foundValue;
    }

    private LocalDate getLocalDate(String key) {
        return getLocalDate(key, null);
    }

    private StateEnum getStateEnum(String key, StateEnum defaultValue) {
        StateEnum foundValue = GsonUtil.defaultGson().fromJson(getString(key, null), StateEnum.class);
        if (foundValue == null) {
            return defaultValue;
        }
        return foundValue;
    }

    private StateEnum getStateEnum(String key) {
        return getStateEnum(key, null);
    }

    @Override
    public void setUser(AmsResult result) {
        if (result == null) {
            return;
        }
        setPreferences(result.getPreferences());
        setPersonalInfo(result.getPersonalInfo());
        setBillingInformation(result.getAmsBillingInformation());
    }

    @Override
    public void setWallet(SVmsResponse body) {
        eraseKeys(POINTS_BALANCE);
        if (body == null || body.getBalanceResult() == null) {
            return;
        }
        setCaribouCardNumber(body.getBalanceResult().getCardNumber());
        saveBigDecimal(POINTS_BALANCE, body.getBalanceResult().getPointsBalance());
        setMoneyBalance(body.getBalanceResult().getBalanceAmount());
    }

    @Override
    public void setPaymentCard(AmsPaymentCard amsPaymentCard) {
        erasePaymentCardInfo();
        if (amsPaymentCard == null) {
            return;
        }
        saveString(CREDIT_CARD_ID, amsPaymentCard.getId());
        saveString(CREDIT_CARD_NUMBER_PARTIAL, amsPaymentCard.getCardNumberPartial());
        saveString(CREDIT_CARD_TYPE, amsPaymentCard.getCardType());
        saveString(CREDIT_CARD_EXP_MONTH, amsPaymentCard.getExpiringMonth());
        saveString(CREDIT_CARD_EXP_YEAR, amsPaymentCard.getExpiringYear());
        saveString(CREDIT_CARD_FIRST_NAME, amsPaymentCard.getFirstName());
        saveString(CREDIT_CARD_LAST_NAME, amsPaymentCard.getLastName());
        saveString(CREDIT_CARD_STATUS, amsPaymentCard.getStatus());
        saveString(CREDIT_CARD_TOKEN, amsPaymentCard.getToken());
        saveBoolean(CREDIT_CARD_PRIMARY, amsPaymentCard.isPrimary());

        if (amsPaymentCard.getBillingAddress() == null) {
            return;
        }
        saveString(CREDIT_CARD_ADDRESS_LINE_1, amsPaymentCard.getBillingAddress().getAddressLine1());
        saveString(CREDIT_CARD_ADDRESS_LINE_2, amsPaymentCard.getBillingAddress().getAddressLine2());
        saveString(CREDIT_CARD_CITY, amsPaymentCard.getBillingAddress().getCity());
        saveString(CREDIT_CARD_COUNTRY, amsPaymentCard.getBillingAddress().getCountry());
        saveString(CREDIT_CARD_STATE, amsPaymentCard.getBillingAddress().getState());
        saveString(CREDIT_CARD_ZIP, amsPaymentCard.getBillingAddress().getZip());

    }


    @Override
    public void setCaribouCard(AmsStoredValueCard amsStoredValueCard) {
        eraseCaribouCardInfo();
        if (amsStoredValueCard == null) {
            return;
        }
        saveString(CARIBOU_CARD_NUMBER, amsStoredValueCard.getCardNumber());

        if (amsStoredValueCard.getAmsAutoReloadSettings() == null) {
            return;
        }
        saveString(AUTO_RELOAD_CURRENCY, amsStoredValueCard.getAmsAutoReloadSettings().getCurrency());
        saveBoolean(AUTO_RELOAD_ENABLED, amsStoredValueCard.getAmsAutoReloadSettings().getEnabled());
        saveInteger(AUTO_RELOAD_ERROR_COUNT, amsStoredValueCard.getAmsAutoReloadSettings().getErrorCount());
        saveBigDecimal(AUTO_RELOAD_INCREMENT_AMOUNT, amsStoredValueCard.getAmsAutoReloadSettings().getIncrementAmount());
        saveString(AUTO_RELOAD_PAYMENT_CARD_ID, amsStoredValueCard.getAmsAutoReloadSettings().getPaymentCardId());
        saveBigDecimal(AUTO_RELOAD_THRESHOLD_AMOUNT, amsStoredValueCard.getAmsAutoReloadSettings().getThresholdAmount());
    }

    @Override
    public void setPersonalInfo(AmsPersonalInfo personalInfo) {
        eraseKeys(PERSONAL_INFO_DATE_OF_BIRTH, PERSONAL_INFO_EMAIL_ADDRESS, PERSONAL_INFO_FIRST_NAME,
                PERSONAL_INFO_LAST_NAME, PERSONAL_INFO_GENDER, PERSONAL_INFO_PHONE, PERSONAL_INFO_ADDRESS_LINE_1,
                PERSONAL_INFO_ADDRESS_LINE_2, PERSONAL_INFO_CITY, PERSONAL_INFO_COUNTRY, PERSONAL_INFO_STATE,
                PERSONAL_INFO_ZIP);
        if (personalInfo == null) {
            return;
        }
        saveLocalDate(PERSONAL_INFO_DATE_OF_BIRTH, personalInfo.getDateOfBirth());
        saveString(PERSONAL_INFO_EMAIL_ADDRESS, personalInfo.getEmailAddress());
        saveString(PERSONAL_INFO_FIRST_NAME, personalInfo.getFirstName());
        saveString(PERSONAL_INFO_GENDER, personalInfo.getGender());
        saveString(PERSONAL_INFO_LAST_NAME, personalInfo.getLastName());
        saveString(PERSONAL_INFO_PHONE, personalInfo.getMemberPhone());
        if (personalInfo.getAddress() == null) {
            return;
        }
        saveString(PERSONAL_INFO_ADDRESS_LINE_1, personalInfo.getAddress().getAddressLine1() == null
                ? "" : personalInfo.getAddress().getAddressLine1());
        saveString(PERSONAL_INFO_ADDRESS_LINE_2, personalInfo.getAddress().getAddressLine2());
        saveString(PERSONAL_INFO_CITY, personalInfo.getAddress().getCity());
        saveString(PERSONAL_INFO_COUNTRY, personalInfo.getAddress().getCountry() == null
                ? "US" : personalInfo.getAddress().getCountry());
        saveStateEnum(PERSONAL_INFO_STATE, StateEnum.getFromName(personalInfo.getAddress().getState()));
        saveString(PERSONAL_INFO_ZIP, personalInfo.getAddress().getZip());
    }

    @Override
    public void setPreferences(AmsPreferences preferences) {
        eraseKeys(PREFERENCES_PERKS_PROGRAM,
                PREFERENCES_BRU_PROGRAM,
                PREFERENCES_NNYB_PROGRAM,
                PREFERENCES_EBB_PROGRAM);
        if (preferences == null) {
            return;
        }
        saveBoolean(PREFERENCES_PERKS_PROGRAM, preferences.getPerksProgram());
        saveBoolean(PREFERENCES_EBB_PROGRAM, preferences.getEbbProgram());
        saveBoolean(PREFERENCES_NNYB_PROGRAM, preferences.getNnybProgram());
        saveBoolean(PREFERENCES_BRU_PROGRAM, preferences.getBruProgram());
    }

    @Override
    public boolean isSubscribedToRewardsProgram(BrandEnum brand) {
        switch (brand) {
            case CBOU_BRAND:
            case POLAR_BRAND:
                return getBoolean(PREFERENCES_PERKS_PROGRAM, true);

            case BRU_BRAND:
                return getBoolean(PREFERENCES_BRU_PROGRAM, true);

            case NNYB_BRAND:
                return getBoolean(PREFERENCES_NNYB_PROGRAM, true);

            case EBB_BRAND:
                return getBoolean(PREFERENCES_EBB_PROGRAM, true);

            default:
                return true;
        }
    }

    private boolean isMissingKey(String... keys) {
        SecurePreferences securePreferences = getSecurePreferences();
        for (String key : keys) {
            if (!securePreferences.contains(key) && !securePreferences.contains(buildNullKey(key))) {
                Log.d(TAG, "Missing key: " + key);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMissingUserData() {
        return isMissingPreferences()
                || isMissingPersonalInfo()
                || isMissingKey(CARIBOU_CARD_NUMBER);
    }

    private boolean isMissingPersonalInfo() {
        return isMissingKey(PERSONAL_INFO_DATE_OF_BIRTH, PERSONAL_INFO_EMAIL_ADDRESS, PERSONAL_INFO_FIRST_NAME,
                PERSONAL_INFO_LAST_NAME, PERSONAL_INFO_GENDER, PERSONAL_INFO_PHONE, PERSONAL_INFO_ADDRESS_LINE_1,
                PERSONAL_INFO_ADDRESS_LINE_2, PERSONAL_INFO_CITY, PERSONAL_INFO_COUNTRY, PERSONAL_INFO_STATE,
                PERSONAL_INFO_ZIP);
    }

    private boolean isMissingPreferences() {
        return isMissingKey(PREFERENCES_PERKS_PROGRAM,
                PREFERENCES_BRU_PROGRAM,
                PREFERENCES_NNYB_PROGRAM,
                PREFERENCES_EBB_PROGRAM);
    }

    private void eraseCaribouCardInfo() {
        eraseKeys(CARIBOU_CARD_NUMBER, AUTO_RELOAD_CURRENCY, AUTO_RELOAD_ENABLED, AUTO_RELOAD_ERROR_COUNT,
                AUTO_RELOAD_INCREMENT_AMOUNT, AUTO_RELOAD_PAYMENT_CARD_ID, AUTO_RELOAD_THRESHOLD_AMOUNT);
    }

    private void erasePaymentCardInfo() {
        eraseKeys(CREDIT_CARD_ID, CREDIT_CARD_NUMBER_PARTIAL, CREDIT_CARD_TYPE,
                CREDIT_CARD_EXP_MONTH, CREDIT_CARD_EXP_YEAR, CREDIT_CARD_FIRST_NAME,
                CREDIT_CARD_LAST_NAME, CREDIT_CARD_STATUS, CREDIT_CARD_TOKEN, CREDIT_CARD_PRIMARY,
                CREDIT_CARD_ADDRESS_LINE_1, CREDIT_CARD_ADDRESS_LINE_2, CREDIT_CARD_CITY,
                CREDIT_CARD_COUNTRY, CREDIT_CARD_STATE, CREDIT_CARD_ZIP);
    }

    /**
     * Will erase the by key
     */
    private void eraseGuestUserInfo() {
        eraseKeys(GUEST_USER_FIRST_NAME,
                GUEST_USER_LAST_NAME, GUEST_USER_EMAIL_ID,
                GUEST_USER_PHONE_NUMBER);
    }

    @Override
    public void setBillingInformation(AmsBillingInformation amsBillingInformation) {
        List<AmsStoredValueCard> caribouCards = amsBillingInformation.getAmsStoredValueCardList();
        List<AmsPaymentCard> paymentCards = amsBillingInformation.getPaymentCardList();
        if (caribouCards != null && caribouCards.size() > 0) {
            setCaribouCard(caribouCards.get(0));
        } else {
            eraseCaribouCardInfo();
        }
        if (paymentCards != null && paymentCards.size() > 0) {
            setPaymentCard(paymentCards.get(0));
        } else {
            erasePaymentCardInfo();
        }
    }

    @Override
    public AmsPaymentCard getCreditCard() {
        AmsAddress amsAddress = new AmsAddress(getPaymentCardAddressLine1(), getPaymentCardAddressLine2(),
                getPaymentCardCity(), StateEnum.getFromName(getPaymentCardState()), getPaymentCardZip(), "US");
        return new AmsPaymentCard(getPaymentCardFirstName(), getPaymentCardLastName(),
                getPaymentCardPartialNumber(), getPaymentCardExpMonth(), getPaymentCardExpYear(),
                amsAddress, CardEnum.getCardTypeFromApiName(getPaymentCardType()), getpaymentCardToken());
    }

    @Override
    public String getAuthToken() {
        return getString(AUTH_TOKEN, null);
    }

    public void setAuthToken(String authToken) {
        saveString(AUTH_TOKEN, authToken);
    }

    /**
     * get anonymous token for guest user
     */
    @Override
    public String getAnonAuthToken() {
        return getString(ANON_AUTH_TOKEN, null);
    }

    /**
     * set anonymous token for guest user
     */
    @Override
    public void setAnonAuthToken(String token) {
        saveString(ANON_AUTH_TOKEN, token);
    }

    @Override
    public String getFiservToken() {
        return getString(FISERV_TOKEN, null);
    }

    @Override
    public void setFiservToken(String token) {
        saveString(FISERV_TOKEN, token);
    }

    @Override
    public String getLastName() {
        return getString(PERSONAL_INFO_LAST_NAME);
    }

    @Override
    public String getFirstName() {
        return getString(PERSONAL_INFO_FIRST_NAME);
    }

    @Override
    public String getEmail() {
        return getString(PERSONAL_INFO_EMAIL_ADDRESS);
    }


    @Override
    public LocalDate getBirthday() {
        return getLocalDate(PERSONAL_INFO_DATE_OF_BIRTH);
    }

    @Override
    public String getCity() {
        return getString(PERSONAL_INFO_CITY);
    }

    @Override
    public StateEnum getState() {
        return getStateEnum(PERSONAL_INFO_STATE);
    }

    @Override
    public String getZip() {
        return getString(PERSONAL_INFO_ZIP);
    }

    @Override
    public String getCaribouCardNumber() {
        return getString(CARIBOU_CARD_NUMBER);
    }

    @Override
    public void setCaribouCardNumber(String cardNumber) {
        if (cardNumber == null || "".equals(cardNumber)) {
            Log.e(TAG, new LogErrorException("Trying to save empty cardNumber: " + cardNumber));
            return;
        }
        saveString(CARIBOU_CARD_NUMBER, cardNumber);
    }

    @Override
    public String getPhoneNumber() {
        return getString(PERSONAL_INFO_PHONE);
    }

    @Override
    public Boolean getAutoreloadEnabled() {
        return getBoolean(AUTO_RELOAD_ENABLED);
    }

    @Override
    public String getAutoReloadCurrency() {
        return getString(AUTO_RELOAD_CURRENCY);
    }

    @Override
    public BigDecimal getAutoReloadIncrementAmount() {
        return getBigDecimal(AUTO_RELOAD_INCREMENT_AMOUNT);
    }

    @Override
    public BigDecimal getAutoReloadThresholdAmount() {
        return getBigDecimal(AUTO_RELOAD_THRESHOLD_AMOUNT);
    }

    @Override
    public Integer getAutoReloadErrorCount() {
        return getInteger(AUTO_RELOAD_ERROR_COUNT);
    }

    @Override
    public String getPaymentCardId() {
        return getString(CREDIT_CARD_ID);
    }

    @Override
    public String getPaymentCardFirstName() {
        return getString(CREDIT_CARD_FIRST_NAME);
    }

    @Override
    public String getPaymentCardLastName() {
        return getString(CREDIT_CARD_LAST_NAME);
    }

    @Override
    public String getPaymentCardCity() {
        return getString(CREDIT_CARD_CITY);
    }

    @Override
    public String getPaymentCardState() {
        return getString(CREDIT_CARD_STATE);
    }

    @Override
    public String getPaymentCardZip() {
        return getString(CREDIT_CARD_ZIP);
    }

    @Override
    public String getPaymentCardAddressLine1() {
        return getString(CREDIT_CARD_ADDRESS_LINE_1);
    }

    @Override
    public String getPaymentCardAddressLine2() {
        return getString(CREDIT_CARD_ADDRESS_LINE_2);
    }

    private String getPaymentCardPartialNumber() {
        return getString(CREDIT_CARD_NUMBER_PARTIAL);
    }

    private String getPaymentCardExpMonth() {
        return getString(CREDIT_CARD_EXP_MONTH);
    }

    private String getPaymentCardExpYear() {
        return getString(CREDIT_CARD_EXP_YEAR);
    }

    private String getpaymentCardToken() {
        return getString(CREDIT_CARD_TOKEN);
    }

    private String getPaymentCardType() {
        return getString(CREDIT_CARD_TYPE);
    }

    @Override
    public String getUid() {
        return getString(UID);
    }

    @Override
    public void setUid(String uid) {
        saveString(UID, uid);
    }

    @Override
    public String getGuestUid() {
        return getString(GUEST_UID);
    }

    @Override
    public void setGuestUid(String uid) {
        saveString(GUEST_UID, uid);
    }

    @Override
    public BigDecimal getMoneyBalance() {
        return getBigDecimal(MONEY_BALANCE);
    }

    @Override
    public void setMoneyBalance(BigDecimal balance) {
        saveBigDecimal(MONEY_BALANCE, balance);
    }

    @Override
    public BigDecimal getPointsBalance() {
        return getBigDecimal(POINTS_BALANCE);
    }

    @Override
    public boolean hasCreditCardBasicInfo() {
        return getPaymentCardFirstName() != null && getPaymentCardLastName() != null;
    }

    @Override
    public boolean hasCreditCardAddress() {
        return getPaymentCardAddressLine1() != null
                && getPaymentCardCity() != null
                && getPaymentCardZip() != null
                && getPaymentCardState() != null;
    }

    @Override
    public boolean isUsersFirstTimeOnDashboard() {
        return getBoolean(FIRST_TIME_ON_DASHBOARD, true);
    }

    @Override
    public void setUsersFirstTimeOnDashboard(boolean firstTimeOnDashboard) {
        saveBoolean(FIRST_TIME_ON_DASHBOARD, firstTimeOnDashboard);
    }

    @Override
    public boolean isUserLoggedIn() {
        try {
            return !TextUtils.isEmpty(getUid());
        } catch (Exception e) {
            Log.e(TAG, new LogErrorException("Problems checking User Logged In: " + e.getMessage(), e));
            return false;
        }
    }

    @Override
    public boolean isGuestUserFlowStarted() {
        return getBoolean(GUEST_USER_FLOW_STARTED, false);
    }

    @Override
    public void setGuestUserFlowStarted(boolean flowStarted) {
        saveBoolean(GUEST_USER_FLOW_STARTED, flowStarted);
    }

    @Override
    public LocalDate getLastPlayedTrivia() {
        return getLocalDate(TRIVIA_LAST_PLAYED);
    }

    @Override
    public void setLastPlayedTrivia(LocalDate lastPlayedTrivia) {
        saveLocalDate(TRIVIA_LAST_PLAYED, lastPlayedTrivia);
    }

    @Override
    public boolean isShowShareAPerkOnboarding() {
        return getBoolean(SHOW_SHARE_A_PERK_ONBOARDING, true);
    }

    @Override
    public void setShowShareAPerkOnboarding(boolean firstTime) {
        saveBoolean(SHOW_SHARE_A_PERK_ONBOARDING, firstTime);
    }

    @Override
    public String getDeliveryAddressLine1() {
        return getString(DELIVERY_ADDRESS_LINE_1);
    }

    @Override
    public void setDeliveryAddressLine1(String addressLine1) {
        saveString(DELIVERY_ADDRESS_LINE_1, addressLine1);
    }

    @Override
    public String getDeliveryAddressLine2() {
        return getString(DELIVERY_ADDRESS_LINE_2);
    }

    @Override
    public void setDeliveryAddressLine2(String addressLine2) {
        saveString(DELIVERY_ADDRESS_LINE_2, addressLine2);
    }

    @Override
    public String getDeliveryZipCode() {
        return getString(DELIVERY_ZIP_CODE);
    }

    @Override
    public void setDeliveryZipCode(String zipCode) {
        saveString(DELIVERY_ZIP_CODE, zipCode);
    }

    @Override
    public String getDeliveryContact() {
        return getString(DELIVERY_CONTACT);
    }

    @Override
    public void setDeliveryContact(String contact) {
        saveString(DELIVERY_CONTACT, contact);
    }

    @Override
    public String getDeliveryInstructions() {
        return getString(DELIVERY_INSTRUCTIONS);
    }

    @Override
    public void setDeliveryInstructions(String instructions) {
        if (instructions == null) {
            eraseKeys(DELIVERY_INSTRUCTIONS);
            return;
        }
        saveString(DELIVERY_INSTRUCTIONS, instructions);
    }

    @Override
    public void updateCurbsideDataStatus(NcrOrderStatus status) {
        CurbsideOrderData curbsideOrderData = getCurbsideOrderData();
        if (curbsideOrderData == null) {
            return;
        }
        curbsideOrderData.setStatus(status);
        saveCurbsideOrderData(curbsideOrderData);
    }

    @Override
    public void saveCurbsideOrderData(CurbsideOrderData curbsideOrderData) {
        if (curbsideOrderData == null) {
            eraseKeys(CURBSIDE_ORDER_DATA);
            return;
        }
        saveString(CURBSIDE_ORDER_DATA, GsonUtil.defaultGson().toJson(curbsideOrderData));
    }

    @Override
    public DateTime getCurbsideLatestPickupTime() {
        String curbsideDataString = getString(CURBSIDE_LATEST_PICKUP_TIME);
        if (curbsideDataString == null) {
            return null;
        }
        return GsonUtil.defaultGson().fromJson(curbsideDataString, DateTime.class);
    }

    @Override
    public void saveCurbsideLatestPickupTime(DateTime latestCurbsidePickupTime) {
        if (latestCurbsidePickupTime == null) {
            eraseKeys(CURBSIDE_LATEST_PICKUP_TIME);
            return;
        }
        saveString(CURBSIDE_LATEST_PICKUP_TIME, GsonUtil.defaultGson().toJson(latestCurbsidePickupTime));
    }

    @Override
    public CurbsideOrderData getCurbsideOrderData() {
        String curbsideDataString = getString(CURBSIDE_ORDER_DATA);
        if (curbsideDataString == null) {
            return null;
        }
        return GsonUtil.defaultGson().fromJson(curbsideDataString, CurbsideOrderData.class);
    }

    @Override
    public void saveCurbsideLocationPhone(String phone) {
        if (phone == null) {
            eraseKeys(CURBSIDE_LOCATION_PHONE);
            return;
        }
        saveString(CURBSIDE_LOCATION_PHONE, phone);
    }

    @Override
    public String getCurbsideLocationPhone() {
        return getString(CURBSIDE_LOCATION_PHONE);
    }

    @Override
    public void saveCurbsideLocationMessage(String message) {
        if (message == null) {
            eraseKeys(CURBSIDE_LOCATION_MESSAGE);
            return;
        }
        saveString(CURBSIDE_LOCATION_MESSAGE, message);
    }

    @Override
    public String getCurbsideLocationMessage() {
        return getString(CURBSIDE_LOCATION_MESSAGE);
    }

    @Override
    public int getCurbsideHintDisplayCounter() {
        return getInteger(UserConstants.CURBSIDE_HINT_DISPLAY_COUNTER);
    }

    @Override
    public void incrementCurbsideHintDisplayCounter() {
        saveInteger(CURBSIDE_HINT_DISPLAY_COUNTER, getCurbsideHintDisplayCounter() + 1);
    }

    @Override
    public int getCurbSideImHereTeachingCounter() {
        return getInteger(CURBSIDE_TEACHING_DISPLAY_COUNTER);
    }

    @Override
    public void incrementCurbSideImHereTeachingCounter() {
        saveInteger(CURBSIDE_TEACHING_DISPLAY_COUNTER, getCurbSideImHereTeachingCounter() + 1);
    }

    @Override
    public int getNationalShortageDialogCounter() {
        return getInteger(NATIONAL_SHORTAGE_DISPLAY_COUNTER);
    }

    @Override
    public void incrementNationalShortageDialogCounter() {
        saveInteger(NATIONAL_SHORTAGE_DISPLAY_COUNTER, getNationalShortageDialogCounter() + 1);
    }

    @Override
    public CurbsidePickupData getCurbSidePickupData() {
        String curbsideDataString = getString(CURBSIDE_PICKUP_DATA);
        if (curbsideDataString == null) {
            return null;
        }
        return GsonUtil.defaultGson().fromJson(curbsideDataString, CurbsidePickupData.class);
    }

    @Override
    public void saveCurbsidePickupData(CurbsidePickupData curbsidePickupData) {
        if (curbsidePickupData == null) {
            eraseKeys(CURBSIDE_PICKUP_DATA);
            return;
        }
        saveString(CURBSIDE_PICKUP_DATA, GsonUtil.defaultGson().toJson(curbsidePickupData));
    }

    /**
     * Fetch data from memory
     * New model created and return when required
     */
    @Override
    public GuestInfoModel getGuestUser() {
        GuestInfoModel guestInfoModel = new GuestInfoModel();
        guestInfoModel.setGuestFirstName(getString(GUEST_USER_FIRST_NAME));
        guestInfoModel.setGuestLastName(getString(GUEST_USER_LAST_NAME));
        guestInfoModel.setGuestEmailId(getString(GUEST_USER_EMAIL_ID));
        guestInfoModel.setGuestPhoneNumber(getString(GUEST_USER_PHONE_NUMBER));
        return guestInfoModel;
    }

    /**
     * Here we are strong the model data into memory
     */
    @Override
    public void setGuestUser(GuestInfoModel guestUser) {
        if (guestUser == null) {
            return;
        }
        eraseGuestUserInfo();
        saveString(GUEST_USER_FIRST_NAME, guestUser.getGuestFirstName());
        saveString(GUEST_USER_LAST_NAME, guestUser.getGuestLastName());
        saveString(GUEST_USER_EMAIL_ID, guestUser.getGuestEmailId());
        final String phoneNumber = toPhoneNumberWithoutSymbols(guestUser.getGuestPhoneNumber());
        if (!StringUtils.isEmpty(phoneNumber)) {
            saveString(GUEST_USER_PHONE_NUMBER, phoneNumber);
        }
    }
}
