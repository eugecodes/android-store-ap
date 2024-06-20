package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import android.location.Location;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalTime;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsStoreLocationExtraData;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextDate;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextLocation;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextLocationAddress;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextLocationDisplayCoordinates;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextOpenHours;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextTimePeriod;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.StoreHoursCheckUtil;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;


/**
 * Created by andressegurola on 10/5/17.
 */

public class StoreLocation extends BaseObservable implements Serializable {

    private static final String TAG = StoreLocation.class.getSimpleName();

    private static final String DEFAULT_PREPARATION_TIME = "10 - 15";

    @SerializedName("id")
    private String mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("restrictedAccessLocation")
    private boolean mRestrictedAccessLocation;

    @SerializedName("address")
    private String mAddress;

    @SerializedName("addressShort")
    private String mAddressShort;

    @SerializedName("phone")
    private String mPhone;

    @SerializedName("distanceInMiles")
    private Double mDistanceInMiles;

    @SerializedName("locality")
    private String mLocality;

    @SerializedName("state")
    private String mState;

    @SerializedName("latitude")
    private double mLatitude;

    @SerializedName("longitude")
    private double mLongitude;

    @SerializedName("zipcode")
    private String mZipcode;

    @SerializedName("preparationTime")
    private String mPreparationTime;

    @SerializedName("features")
    private List<StoreFeature> mFeatures = new ArrayList<>();

    @SerializedName("openHourSchedule")
    private Map<Integer, LocationScheduleModel> mOpenHourSchedule = new HashMap<>();

    @SerializedName("deliveryHoursSchedule")
    private Map<Integer, LocationScheduleModel> mDeliveryHoursSchedule = new HashMap();

    @SerializedName("comingSoon")
    private boolean mComingSoon;

    @SerializedName("closed")
    private boolean mIsClosed;

    @SerializedName("orderAheadTempOff")
    private boolean mOrderAheadTempOff;

    @SerializedName("holidayHours")
    private List<YextDate> mHolidayHours = new ArrayList<>();

    @SerializedName("deliveryHolidayHours")
    private List<YextDate> mDeliveryHolidayHours = new ArrayList<>();

    @SerializedName("pickupTypes")
    private List<YextPickupType> mPickupTypes;

    @SerializedName("deliveryFee")
    private BigDecimal mDeliveryFee;

    @SerializedName("deliveryMinimum")
    private BigDecimal mDeliveryMinimum;

    @SerializedName("deliveryRadius")
    private BigDecimal mDeliveryRadius;

    @SerializedName("acceptsTip")
    private boolean mAcceptsTips;

    private String mCurbsideInstruction;

    @SerializedName("guestCheckoutEnabled")
    private boolean mGuestCheckoutEnabled;

    private String mTimeZone;

    public StoreLocation() {

    }

    public StoreLocation(OmsStoreLocationExtraData omsStoreLocationExtraData) {
        mId = String.valueOf(omsStoreLocationExtraData.getId());
        mName = omsStoreLocationExtraData.getName();
        mPhone = omsStoreLocationExtraData.getPhone();
        mLatitude = omsStoreLocationExtraData.getLatitude();
        mLongitude = omsStoreLocationExtraData.getLongitude();
        mAddress = omsStoreLocationExtraData.getAddress();
        mZipcode = omsStoreLocationExtraData.getZip();
        mLocality = omsStoreLocationExtraData.getCity();
        mState = omsStoreLocationExtraData.getState();
    }

