package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation

import caribouapp.caribou.com.cariboucoffee.fiserv.model.FiservAnonResponse
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowContract
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.CheckoutModel
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.model.PaymentTypeData
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.view.PaymentTypeSelectionActivity
import caribouapp.caribou.com.cariboucoffee.order.Order
import com.braintreepayments.api.PayPalClient
import com.braintreepayments.api.VenmoClient
import com.google.android.gms.wallet.PaymentsClient

/**
 * Created by Swapnil Kshirsagar on 03/15/2023.
 */
interface PaymentSelectionContract {
    interface View : OOSFlowContract.View {
        fun hideGooglePayButton()
        fun showGooglePayButton()
        fun setVenmoButtonVisibility(isVisible: Boolean)
        fun showErrorDialog(errorMessage: String)
        fun getAccountTokenResponse(data: FiservAnonResponse?)
        fun goToConfirmation(orderData: Order<*>?)
    }

    interface Presenter : OOSFlowContract.Presenter {
        val payPalClient: PayPalClient
        val venmoClient: VenmoClient

        val isGooglePayEnabled: Boolean
        val isVenmoEnabled: Boolean
        fun isPayPalEnabled(): Boolean
        fun isPaymentSelectionScreenEnabled(): Boolean
        fun initiatePaymentsClient(paymentTypeSelectionActivity: PaymentTypeSelectionActivity)
        fun getPaymentsClient(): PaymentsClient
        fun getAccountToken()
        fun callPlaceOrderService(payMethodType: String, checkOutModel: CheckoutModel?)
        suspend fun callSaleRequest(checkOutModel: CheckoutModel, fiservTokenId: String, paymentData: PaymentTypeData)
        fun initBraintreeClient(paymentTypeSelectionActivity: PaymentTypeSelectionActivity)
    }
}

