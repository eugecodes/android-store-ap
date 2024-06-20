package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation

import caribouapp.caribou.com.cariboucoffee.common.CCInformationModel
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowContract
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.CheckoutModel
import caribouapp.caribou.com.cariboucoffee.order.Order
import java.math.BigDecimal

/**
 * Created by Swapnil on 09/23/22.
 */
interface NewCardPaymentContract {
    interface View : OOSFlowContract.View {
        fun cvvUnknownErrorEnabled(enabled: Boolean): Boolean
        fun cardNumberErrorEnabled(enabled: Boolean): Boolean
        fun validCardNumberErrorEnabled(enabled: Boolean): Boolean
        fun nameErrorEnabled(enabled: Boolean): Boolean
        fun expirationMonthErrorEnabled(enabled: Boolean): Boolean
        fun expirationYearErrorEnabled(enabled: Boolean): Boolean
        fun monthExpiredErrorEnabled(enabled: Boolean): Boolean
        fun yearExpiredErrorEnabled(enabled: Boolean): Boolean
        fun cvvErrorEnabled(enabled: Boolean): Boolean
        fun validCvvErrorEnabled(enabled: Boolean): Boolean
        fun billingAddressErrorEnabled(enabled: Boolean): Boolean
        fun zipErrorEnabled(enabled: Boolean): Boolean
        fun emptyZipErrorEnabled(enabled: Boolean): Boolean
        fun cityErrorEnabled(enabled: Boolean): Boolean
        fun stateErrorEnabled(enabled: Boolean): Boolean
        fun cardTypeUnknownErrorEnabled(enabled: Boolean): Boolean
        fun cardDigitNumberErrorEnabled(enabled: Boolean): Boolean
        fun setModel(model: CCInformationModel?)
        fun updateNameOnCard(name: String)
        fun showNotValidSelectedPickupTime()
        fun showStoreClosedDialog()
        fun showStoreNearClosingForBulk()
        fun showDeliveryClosedDialog()
        fun showDeliveryMinimumNotMetDialog(deliveryMinimum: BigDecimal?)
        fun showChooseANewPickUpTimeDialog()
        fun showFailToPlaceOrderDialog()
        fun goToConfirmation(orderData: Order<*>?)
        fun showErrorDialog(errorMessage: String)
        fun showCommonErrorDialog(errorMessage: String)
        fun getCurrentYear(): Int
        fun getCurrentMonth(): Int
    }

    interface Presenter : OOSFlowContract.Presenter {
        fun setModel(model: CCInformationModel?)
        fun placeOrder(checkoutModel: CheckoutModel?)
        fun isAllBillingFieldsShow(): Boolean
        fun getGuestDetails()
        fun cancelOrder()
    }
}