    public StoreLocation(YextLocation yextLocation, LatLng currentLocation) {

        mId = yextLocation.getMetaData().getId();

        mName = yextLocation.getStoreDescription();

        mRestrictedAccessLocation = yextLocation.isRestrictedAccess();

        mPhone = StringUtils.formatPhoneNumber(yextLocation.getPhone());
        YextLocationDisplayCoordinates displayCoordinates = yextLocation.getYextLocationDisplayCoordinates();
        mLatitude = displayCoordinates.getYextDisplayLat();
        mLongitude = displayCoordinates.getYextDisplayLng();
        mAcceptsTips = yextLocation.isAcceptsTips();


        setDistanceToCurrentLocation(currentLocation);

        YextLocationAddress address = yextLocation.getAddress();
        mAddress =
                StringUtils.appendWithNewLine(
                        address.getAddressLine1(),
                        address.getCity() + ", " + address.getState() + " " + address.getZipcode()
                );

        mZipcode = address.getZipcode();

        mAddressShort = address.getAddressLine1();
        mIsClosed = yextLocation.isClosed();


        mPreparationTime = yextLocation.getPreparationTime();
        if (mPreparationTime == null) {
            mPreparationTime = DEFAULT_PREPARATION_TIME;
        }
        mLocality = address.getCity();
        mState = address.getState();
        loadDeliveryData(yextLocation);

        loadYextFeatures(yextLocation);
        loadSchedules(yextLocation);
        mOrderAheadTempOff = yextLocation.isOrderAheadTempOff();

        loadPickupTypes(yextLocation);

        mCurbsideInstruction = yextLocation.getCurbsideInstruction();

        mGuestCheckoutEnabled = yextLocation.isGuestCheckout();
        mTimeZone = yextLocation.getTimezone();
    }

    private void loadPickupTypes(YextLocation yextLocation) {
        List<YextPickupType> pickupTypes = yextLocation.getOrderPickUp();
        mPickupTypes = new ArrayList<>();

        if (pickupTypes == null) {
            return;
        }

        for (YextPickupType yextPickupType : pickupTypes) {
            if (yextPickupType != null) {
                mPickupTypes.add(yextPickupType);
            }
        }
    }


    private void setDistanceToCurrentLocation(LatLng currentLocation) {
        if (currentLocation == null) {
            mDistanceInMiles = null;
            return;
        }

        Location loc1 = new Location("");
        loc1.setLatitude(mLatitude);
        loc1.setLongitude(mLongitude);

        Location loc2 = new Location("");
        loc2.setLatitude(currentLocation.latitude);
        loc2.setLongitude(currentLocation.longitude);
        mDistanceInMiles = (double) loc1.distanceTo(loc2) / AppConstants.METERS_IN_A_MILE; // Divided by 1600 to convert from meters to miles.
    }

    private void loadYextFeatures(YextLocation yextLocation) {
        mFeatures.clear();
        mFeatures.add(StoreFeature.WIFI);

        @SuppressWarnings("unchecked")
        List<String> services = yextLocation.getAmenities();
        if (services != null) {
            for (String service : services) {
                mFeatures.add(StoreFeature.fromYextName(service));
            }
        }
        if (yextLocation.isOrderAhead()) {
            mFeatures.add(StoreFeature.ORDER_OUT_OF_STORE);
        }
    }

    private void loadSchedules(YextLocation yextLocation) {
        mOpenHourSchedule.clear();
        mHolidayHours.clear();
        mDeliveryHoursSchedule.clear();
        mDeliveryHolidayHours.clear();

        if (yextLocation.isClosed()) {
            Log.i(TAG, "Closed store: " + yextLocation.getStoreDescription());
            return;
        }

        YextOpenHours openHours = yextLocation.getHours();
        if (openHours != null) {
            mOpenHourSchedule.putAll(loadYextSchedule(yextLocation, openHours));
            if (openHours.getHolidayHours() != null) {
                mHolidayHours.addAll(openHours.getHolidayHours());
            }
        }

        YextOpenHours deliveryHours = yextLocation.getDeliveryHours();
        if (deliveryHours != null) {
            mDeliveryHoursSchedule.putAll(loadYextSchedule(yextLocation, deliveryHours));
            if (deliveryHours.getHolidayHours() != null) {
                mDeliveryHolidayHours.addAll(deliveryHours.getHolidayHours());
            }
        }
    }

