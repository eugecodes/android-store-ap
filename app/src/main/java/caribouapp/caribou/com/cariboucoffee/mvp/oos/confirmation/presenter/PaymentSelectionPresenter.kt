package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.presenter

import caribouapp.caribou.com.cariboucoffee.AppConstants
import caribouapp.caribou.com.cariboucoffee.BuildConfig
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewResultCallback
import caribouapp.caribou.com.cariboucoffee.domain.AuthorizeSaleResult
import caribouapp.caribou.com.cariboucoffee.domain.AuthorizeSaleUseCase
import caribouapp.caribou.com.cariboucoffee.fiserv.api.PayGateService
import caribouapp.caribou.com.cariboucoffee.fiserv.model.*
import caribouapp.caribou.com.cariboucoffee.googlepay.PaymentsUtil
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowPresenter
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.CheckoutModel
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.PaymentSelectionContract
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.model.PaymentTypeData
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.view.PaymentTypeSelectionActivity
import caribouapp.caribou.com.cariboucoffee.order.Order
import caribouapp.caribou.com.cariboucoffee.util.Log
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException
import caribouapp.caribou.com.cariboucoffee.util.StringUtils
import com.braintreepayments.api.BraintreeClient
import com.braintreepayments.api.PayPalClient
import com.braintreepayments.api.VenmoClient
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentsClient
import java.security.interfaces.RSAPublicKey
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by Swapnil Kshirsagar on 03/15/2023.
 */
