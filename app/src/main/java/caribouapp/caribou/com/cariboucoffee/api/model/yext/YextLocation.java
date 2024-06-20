package caribouapp.caribou.com.cariboucoffee.api.model.yext;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class YextLocation {

    @SerializedName("name")
    private String mLocationName;

    @SerializedName("address")
    private YextLocationAddress mAddress;

    @SerializedName("mainPhone")
    private String mPhone;

    @SerializedName("yextDisplayCoordinate")
    private YextLocationDisplayCoordinates mYextLocationDisplayCoordinates;

    @SerializedName("hours")
    private YextOpenHours mHours;

    @SerializedName("closed")
    private boolean isClosed;

    @SerializedName("c_deliveryHours")
    private YextOpenHours mDeliveryHours;

    @SerializedName("c_amenities")
    private List<String> mAmenities;

    @SerializedName("c_acceptsTips")
    private boolean mAcceptsTips;

    @SerializedName("c_restrictedAccess")
    private boolean mRestrictedAccess;

    @SerializedName("c_storeDescription")
    private String mStoreDescription;

    @SerializedName("c_prepTime")
    private String mPreparationTime;

    @SerializedName("c_orderAheadTempOff")
    private boolean mOrderAheadTempOff;

    @SerializedName("c_orderAhead")
    private boolean mIsOrderAhead;

    @SerializedName("c_brand")
    private List<String> mBrand;

    @SerializedName("c_deliveryFee")
    private String mDeliveryFee;

    @SerializedName("c_deliveryRadius")
    private String mDeliveryRadius;

    @SerializedName("c_minimumDeliveryAmount")
    private String mMinimumDeliveryAmount;

    @SerializedName("c_orderPickUp")
    private List<YextPickupType> mOrderPickUp = new ArrayList<>();

    @SerializedName("meta")
    private YextMetaData mMetaData;

    @SerializedName("c_curbsideInstruction")
    private String mCurbsideInstruction;

    @SerializedName("c_guestCheckout")
    private boolean mGuestCheckout;

    @SerializedName("timezone")
    private String timezone;

    public String getLocationName() {
        return mLocationName;
    }

    public void setLocationName(String locationName) {
        mLocationName = locationName;
    }

    public YextLocationAddress getAddress() {
        return mAddress;
    }

    public void setAddress(YextLocationAddress address) {
        mAddress = address;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public YextLocationDisplayCoordinates getYextLocationDisplayCoordinates() {
        return mYextLocationDisplayCoordinates;
    }

    public void setYextLocationDisplayCoordinates(YextLocationDisplayCoordinates yextLocationDisplayCoordinates) {
        mYextLocationDisplayCoordinates = yextLocationDisplayCoordinates;
    }

    public YextOpenHours getHours() {
        return mHours;
    }

    public void setHours(YextOpenHours hours) {
        mHours = hours;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public List<String> getAmenities() {
        return mAmenities;
    }

    public void setAmenities(List<String> amenities) {
        mAmenities = amenities;
    }

    public boolean isRestrictedAccess() {
        return mRestrictedAccess;
    }

    public void setRestrictedAccess(boolean restrictedAccess) {
        mRestrictedAccess = restrictedAccess;
    }

    public String getStoreDescription() {
        return mStoreDescription;
    }

    public void setStoreDescription(String storeDescription) {
        mStoreDescription = storeDescription;
    }

    public String getPreparationTime() {
        return mPreparationTime;
    }

    public void setPreparationTime(String preparationTime) {
        mPreparationTime = preparationTime;
    }

    public boolean isOrderAhead() {
        return mIsOrderAhead;
    }

    public void setOrderAhead(boolean orderAhead) {
        mIsOrderAhead = orderAhead;
    }

    public List<String> getBrand() {
        return mBrand;
    }

    public void setBrand(List<String> brand) {
        mBrand = brand;
    }

    public YextMetaData getMetaData() {
        return mMetaData;
    }

    public void setMetaData(YextMetaData metaData) {
        mMetaData = metaData;
    }

    public boolean isOrderAheadTempOff() {
        return mOrderAheadTempOff;
    }

    public void setOrderAheadTempOff(boolean orderAheadTempOff) {
        mOrderAheadTempOff = orderAheadTempOff;
    }

    public List<YextPickupType> getOrderPickUp() {
        return mOrderPickUp;
    }

    public void setOrderPickUp(List<YextPickupType> orderPickUp) {
        mOrderPickUp = orderPickUp;
    }

    public String getDeliveryFee() {
        return mDeliveryFee;
    }

    public void setDeliveryFee(String deliveryFee) {
        mDeliveryFee = deliveryFee;
    }

    public String getDeliveryRadius() {
        return mDeliveryRadius;
    }

    public void setDeliveryRadius(String deliveryRadius) {
        mDeliveryRadius = deliveryRadius;
    }

    public String getMinimumDeliveryAmount() {
        return mMinimumDeliveryAmount;
    }

    public void setMinimumDeliveryAmount(String minimumDeliveryAmount) {
        mMinimumDeliveryAmount = minimumDeliveryAmount;
    }

    public YextOpenHours getDeliveryHours() {
        return mDeliveryHours;
    }

    public void setDeliveryHours(YextOpenHours deliveryHours) {
        mDeliveryHours = deliveryHours;
    }

    public boolean isAcceptsTips() {
        return mAcceptsTips;
    }

    public void setAcceptsTips(boolean acceptsTips) {
        mAcceptsTips = acceptsTips;
    }

    public String getCurbsideInstruction() {
        return mCurbsideInstruction;
    }

    public void setCurbsideInstruction(String curbsideInstruction) {
        mCurbsideInstruction = curbsideInstruction;
    }

    public boolean isGuestCheckout() {
        return mGuestCheckout;
    }

    public void setGuestCheckout(boolean isGuestCheckout) {
        mGuestCheckout = isGuestCheckout;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void populateStoreHours() {
        if (isClosed()) {
            return;
        }
        YextOpenHours yextOpenHours = getHours();
        if (yextOpenHours != null) {
            yextOpenHours.populateOpenHours();
        }
        YextOpenHours deliveryOpenHours = getDeliveryHours();
        if (deliveryOpenHours != null) {
            deliveryOpenHours.populateOpenHours();
        }
    }
}
