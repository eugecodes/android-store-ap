package caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.presenter;

import androidx.databinding.Observable;

import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.BR;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.CurbsidePickupData;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.GeolocationServices;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.PickupContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.model.DeliveryModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.model.PickupModel;
import caribouapp.caribou.com.cariboucoffee.order.DeliveryData;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.order.PickupData;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.StoreHoursCheckUtil;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import caribouapp.caribou.com.cariboucoffee.util.ValidationUtils;

public class PickupPresenter extends OOSFlowPresenter<PickupContract.View> implements PickupContract.Presenter {

    private static final String TAG = PickupPresenter.class.getSimpleName();
    private static final String OPTION_OTHER = "other";

    private PickupModel mModel;
    @Inject
    SettingsServices mSettingsServices;
    @Inject
    GeolocationServices mGeolocationServices;
    @Inject
    UserServices mUserServices;
    @Inject
    Clock mClock;

    private Observable.OnPropertyChangedCallback mOnPropertyChangedCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (getView() == null || mModel.isFirstAttempt()) {
                return;
            }

            if (propertyId == BR.addressLine1) {
                getView().addressLine1ErrorEnabled(!checkAddressLine1());
            } else if (propertyId == BR.zipCode) {
                getView().zipErrorEnabled(!checkZipCode());
            } else if (propertyId == BR.contactPhoneNumber) {
                getView().deliveryPhoneNumberErrorEnabled(!checkDeliveryPhoneNumber());
            }
        }
    };

    public PickupPresenter(PickupContract.View view, PickupModel model) {
        super(view);
        mModel = model;
        mModel.getDeliveryModel().addOnPropertyChangedCallback(mOnPropertyChangedCallback);
    }

    @Override
    protected void setOrder(Order order) {
        mModel.setCarColorsOptions(mSettingsServices.getPickupCurbsideCarColors());
        mModel.setCarTypesOptions(mSettingsServices.getPickupCurbsideCarTypes());
        setPickupTypeDescriptions();
        mModel.setOrder(order);

        getView().setCurbsideTipMessage(mSettingsServices.getPickupCurbsideTipMessage());
        String carMakeString = mSettingsServices.getCurbsideCarMakesOptions();
        mModel.setCarMakeEnabled(carMakeString != null && !carMakeString.isEmpty());
        List<String> carMakeOption = carMakeString != null ? new ArrayList(Arrays.asList(carMakeString.split(","))) : null;
        sortCarMakeOption(carMakeOption);
        getView().updateCarOptions(mModel.getCarTypesOptions(), mModel.getCarColorsOptions(), carMakeOption, mUserServices.getCurbSidePickupData());

        loadAvailablePickupTypes(order);
        loadPickupData(order);

        mModel.setDeliveryEnabled(isDeliveryOpen(order.getStoreLocation()));
        mModel.setDeliveryMinimum(order.getStoreLocation().getDeliveryMinimum());
        mModel.setDeliveryRadius(order.getStoreLocation().getDeliveryRadius());
        mModel.setDelvieryFee(order.getStoreLocation().getDeliveryFee());

        getView().updatePickupData(mModel);
    }

    private void setPickupTypeDescriptions() {
        mModel.setCurbsideDescription(mSettingsServices.getCurbSidePickupDescription());
        mModel.setPickupCurbsideTipMessage(mSettingsServices.getPickupCurbsideTipMessage());
        mModel.setWalkInDescription(mSettingsServices.getWalkInPickupDescription());
        mModel.setDriveThruDescription(mSettingsServices.getDriveThruPickupDescription());
        mModel.setDeliveryDescription(mSettingsServices.getDeliveryPickupDescription());
    }

    /**
     * Sort known Car Make options alphabetically and puts 'Other' option at last.
     */
    private void sortCarMakeOption(List<String> carMakeOption) {
        if (carMakeOption == null) {
            return;
        }
        Collections.sort(carMakeOption, (carMake1, carMake2) -> {
            if (OPTION_OTHER.equalsIgnoreCase(carMake1)) {
                return 1;
            }
            if (OPTION_OTHER.equalsIgnoreCase(carMake2)) {
                return -1;
            }
            return carMake1.compareTo(carMake2);
        });
    }

    private void loadAvailablePickupTypes(Order order) {
        List<YextPickupType> pickupTypes = order.getStoreLocation().getPickupTypes();
        getView().showPickupTypes(pickupTypes);

        if (!isDeliveryOpen(order.getStoreLocation())) {
            Log.d(TAG, "Disabling delivery pickup type. Store is closed for delivery.");
            getView().disablePickupType(YextPickupType.Delivery);
        }
    }

    private boolean isDeliveryOpen(StoreLocation storeLocation) {
        List<YextPickupType> pickupTypes = storeLocation.getPickupTypes();
        return pickupTypes.contains(YextPickupType.Delivery) && StoreHoursCheckUtil.isDeliveryOpen(mClock, storeLocation);
    }

    private void loadPickupData(Order order) {
        PickupData pickupData = order.getPickupData();
        loadDefaults();

        if (pickupData == null) {
            return;
        }

        YextPickupType yextPickupType = pickupData.getYextPickupType();
        mModel.setSelectedPickupType(yextPickupType);

        if (yextPickupType == YextPickupType.Curbside) {
            CurbsidePickupData curbsidePickupData = pickupData.getCurbsidePickupData();
            mModel.setSelectedCarType(curbsidePickupData.getCarType());
            mModel.setSelectedCarColor(curbsidePickupData.getCarColor());
            mModel.setSelectedCarMake(curbsidePickupData.getCarMake());
        } else if (yextPickupType == YextPickupType.Delivery) {
            DeliveryData deliveryData = mModel.getOrder().getDeliveryData();
            if (deliveryData != null) {
                mModel.getDeliveryModel().loadFromDeliveryData(deliveryData);
            }
        }
    }

    private void loadDefaults() {
        CurbsidePickupData curbsidePickupData = mUserServices.getCurbSidePickupData();
        if (curbsidePickupData != null) {
            mModel.setSelectedCarColor(curbsidePickupData.getCarColor());
            mModel.setSelectedCarType(curbsidePickupData.getCarType());
            mModel.setSelectedCarMake(curbsidePickupData.getCarMake());
        }
        DeliveryModel deliveryModel = mModel.getDeliveryModel();
        if (mUserServices.getDeliveryAddressLine1() == null) {
            deliveryModel.setZipCode(mUserServices.getZip());
            deliveryModel.setContactPhoneNumber(mUserServices.getPhoneNumber());
        } else {
            deliveryModel.loadFromUserServices(mUserServices);
        }
    }

    @Override
    public boolean isPickupTypeEnabled(YextPickupType yextPickupType) {
        return mModel.isPickupTypeEnabled(yextPickupType);
    }

    @Override
    public void setPickupType(YextPickupType yextPickupType) {
        mModel.setSelectedPickupType(yextPickupType);
        getView().updatePickupType(yextPickupType);
    }

    @Override
    public void setCarColor(String carColor) {
        mModel.setSelectedCarColor(carColor);
    }

    @Override
    public void setCarType(String carType) {
        mModel.setSelectedCarType(carType);
    }

    private void continueOrder() {
        YextPickupType previousType =
                mModel.getOrder().getPickupData() == null
                        ? null : mModel.getOrder().getPickupData().getYextPickupType();

        PickupData pickupData = new PickupData();
        pickupData.setYextPickupType(mModel.getSelectedPickupType());
        mModel.getOrder().setPickupData(pickupData);
        mModel.getOrder().setDeliveryData(null);

        if (mModel.getSelectedPickupType() == YextPickupType.Curbside) {
            CurbsidePickupData curbsidePickupData =
                    new CurbsidePickupData(
                            mModel.getSelectedCarMake(),
                            mModel.getSelectedCarColor(),
                            mModel.getSelectedCarType());
            pickupData.setCurbsidePickupData(curbsidePickupData);
            mUserServices.saveCurbsidePickupData(curbsidePickupData);
        } else if (mModel.getSelectedPickupType() == YextPickupType.Delivery) {
            mModel.getOrder().setDeliveryData(mModel.getDeliveryModel().toDeliveryData());
            mModel.getDeliveryModel().saveTo(mUserServices);
        }
        getView().navigateToNextOrderScreen(requiresBounce(previousType));
    }

    private boolean requiresBounce(YextPickupType previousType) {
        YextPickupType newPickupType = mModel.getOrder().getPickupData().getYextPickupType();
        return previousType != newPickupType && (previousType == YextPickupType.Delivery
                || newPickupType == YextPickupType.Delivery);
    }

    private String getAddressForGeolocation() {
        DeliveryModel deliveryModel = mModel.getDeliveryModel();
        return deliveryModel.getAddressLine1() + "," + deliveryModel.getZipCode();
    }

    @Override
    public void validateAndContinue() {
        mModel.setFirstAttempt(false);

        if (mModel.getSelectedPickupType() == null) {
            getView().showMessage(R.string.choose_pickup_option);
            return;
        }

        if (mModel.getSelectedPickupType() == YextPickupType.Delivery) {
            validateDeliveryAndContinue();
            return;
        } else if (mModel.getSelectedPickupType() == YextPickupType.Curbside) {
            validateCurbsideAndContinue();
            return;
        }

        continueOrder();
    }

    @Override
    public void setCarMake(String carMakeSelected) {
        mModel.setSelectedCarMake(carMakeSelected);
    }

    private void validateCurbsideAndContinue() {
        boolean errorFound = getView().carTypeErrorEnable(mModel.getSelectedCarType() == null)
                | getView().carColorErrorEnable(mModel.getSelectedCarColor() == null)
                | getView().carMakeErrorEnable(mModel.getSelectedCarMake() == null && mModel.isCarMakeEnabled());

        if (errorFound) {
            return;
        }

        continueOrder();
    }

    private void validateDeliveryAndContinue() {

        if (!isDeliveryOpen(mModel.getOrder().getStoreLocation())) {
            getView().showContinueNotAvailableDialog();
            return;
        }

        boolean errorFound = getView().addressLine1ErrorEnabled(!checkAddressLine1())
                | getView().zipErrorEnabled(!checkZipCode())
                | getView().deliveryPhoneNumberErrorEnabled(!checkDeliveryPhoneNumber());

        if (errorFound) {
            return;
        }

        StoreLocation storeLocation = mModel.getOrder().getStoreLocation();
        LatLng storeLatLng = new LatLng(storeLocation.getLatitude(), storeLocation.getLongitude());
        mGeolocationServices.distanceBetween(storeLatLng, getAddressForGeolocation(), new BaseViewResultCallback<BigDecimal>(getView()) {
            @Override
            protected void onSuccessViewUpdates(BigDecimal data) {
                if (mModel.getDeliveryMaxDistance().compareTo(data) >= 0) {
                    continueOrder();
                } else {
                    getView().showOutOfDeliveryRangeDialog(mModel.getDeliveryMaxDistance());
                }
            }

            @Override
            protected void onFailView(int errorCode, String errorMessage) {
                getView().showMessage(R.string.invalid_address);
            }
        });
    }

    private boolean checkAddressLine1() {
        return !StringUtils.isEmpty(mModel.getDeliveryModel().getAddressLine1());
    }

    private boolean checkZipCode() {
        return ValidationUtils.isValidZipCode(mModel.getDeliveryModel().getZipCode());
    }

    private boolean checkDeliveryPhoneNumber() {
        return ValidationUtils.isValidPhoneNumber(mModel.getDeliveryModel().getContactPhoneNumber());
    }

    private String getPhoneWithoutSymbols() {
        return StringUtils.toPhoneNumberWithoutSymbols(mModel.getDeliveryModel().getContactPhoneNumber());
    }

    @Override
    public void detachView() {
        mModel.getDeliveryModel().removeOnPropertyChangedCallback(mOnPropertyChangedCallback);
        super.detachView();
    }

    public UserServices getUserServices() {
        return mUserServices;
    }

    public void setUserServices(UserServices userServices) {
        mUserServices = userServices;
    }

    public void setGeolocationServices(GeolocationServices geolocationServices) {
        mGeolocationServices = geolocationServices;
    }

    public void setClock(Clock clock) {
        mClock = clock;
    }

    public void setSettingsServices(SettingsServices settingsServices) {
        mSettingsServices = settingsServices;
    }

    @Override
    protected boolean shouldCheckForOrderDeletion() {
        return true;
    }
}
