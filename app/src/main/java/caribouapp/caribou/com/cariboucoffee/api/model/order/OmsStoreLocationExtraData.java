package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;

/**
 * Created by asegurola on 4/27/18.
 */

public class OmsStoreLocationExtraData implements Serializable {

    @SerializedName("id")
    private String mId;

    @SerializedName("state")
    private String mState;

    @SerializedName("city")
    private String mCity;

    @SerializedName("lat")
    private double mLatitude;

    @SerializedName("lng")
    private double mLongitude;

    @SerializedName("zip")
    private String mZip;

    @SerializedName("address")
    private String mAddress;

    @SerializedName("phone")
    private String mPhone;

    @SerializedName(value = "name", alternate = {"store_name"})
    private String mName;

    public OmsStoreLocationExtraData(StoreLocation storeLocation) {
        mId = storeLocation.getId();
        mState = storeLocation.getState();
        mCity = storeLocation.getLocatity();
        mLatitude = storeLocation.getLatitude();
        mLongitude = storeLocation.getLongitude();
        mZip = storeLocation.getZipcode();
        mAddress = storeLocation.getAddress();
        mPhone = storeLocation.getPhone();
        mName = storeLocation.getName();
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public String getZip() {
        return mZip;
    }

    public void setZip(String zip) {
        mZip = zip;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
