package caribouapp.caribou.com.cariboucoffee.common;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;

/**
 * Created by jmsmuy on 11/28/17.
 */

public class CCInformationModel extends BaseObservable implements Serializable {

    private boolean mReplaceCardEnabled = true;
    private String mFirstName;
    private String mLastName;
    private String mCcNumber;
    private String mExpirationMonth;
    private String mExpirationYear;
    private String mCvv;
    private String mBillingAddress;
    private String mBillingAddress2;
    private String mCity;
    private StateEnum mState;
    private String mZip;
    private boolean mApproveChangeCard = false;
    private String mToken;
    private String mNameOnCard;


    @Bindable
    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
        notifyPropertyChanged(BR.firstName);
    }

    @Bindable
    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
        notifyPropertyChanged(BR.lastName);
    }


    @Bindable
    public String getExpirationMonth() {
        return mExpirationMonth == null ? "" : mExpirationMonth;
    }

    public void setExpirationMonth(String expirationMonth) {
        mExpirationMonth = expirationMonth;
        notifyPropertyChanged(BR.expirationMonth);
    }

    @Bindable
    public String getExpirationYear() {
        return mExpirationYear == null ? "" : mExpirationYear;
    }

    public void setExpirationYear(String expirationYear) {
        mExpirationYear = expirationYear;
        notifyPropertyChanged(BR.expirationYear);
    }

    @Bindable
    public String getCvv() {
        return mCvv;
    }

    public void setCvv(String cvv) {
        mCvv = cvv;
        notifyPropertyChanged(BR.cvv);
    }

    @Bindable
    public String getBillingAddress() {
        return mBillingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        mBillingAddress = billingAddress;
        notifyPropertyChanged(BR.billingAddress);
    }

    @Bindable
    public String getBillingAddress2() {
        return mBillingAddress2;
    }

    public void setBillingAddress2(String billingAddress2) {
        mBillingAddress2 = billingAddress2;
        notifyPropertyChanged(BR.billingAddress2);
    }

    @Bindable
    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
        notifyPropertyChanged(BR.city);
    }

    @Bindable
    public StateEnum getState() {
        return mState;
    }

    public void setState(StateEnum state) {
        mState = state;
        notifyPropertyChanged(BR.state);
    }

    @Bindable
    public String getZip() {
        return mZip;
    }

    public void setZip(String zip) {
        mZip = zip;
        notifyPropertyChanged(BR.zip);
    }

    @Bindable
    public String getCcNumber() {
        return mCcNumber;
    }

    public void setCcNumber(String ccNumber) {
        mCcNumber = ccNumber;
        notifyPropertyChanged(BR.ccNumber);
    }

    @Bindable
    public boolean isReplaceCardEnabled() {
        return mReplaceCardEnabled;
    }

    public void setReplaceCardEnabled(boolean replaceCardEnabled) {
        mReplaceCardEnabled = replaceCardEnabled;
        setApproveChangeCard(!replaceCardEnabled);
    }

    @Bindable
    public boolean isApproveChangeCard() {
        return mApproveChangeCard;
    }

    public void setApproveChangeCard(boolean approveChangeCard) {
        mApproveChangeCard = approveChangeCard;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    @Bindable
    public String getNameOnCard() {
        return mNameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        mNameOnCard = nameOnCard;
        notifyPropertyChanged(BR.nameOnCard);
    }
}