class PaymentSelectionPresenter(
    view: PaymentSelectionContract.View?
) : OOSFlowPresenter<PaymentSelectionContract.View?>(view), PaymentSelectionContract.Presenter {
    @JvmField
    @Inject
    var mSettingsServices: SettingsServices? = null

    @JvmField
    @Inject
    var mPayGateService: PayGateService? = null

    @JvmField
    @Inject
    @Named("PayGatePublicKey")
    var mPayGatePubKey: RSAPublicKey? = null

    @Inject
    lateinit var authorizeSaleUseCase: AuthorizeSaleUseCase

    private lateinit var paymentsClient: PaymentsClient
    private lateinit var braintreeClient: BraintreeClient
    override lateinit var payPalClient: PayPalClient
    override lateinit var venmoClient: VenmoClient
    companion object {
        private val TAG = PaymentTypeSelectionActivity::class.java.simpleName
    }

    override var isVenmoEnabled: Boolean = false
        get() = field && mSettingsServices!!.isVenmoEnabled

    override var isGooglePayEnabled: Boolean = false
        get() = field && mSettingsServices!!.isGooglePayEnabled

    override fun isPayPalEnabled(): Boolean {
        return mSettingsServices!!.isPayPalEnabled
    }

    override fun isPaymentSelectionScreenEnabled(): Boolean {
        return mSettingsServices!!.isPaymentSelectionScreenEnabled
    }

    override fun initiatePaymentsClient(paymentTypeSelectionActivity: PaymentTypeSelectionActivity) {
        paymentsClient = PaymentsUtil.createPaymentsClient(paymentTypeSelectionActivity)
        possiblyShowGooglePayButton()
    }

    private fun possiblyShowGooglePayButton() {
        // We are showing the Google Pay button if Google service is up and running as per
        // documentation of Google https://developers.google.com/pay/api/android/guides/tutorial
        val isReadyToPayJson = PaymentsUtil.isReadyToPayRequest() ?: return
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString())
        val task = paymentsClient.isReadyToPay(request)
        task.addOnCompleteListener { completedTask ->
            if (completedTask.isSuccessful) {
                completedTask.result?.let(::setGooglePayAvailable)
            } else {
                view!!.hideLoadingLayer()
                val exception = completedTask.exception
                exception?.message?.let { view!!.showErrorDialog(it) }
                Log.e("possiblyShowGooglePayButton", LogErrorException("Error: isReadyToPay failed $exception"))
            }
        }
    }

    override fun getPaymentsClient(): PaymentsClient {
        return paymentsClient
    }

    override fun getAccountToken() {
        view!!.showLoadingLayer()
        callAcquireToken()
    }

    override fun callPlaceOrderService(payMethodType: String, checkOutModel: CheckoutModel?) {
        orderService!!.placeOrder(object : BaseViewResultCallback<Order<*>?>(
            view, true, false
        ) {
            override fun onFailView(errorCode: Int, errorMessage: String) {
                view!!.showErrorDialog("$errorCode $errorMessage")
            }

            override fun onErrorView(throwable: Throwable) {
                getAccountToken()
            }

            override fun onSuccessViewUpdates(data: Order<*>?) {
                getAccountToken()
            }
        })
    }

    override suspend fun callSaleRequest(checkOutModel: CheckoutModel, fiservTokenId: String, paymentData: PaymentTypeData) {
        view!!.showLoadingLayer(true)
        when (val result = authorizeSaleUseCase(checkOutModel.order, fiservTokenId, paymentData)) {
            is AuthorizeSaleResult.Error -> view!!.showErrorDialog(result.message)
            is AuthorizeSaleResult.Successful -> view!!.goToConfirmation(checkOutModel.order)
        }
        view!!.hideLoadingLayer()
    }

    override fun initBraintreeClient(paymentTypeSelectionActivity: PaymentTypeSelectionActivity) {
        braintreeClient = BraintreeClient(paymentTypeSelectionActivity, BuildConfig.PAY_PAL_AUTH_TOKEN)
        payPalClient = PayPalClient(paymentTypeSelectionActivity, braintreeClient)
        venmoClient = VenmoClient(paymentTypeSelectionActivity, braintreeClient)
        if (mSettingsServices!!.isVenmoEnabled) {
            venmoClient.isReadyToPay(paymentTypeSelectionActivity) { isVenmoAvailable, error ->
                isVenmoEnabled = isVenmoAvailable
                view!!.setVenmoButtonVisibility(isVisible = isVenmoAvailable)
                error?.let { Log.d("PaymentSelectionPresenter", "Venmo not available: ${it.message}") }
            }
        } else {
            view!!.setVenmoButtonVisibility(isVisible = false)
        }
    }

    /**
     * If isReadyToPay returned `true`, show the button and hide the "checking" text. Otherwise,
     * notify the user that Google Pay is not available. Please adjust to fit in with your current
     * user flow. You are not required to explicitly let the user know if isReadyToPay returns `false`.
     *
     * @param available isReadyToPay API response.
     */
    private fun setGooglePayAvailable(available: Boolean) {
        isGooglePayEnabled = available
        if (available) {
            view!!.showGooglePayButton()
        } else {
            view!!.hideGooglePayButton()
        }
    }

    // Call Token to be pass to Fiserv sale API i.e. Client Token
    private fun callAcquireToken() {
        val deviceId = encryptBase64(mSettingsServices!!.deviceId!!)
        mPayGateService?.acquireToken(object : ResultCallback<FiservAnonResponse?> {
            override fun onSuccess(fiservAnonResponse: FiservAnonResponse?) {
                view!!.hideLoadingLayer()
                android.util.Log.d(TAG, fiservAnonResponse.toString())
                view!!.getAccountTokenResponse(fiservAnonResponse)
            }

            override fun onFail(errorCode: Int, errorMessage: String?) {
                view!!.hideLoadingLayer()
                view!!.showErrorDialog(errorMessage ?: "Unknown error")
                Log.e(TAG, LogErrorException("On Fail : Acquire Token connection Failed"))
            }

            override fun onError(error: Throwable?) {
                view!!.hideLoadingLayer()
                view!!.showErrorDialog(mSettingsServices!!.pgCommonErrorMsg)
                Log.e(TAG, LogErrorException("On Error : Acquire Token connection Failed", error))
            }
        }, deviceId)
    }

    fun encryptBase64(input: String?): String? {
        if (input.isNullOrEmpty()) {
            return input
        }

        return StringUtils.encryptToBase64(mPayGatePubKey!!, input.encodeToByteArray(), AppConstants.PAY_GATE_PUBLIC_KEY_ALGORITHM)
    }

    override fun setOrder(data: Order<out OrderItem<*>>?) {
        TODO("Not yet implemented")
    }
}
