package caribouapp.caribou.com.cariboucoffee.mvp.enrollment.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import org.joda.time.LocalDate;

import java.io.Serializable;

import caribouapp.caribou.com.cariboucoffee.common.StateEnum;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;

/**
 * Created by jmsmuy on 1/8/18.
 */

public class PersonalInformationModel extends BaseObservable implements Serializable {

    private boolean mBirthdayAlreadyDefined;
    private String mFirstName;
    private String mLastName;
    private String mZipCode;
    private String mCity;
    private String mPhoneNumber;
    private String mEmail;
    private StateEnum mState;
    private LocalDate mBirthday;
    private boolean mMarketingMails = true;
    private boolean mCateringMails = true;
    private boolean mTermsNConditions;
    private boolean mFirstAttempt = true;
    private boolean mEditPersonalInformation;
    private boolean mIsWelcomeBackUser;
    private boolean mEnrolledViaGoogle;

    @Bindable
    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
        notifyPropertyChanged(BR.firstName);
        notifyPropertyChanged(BR.fullName);
    }

    @Bindable
    public LocalDate getBirthday() {
        return mBirthday;
    }

    public void setBirthday(LocalDate birthday) {
        mBirthday = birthday;
        notifyPropertyChanged(BR.birthday);
    }

    @Bindable
    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
        notifyPropertyChanged(BR.lastName);
        notifyPropertyChanged(BR.fullName);
    }

    @Bindable
    public String getZipCode() {
        return mZipCode;
    }

    public void setZipCode(String zipCode) {
        mZipCode = zipCode;
        notifyPropertyChanged(BR.zipCode);
    }

    @Bindable
    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
        notifyPropertyChanged(BR.city);
        notifyPropertyChanged(BR.fullAddress);
    }

    @Bindable
    public StateEnum getState() {
        return mState;
    }

    public void setState(StateEnum state) {
        mState = state;
        notifyPropertyChanged(BR.state);
        notifyPropertyChanged(BR.fullAddress);
    }

    @Bindable
    public boolean isMarketingMails() {
        return mMarketingMails;
    }

    public void setMarketingMails(boolean marketingMails) {
        mMarketingMails = marketingMails;
        notifyPropertyChanged(BR.marketingMails);
    }

    @Bindable
    public boolean isTermsNConditions() {
        return mTermsNConditions;
    }

    public void setTermsNConditions(boolean termsNConditions) {
        mTermsNConditions = termsNConditions;
        notifyPropertyChanged(BR.termsNConditions);
    }

    public boolean isFirstAttempt() {
        return mFirstAttempt;
    }

    public void setFirstAttempt(boolean firstAttempt) {
        mFirstAttempt = firstAttempt;
    }

    @Bindable
    public boolean isBirthdayAlreadyDefined() {
        return mBirthdayAlreadyDefined;
    }

    public void setBirthdayAlreadyDefined(boolean birthdayAlreadyDefined) {
        mBirthdayAlreadyDefined = birthdayAlreadyDefined;
        notifyPropertyChanged(BR.birthdayAlreadyDefined);
    }

    @Bindable
    public String getFullAddress() {
        return getCity() == null || getState() == null
                ? null : StringUtils.format("%s, %s", getCity(), getState());
    }

    @Bindable
    public String getFullName() {
        return getFirstName() == null || getLastName() == null
                ? null : StringUtils.format("%s %s", getFirstName(), getLastName());
    }

    @Bindable
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
        notifyPropertyChanged(BR.phoneNumber);
    }

    @Bindable
    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
        notifyPropertyChanged(BR.email);
    }

    public boolean isEditPersonalInformation() {
        return mEditPersonalInformation;
    }

    public void setEditPersonalInformation(boolean editPersonalInformation) {
        mEditPersonalInformation = editPersonalInformation;
    }

    public boolean isWelcomeBackUser() {
        return mIsWelcomeBackUser;
    }

    public void setWelcomeBackUser(boolean welcomeBackUser) {
        mIsWelcomeBackUser = welcomeBackUser;
    }

    public boolean isEnrolledViaGoogle() {
        return mEnrolledViaGoogle;
    }

    public void setEnrolledViaGoogle(boolean enrolledViaGoogle) {
        mEnrolledViaGoogle = enrolledViaGoogle;
    }

    @Bindable
    public boolean isCateringMails() {
        return mCateringMails;
    }

    public void setCateringMails(boolean cateringMails) {
        mCateringMails = cateringMails;
        notifyPropertyChanged(BR.cateringMails);
    }
}