    private Map<Integer, LocationScheduleModel> loadYextSchedule(YextLocation yextLocation, YextOpenHours yextOpenHours) {
        Map<Integer, LocationScheduleModel> locationScheduleModels = new HashMap<>();

        try {

            for (Map.Entry<Integer, YextTimePeriod> yextOpenHoursEntry : yextOpenHours.getOpenHours().entrySet()) {
                if (yextOpenHoursEntry.getValue().isClosed()) {
                    continue;
                }
                LocationScheduleModel daySchedule = new LocationScheduleModel(yextOpenHoursEntry.getKey());
                daySchedule.setOpens(yextOpenHoursEntry.getValue().getStartTime());
                daySchedule.setCloses(yextOpenHoursEntry.getValue().getFinishTime());
                locationScheduleModels.put(yextOpenHoursEntry.getKey(), daySchedule);
            }
            fillMissingDays(locationScheduleModels);

            LocationScheduleModel holidays = new LocationScheduleModel(LocationScheduleModel.WEEK_DAY_HOLIDAYS);
            locationScheduleModels.put(LocationScheduleModel.WEEK_DAY_HOLIDAYS, holidays);

        } catch (RuntimeException e) {
            Log.e(TAG, new LogErrorException("Error parsing yext hours: " + yextLocation.getLocationName(), e));
        }

        return locationScheduleModels;
    }

    private void fillMissingDays(Map<Integer, LocationScheduleModel> locationScheduleModels) {
        for (int weekDayIndex = DateTimeConstants.MONDAY; weekDayIndex <= DateTimeConstants.SUNDAY; weekDayIndex++) {
            if (locationScheduleModels.get(weekDayIndex) == null) {
                LocationScheduleModel daySchedule = new LocationScheduleModel(weekDayIndex);
                locationScheduleModels.put(weekDayIndex, daySchedule);
            }
        }
    }

    private void loadDeliveryData(YextLocation yextLocation) {
        mDeliveryFee = parseBigDecimal(yextLocation.getDeliveryFee());
        mDeliveryMinimum = parseBigDecimal(yextLocation.getMinimumDeliveryAmount());
        mDeliveryRadius = parseBigDecimal(yextLocation.getDeliveryRadius());
    }

    private BigDecimal parseBigDecimal(String stringToParse) {
        try {
            if (stringToParse != null && !stringToParse.isEmpty()) {
                return new BigDecimal(stringToParse);
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, new LogErrorException("Server sending malformatted BigDecimal : " + e.getMessage()));
        }
        return null;
    }

    private String fillHourTimePart(String sHour) {
        return sHour.length() == 1 ? "0" + sHour : sHour;
    }

    @Bindable
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getAddressShort() {
        return mAddressShort;
    }

    public void setAddressShort(String addressShort) {
        mAddressShort = addressShort;
        notifyPropertyChanged(BR.addressShort);
    }

