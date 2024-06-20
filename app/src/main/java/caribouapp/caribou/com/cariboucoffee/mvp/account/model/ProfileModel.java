package caribouapp.caribou.com.cariboucoffee.mvp.account.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;

import caribouapp.caribou.com.cariboucoffee.common.StateEnum;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;

/**
 * Created by andressegurola on 12/8/17.
 */

public class ProfileModel extends BaseObservable implements Serializable {
    private String mCity;
    private String mEmail;
    private String mFirstName;
    private String mLastName;
    private StateEnum mState;
    private String mZip;

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
    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
        notifyPropertyChanged(BR.firstName);
        notifyPropertyChanged(BR.fullName);
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
    public StateEnum getState() {
        return mState;
    }

    public void setState(StateEnum state) {
        mState = state;
        notifyPropertyChanged(BR.state);
        notifyPropertyChanged(BR.fullAddress);
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
    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getFullAddress() {
        if (getCity() == null && getState() != null) {
            return getState().getAbbreviation();
        }
        if (getState() == null) {
            return getCity();
        }
        return StringUtils.format("%s, %s", getCity(), getState().getAbbreviation());
    }

    @Bindable
    public String getFullName() {
        if (getLastName() == null) {
            return getFirstName();
        }
        if (getFirstName() == null) {
            return getLastName();
        }
        return StringUtils.format("%s %s", getFirstName(), getLastName());
    }
}
