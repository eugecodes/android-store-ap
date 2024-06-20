package caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout;

import java.math.BigDecimal;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.CheckoutModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.PickUpTimeModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.TippingOption;
import caribouapp.caribou.com.cariboucoffee.order.Order;

/**
 * Created by gonzalogelos on 4/5/18.
 */

public interface OrderCheckoutContract {

    interface View extends OOSFlowContract.View {

        void anchorViewToBannerOrTop();

        void anchorViewToReward();

        void updateMoneyBalance(boolean enoughBalanceMoney, BigDecimal balanceMoney);

        void showCloseDialog();

        void goToAddFunds(BigDecimal amountNeededToAdd);

        void displayOrderData(Order orderData);

        void goToConfirmation(Order orderData);

        void showFailToPlaceOrderDialog();

        void showNationalOutageDialog(String title, String message);

        void showStoreClosedDialog();

        void showPickUpTimesDialog(List<PickUpTimeModel> pickUpTimes);

        void viewOnMap();

        void showChooseANewPickUpTimeDialog();

        void hideRewardErrorBanner();

        void setShowPickupTimeField(boolean show);

        void setShowPickupLocationField(boolean show);

        void showAsapNotAvailable();

        void showStoreNearClosingForBulk();

        void showPickupTypeScreen();

        void setDeliveryMessage(String pickupDeliveryPrepMessage);

        void setPickupCurbsideTipMessage(String pickupCurbsideMessage);

        void showDeliveryMinimumNotMetDialog(BigDecimal deliveryMinimum);

        void showDeliveryClosedDialog();

        void showNotValidSelectedPickupTime();

        void showCustomTipDialog(TippingOption selectedTipOption, BigDecimal orderTotal);

        void showTipping();

        void setupTippingOptions(List<TippingOption> tippingOptions);

        void showChosenTip(TippingOption tippingOption, BigDecimal tip);
    }

    interface Presenter extends OOSFlowContract.Presenter {

        void init();

        void loadPerkPoints();

        void checkEnoughFunds();

        void addFundsClicked();

        void placeOrder();

        void cancelOrder();

        void editPickupTime();

        void addedFunds();

        void addAvailableReward();

        void removeReward();

        void loadBanner();

        void editPickupType();

        void selectCustomTip();

        void setTippingOption(TippingOption tippingOption);

        boolean isThisGuestFlow();

        boolean isContinueAsGuestCheckEnabled();

        CheckoutModel getCheckoutModel();

        void displayNationalOutageDialog();
    }
}