    @Bindable
    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
        notifyPropertyChanged(BR.address);
    }

    @Bindable
    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
        notifyPropertyChanged(BR.phone);
    }


    @Bindable
    public Double getDistanceInMiles() {
        return mDistanceInMiles;
    }

    public void setDistanceInMiles(Double distanceInMiles) {
        mDistanceInMiles = distanceInMiles;
        notifyPropertyChanged(BR.distanceInMiles);
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

    @Bindable
    public double[] getCoordinates() {
        return new double[]{mLatitude, mLongitude};
    }

    public Map<Integer, LocationScheduleModel> getOpenHourSchedule() {
        return mOpenHourSchedule;
    }

    public void setOpenHourSchedule(Map<Integer, LocationScheduleModel> openHourSchedule) {
        mOpenHourSchedule = openHourSchedule;
    }

    public String getLocatity() {
        return mLocality;
    }

    public void setLocatity(String locatity) {
        mLocality = locatity;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public List<StoreFeature> getFeatures() {
        return mFeatures;
    }

    public void setFeatures(List<StoreFeature> features) {
        mFeatures = features;
    }

    public boolean isComingSoon() {
        return mComingSoon;
    }

    public void setComingSoon(boolean comingSoon) {
        mComingSoon = comingSoon;
    }

    public String getZipcode() {
        return mZipcode;
    }

    public void setZipcode(String zipcode) {
        mZipcode = zipcode;
    }

    @Bindable
    public boolean isOrderOutOfStore() {
        return mFeatures.contains(StoreFeature.ORDER_OUT_OF_STORE);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }


    public String getPreparationTime() {
        return mPreparationTime;
    }

    public void setPreparationTime(String preparationTime) {
        mPreparationTime = preparationTime;
    }

    public List<YextDate> getHolidayHours() {
        return mHolidayHours;
    }

    public void setHolidayHours(List<YextDate> holidayHours) {
        mHolidayHours = holidayHours;
    }

    public boolean isRestrictedAccessLocation() {
        return mRestrictedAccessLocation;
    }

    public void setRestrictedAccessLocation(boolean restrictedAccessLocation) {
        mRestrictedAccessLocation = restrictedAccessLocation;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StoreLocation)) {
            return false;
        }

        StoreLocation storeLocation = (StoreLocation) obj;
        return mId.equals(storeLocation.mId);
    }

    @Override
    public int hashCode() {
        return mId.hashCode();
    }

    /**
     * Returns the closing time for the given store today
     *
     * @param clock
     * @return
     */
    public LocalTime getClosingTime(Clock clock) {
        LocationScheduleModel locationScheduleModel = StoreHoursCheckUtil.getStoreOpenHoursScheduleForToday(clock, this);
        return locationScheduleModel == null ? null : locationScheduleModel.isClosed() ? null : locationScheduleModel.getCloses();
    }

    public boolean isClosed() {
        return mIsClosed;
    }

    public void setClosed(boolean closed) {
        mIsClosed = closed;
    }

    public boolean isOrderAheadTempOff() {
        return mOrderAheadTempOff;
    }

    public void setOrderAheadTempOff(boolean orderAheadTempOff) {
        mOrderAheadTempOff = orderAheadTempOff;
    }

    public boolean enoughTimeForBulkOrder(Clock clock, int bulkPrepTime, int minuteStoreBeforeClosing) {
        return clock.getCurrentTime().plusMinutes(bulkPrepTime)
                .plusMinutes(minuteStoreBeforeClosing).isBefore(this.getClosingTime(clock));
    }

    public List<YextPickupType> getPickupTypes() {
        return mPickupTypes;
    }

    public BigDecimal getDeliveryFee() {
        return mDeliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        mDeliveryFee = deliveryFee;
    }

    public BigDecimal getDeliveryMinimum() {
        return mDeliveryMinimum;
    }

    public void setDeliveryMinimum(BigDecimal deliveryMinimum) {
        mDeliveryMinimum = deliveryMinimum;
    }

    public BigDecimal getDeliveryRadius() {
        return mDeliveryRadius;
    }

    public void setDeliveryRadius(BigDecimal deliveryRadius) {
        mDeliveryRadius = deliveryRadius;
    }

    public Map<Integer, LocationScheduleModel> getDeliveryHoursSchedule() {
        return mDeliveryHoursSchedule;
    }

    public void setDeliveryHoursSchedule(Map<Integer, LocationScheduleModel> deliveryHoursSchedule) {
        mDeliveryHoursSchedule = deliveryHoursSchedule;
    }

    public List<YextDate> getDeliveryHolidayHours() {
        return mDeliveryHolidayHours;
    }

    public void setDeliveryHolidayHours(List<YextDate> deliveryHolidayHours) {
        mDeliveryHolidayHours = deliveryHolidayHours;
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

    public boolean isGuestCheckoutEnabled() {
        return mGuestCheckoutEnabled;
    }

    public void setGuestCheckoutEnabled(boolean guestCheckoutEnabled) {
        mGuestCheckoutEnabled = guestCheckoutEnabled;
    }

    public String getmTimeZone() {
        return mTimeZone;
    }

    public void setmTimeZone(String mTimeZone) {
        this.mTimeZone = mTimeZone;
    }
}
