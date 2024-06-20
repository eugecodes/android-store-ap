package caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.BR;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;
import caribouapp.caribou.com.cariboucoffee.order.Order;

public class PickupModel extends BaseObservable implements Serializable {
    private Order mOrder;

    private YextPickupType mSelectedPickupType;

    private String mSelectedCarType;

    private String mSelectedCarColor;

    private String mSelectedCarMake;

    private List<String> carTypesOptions;

    private List<String> carColorsOptions;

    private DeliveryModel mDeliveryModel = new DeliveryModel();

    private BigDecimal mDelvieryFee;

    private BigDecimal mDeliveryRadius;

    private BigDecimal mDeliveryMinimum;

    private boolean mFirstAttempt = true;

    private boolean mDeliveryEnabled = true;

    private boolean mCarMakeEnabled;

    private String pickupCurbsideTipMessage;

    @Bindable
    private String mCurbsideDescription;

    @Bindable
    private String mWalkInDescription;

    @Bindable
    private String mDriveThruDescription;

    @Bindable
    private String mDeliveryDescription;


    @Bindable
    public Order getOrder() {
        return mOrder;
    }

    public void setOrder(Order order) {
        mOrder = order;
        notifyPropertyChanged(BR.order);
    }

    public List<String> getCarTypesOptions() {
        return carTypesOptions;
    }

    public void setCarTypesOptions(List<String> carTypesOptions) {
        this.carTypesOptions = carTypesOptions;
    }

    public List<String> getCarColorsOptions() {
        return carColorsOptions;
    }

    public void setCarColorsOptions(List<String> carColorsOptions) {
        this.carColorsOptions = carColorsOptions;
    }

    @Bindable
    public YextPickupType getSelectedPickupType() {
        return mSelectedPickupType;
    }

    public void setSelectedPickupType(YextPickupType selectedPickupType) {
        mSelectedPickupType = selectedPickupType;
        notifyPropertyChanged(BR.selectedPickupType);
    }

    public String getSelectedCarType() {
        return mSelectedCarType;
    }

    public void setSelectedCarType(String selectedCarType) {
        mSelectedCarType = selectedCarType;
    }

    public String getSelectedCarColor() {
        return mSelectedCarColor;
    }

    public void setSelectedCarColor(String selectedCarColor) {
        mSelectedCarColor = selectedCarColor;
    }

    @Bindable
    public DeliveryModel getDeliveryModel() {
        return mDeliveryModel;
    }

    public void setDeliveryModel(DeliveryModel deliveryModel) {
        mDeliveryModel = deliveryModel;
        notifyPropertyChanged(BR.deliveryModel);
    }

    public BigDecimal getDelvieryFee() {
        return mDelvieryFee;
    }

    public void setDelvieryFee(BigDecimal delvieryFee) {
        mDelvieryFee = delvieryFee;
    }

    public BigDecimal getDeliveryMaxDistance() {
        return mDeliveryRadius;
    }

    public void setDeliveryRadius(BigDecimal deliveryRadius) {
        mDeliveryRadius = deliveryRadius;
    }

    public BigDecimal getDeliveryMinimum() {
        return mDeliveryMinimum;
    }

    public void setDeliveryMinimum(BigDecimal deliveryMinimum) {
        mDeliveryMinimum = deliveryMinimum;
    }

    public boolean isFirstAttempt() {
        return mFirstAttempt;
    }

    public void setFirstAttempt(boolean firstAttempt) {
        mFirstAttempt = firstAttempt;
    }

    public boolean isFromCheckout() {
        return !mOrder.getItems().isEmpty();
    }

    public boolean isPickupTypeEnabled(YextPickupType yextPickupType) {
        return yextPickupType != YextPickupType.Delivery || mDeliveryEnabled;
    }

    public String getSelectedCarMake() {
        return mSelectedCarMake;
    }

    public void setSelectedCarMake(String selectedCarMake) {
        mSelectedCarMake = selectedCarMake;
    }

    public void setDeliveryEnabled(boolean enabled) {
        mDeliveryEnabled = enabled;
    }

    public boolean isDeliveryEnabled() {
        return mDeliveryEnabled;
    }

    public boolean isCarMakeEnabled() {
        return mCarMakeEnabled;
    }

    public void setCarMakeEnabled(boolean carMakeEnabled) {
        mCarMakeEnabled = carMakeEnabled;
    }

    public String getCurbsideDescription() {
        return mCurbsideDescription;
    }

    public void setCurbsideDescription(String curbsideDescription) {
        mCurbsideDescription = curbsideDescription;
        notifyPropertyChanged(BR.curbsideDescription);
    }

    public String getWalkInDescription() {
        return mWalkInDescription;
    }

    public void setWalkInDescription(String walkInDescription) {
        mWalkInDescription = walkInDescription;
        notifyPropertyChanged(BR.walkInDescription);
    }

    public String getDriveThruDescription() {
        return mDriveThruDescription;
    }

    public void setDriveThruDescription(String driveThruDescription) {
        mDriveThruDescription = driveThruDescription;
        notifyPropertyChanged(BR.driveThruDescription);
    }

    public String getDeliveryDescription() {
        return mDeliveryDescription;
    }

    public void setDeliveryDescription(String deliveryDescription) {
        mDeliveryDescription = deliveryDescription;
        notifyPropertyChanged(BR.deliveryDescription);
    }

    public String getPickupCurbsideTipMessage() {
        return pickupCurbsideTipMessage;
    }

    public void setPickupCurbsideTipMessage(String pickupCurbsideTipMessage) {
        this.pickupCurbsideTipMessage = pickupCurbsideTipMessage;
    }

}
