package caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.io.Serializable;

import caribouapp.caribou.com.cariboucoffee.BR;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.order.DeliveryData;

public class DeliveryModel extends BaseObservable implements Serializable {
    private String mAddressLine1;
    private String mAddressLine2;
    private String mZipCode;
    private String mContactPhoneNumber;
    private String mInstructions;

    @Bindable
    public String getAddressLine1() {
        return mAddressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        mAddressLine1 = addressLine1;
        notifyPropertyChanged(BR.addressLine1);
    }

    @Bindable
    public String getAddressLine2() {
        return mAddressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        mAddressLine2 = addressLine2;
        notifyPropertyChanged(BR.addressLine2);
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
    public String getContactPhoneNumber() {
        return mContactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        mContactPhoneNumber = contactPhoneNumber;
        notifyPropertyChanged(BR.contactPhoneNumber);
    }

    @Bindable
    public String getInstructions() {
        return mInstructions;
    }

    public void setInstructions(String instructions) {
        mInstructions = instructions;
        notifyPropertyChanged(BR.instructions);
    }

    public void loadFromDeliveryData(DeliveryData deliveryData) {
        mAddressLine1 = deliveryData.getAddressLine1();
        mAddressLine2 = deliveryData.getAddressLine2();
        mZipCode = deliveryData.getZipCode();
        mContactPhoneNumber = deliveryData.getContact();
        mInstructions = deliveryData.getInstructions();
    }

    public void loadFromUserServices(UserServices userServices) {
        mAddressLine1 = userServices.getDeliveryAddressLine1();
        mAddressLine2 = userServices.getDeliveryAddressLine2();
        mZipCode = userServices.getDeliveryZipCode();
        mContactPhoneNumber = userServices.getDeliveryContact();
        mInstructions = userServices.getDeliveryInstructions();
    }

    public DeliveryData toDeliveryData() {
        DeliveryData deliveryData = new DeliveryData();
        deliveryData.setAddressLine1(mAddressLine1);
        deliveryData.setAddressLine2(mAddressLine2);
        deliveryData.setZipCode(mZipCode);
        deliveryData.setContact(mContactPhoneNumber);
        deliveryData.setInstructions(mInstructions);
        return deliveryData;
    }

    public void saveTo(UserServices userServices) {
        userServices.setDeliveryAddressLine1(mAddressLine1);
        userServices.setDeliveryAddressLine2(mAddressLine2);
        userServices.setDeliveryZipCode(mZipCode);
        userServices.setDeliveryContact(mContactPhoneNumber);
        userServices.setDeliveryInstructions(mInstructions);
    }
}
