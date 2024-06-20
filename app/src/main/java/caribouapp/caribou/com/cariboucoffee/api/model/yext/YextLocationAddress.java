package caribouapp.caribou.com.cariboucoffee.api.model.yext;

import com.google.gson.annotations.SerializedName;

public class YextLocationAddress {

    @SerializedName("line1")
    private String mAddressLine1;

    @SerializedName("line2")
    private String mAddressLine2;

    @SerializedName("city")
    private String mCity;

    @SerializedName("region")
    private String mState;

    @SerializedName("postalCode")
    private String mZipcode;

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

    public String getZipcode() {
        return mZipcode;
    }

    public void setZipcode(String zipcode) {
        mZipcode = zipcode;
    }
}
