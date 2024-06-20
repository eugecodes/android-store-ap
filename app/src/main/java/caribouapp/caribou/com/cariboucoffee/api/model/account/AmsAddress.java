package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import caribouapp.caribou.com.cariboucoffee.common.StateEnum;

/**
 * Created by jmsmuy on 11/2/17.
 */

public class AmsAddress implements Serializable {

    @SerializedName("city")
    private String mCity;

    @SerializedName("state")
    private String mState;

    @SerializedName("zip")
    private String mZip;

    @SerializedName("country")
    private String mCountry;

    @SerializedName("addressLine1")
    private String mAddressLine1;

    @SerializedName("addressLine2")
    private String mAddressLine2;

    public AmsAddress(String billingAddress, String billingAddress2, String city, StateEnum state, String zip, String country) {
        setAddressLine1(billingAddress);
        setAddressLine2(billingAddress2);
        setCity(city);
        setCountry(country);
        if (state != null) {
            setState(state.getAbbreviation());
        }
        setZip(zip);
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public String getZip() {
        return mZip;
    }

    public void setZip(String zip) {
        mZip = zip;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public String getAddressLine1() {
        return mAddressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        mAddressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return mAddressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        mAddressLine2 = addressLine2;
    }
}
