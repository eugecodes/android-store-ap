package caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup;

import java.math.BigDecimal;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.CurbsidePickupData;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.model.PickupModel;

public interface PickupContract {
    interface View extends OOSFlowContract.View {

        void navigateToNextOrderScreen(boolean requiresBounce);

        void updateCarOptions(List<String> carTypes, List<String> carColors, List<String> carMakeOptions, CurbsidePickupData curbsidePickupData);

        void updatePickupType(YextPickupType yextPickupType);

        void showPickupTypes(List<YextPickupType> pickupTypes);

        void disablePickupType(YextPickupType yextPickupType);

        void updatePickupData(PickupModel pickupModel);

        boolean addressLine1ErrorEnabled(boolean enabled);

        boolean zipErrorEnabled(boolean enabled);

        boolean deliveryPhoneNumberErrorEnabled(boolean enabled);

        void showOutOfDeliveryRangeDialog(BigDecimal distanceResultInMiles);

        void showContinueNotAvailableDialog();

        void setCurbsideTipMessage(String curbsideTipMessage);

        boolean carMakeErrorEnable(boolean enable);

        boolean carTypeErrorEnable(boolean enable);

        boolean carColorErrorEnable(boolean enable);
    }

    interface Presenter extends OOSFlowContract.Presenter {

        boolean isPickupTypeEnabled(YextPickupType yextPickupType);

        void setPickupType(YextPickupType yextPickupType);

        void setCarColor(String carColor);

        void setCarType(String carType);

        void validateAndContinue();

        void setCarMake(String carMakeSelected);
    }
}
