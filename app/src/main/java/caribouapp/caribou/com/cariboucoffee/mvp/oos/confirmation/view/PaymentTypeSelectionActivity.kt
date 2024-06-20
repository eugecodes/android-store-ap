package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import caribouapp.caribou.com.cariboucoffee.AppConstants.GPAY
import caribouapp.caribou.com.cariboucoffee.AppConstants.PAYPAL
import caribouapp.caribou.com.cariboucoffee.AppConstants.VENMO
import caribouapp.caribou.com.cariboucoffee.BuildConfig
import caribouapp.caribou.com.cariboucoffee.R
import caribouapp.caribou.com.cariboucoffee.SourceApplication
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityPaymentTypeSelectionBinding
import caribouapp.caribou.com.cariboucoffee.fiserv.model.FiservAnonResponse
import caribouapp.caribou.com.cariboucoffee.googlepay.PaymentsUtil
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.PaymentSelectionContract
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.model.PaymentTypeData
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.presenter.PaymentSelectionPresenter
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.viewmodel.CheckoutModelHolderViewModel
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.viewmodel.CheckoutModelPlaceOrderArg
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.viewmodel.PaymentTypePlaceOrderArg
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.viewmodel.TokenIDPlaceOrderArg
import caribouapp.caribou.com.cariboucoffee.order.Order
import com.braintreepayments.api.PayPalAccountNonce
import com.braintreepayments.api.PayPalListener
import com.braintreepayments.api.PayPalVaultRequest
import com.braintreepayments.api.UserCanceledException
import com.braintreepayments.api.VenmoAccountNonce
import com.braintreepayments.api.VenmoListener
import com.braintreepayments.api.VenmoPaymentMethodUsage
import com.braintreepayments.api.VenmoRequest
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import kotlinx.coroutines.launch

private const val LOAD_PAYMENT_DATA_REQUEST_CODE = 991

class PaymentTypeSelectionActivity : BaseActivity<ActivityPaymentTypeSelectionBinding?>(),
    PaymentSelectionContract.View, View.OnClickListener, PayPalListener, VenmoListener {
    private var mAppScreen: AppScreen? = null
    private var mPresenter: PaymentSelectionContract.Presenter? = null
    private val viewModel by viewModels<CheckoutModelHolderViewModel>()

    override fun getLayoutId(): Int {
        return R.layout.activity_payment_type_selection
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAppScreen = AppScreen.PAYMENT_TYPE_SELECTION

        setSupportActionBar(binding!!.tb)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        SourceApplication[this].component.inject(this)
        val paymentSelectionPresenter = PaymentSelectionPresenter(this)
        SourceApplication[this].component.inject(paymentSelectionPresenter)

        mPresenter = paymentSelectionPresenter
        mPresenter!!.initiatePaymentsClient(this)
        mPresenter!!.initBraintreeClient(this)
        mPresenter!!.payPalClient.setListener(this)
        mPresenter!!.venmoClient.setListener(this)
        renderClickEvent()
        renderUIonCondition()

        onBackPressedDispatcher.addCallback(this) {
            if (!isLoading) {
                finish()
            }
        }
    }

    companion object {
        @JvmStatic
        var PAYMENT_SELECTION_TYPE: String = ""
        var fiservAnonResponse: FiservAnonResponse? = null
    }

    private fun renderClickEvent() {
        binding!!.contentIncluded.btnGooglePay.setOnClickListener(this)
        binding!!.contentIncluded.btnPayPal.setOnClickListener(this)
        binding!!.contentIncluded.btnVenmo.setOnClickListener(this)
        binding!!.contentIncluded.btnPayWithDebitCreditCard.setOnClickListener(this)
    }

    private fun hideShowButton(enabled: Boolean, imgButton: RelativeLayout) {
        if (enabled) {
            imgButton.visibility = View.VISIBLE
        } else {
            imgButton.visibility = View.GONE
        }
    }

    private fun renderUIonCondition() {
        hideShowButton(mPresenter!!.isPayPalEnabled(), binding!!.contentIncluded.btnPayPal)
        binding!!.contentIncluded.btnVenmo.isVisible = mPresenter!!.isVenmoEnabled
        binding!!.contentIncluded.btnGooglePay.isVisible = mPresenter!!.isGooglePayEnabled
        setLineSeparatorVisibility()
    }

    override fun getScreenName(): AppScreen {
        return AppScreen.PAYMENT_TYPE_SELECTION
    }

    object StaticIntent {
        const val EXTRA_CHECKOUT_MODEL = "checkout_model"
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_google_pay -> {
                PAYMENT_SELECTION_TYPE = GPAY
                mPresenter!!.callPlaceOrderService(GPAY, viewModel.checkoutModel)
            }

            R.id.btn_pay_pal -> {
                PAYMENT_SELECTION_TYPE = PAYPAL
                mPresenter!!.callPlaceOrderService(PAYPAL, viewModel.checkoutModel)
            }

            R.id.btn_venmo -> {
                PAYMENT_SELECTION_TYPE = VENMO
                mPresenter!!.callPlaceOrderService(VENMO, viewModel.checkoutModel)
            }

            R.id.btn_pay_with_debit_credit_card -> {
                val intent = Intent(this, AddNewCardPaymentActivity::class.java)
                intent.putExtra(
                    AddNewCardPaymentActivity.StaticIntent.EXTRA_CHECKOUT_MODEL,
                    viewModel.checkoutModel
                )
                startActivity(intent)
            }
        }
    }

    private fun requestGooglePayPayment() {
        binding!!.contentIncluded.btnGooglePay.isClickable = false
        val paymentDataRequestJson = PaymentsUtil
            .getPaymentDataRequest(viewModel.checkoutModel.order.totalWithTip.toString())
        android.util.Log.d("RequestJson", "" + paymentDataRequestJson.toString())

        if (paymentDataRequestJson == null) {
            showErrorDialog("RequestPayment Can't fetch payment data request")
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())

        AutoResolveHelper.resolveTask(
            mPresenter!!.getPaymentsClient().loadPaymentData(request),
            this,
            LOAD_PAYMENT_DATA_REQUEST_CODE
        )
    }

    override fun hideGooglePayButton() {
        binding!!.contentIncluded.btnGooglePay.visibility = View.GONE
        setLineSeparatorVisibility()
    }

    override fun showGooglePayButton() {
        binding!!.contentIncluded.btnGooglePay.visibility = View.VISIBLE
        setLineSeparatorVisibility()
    }

    override fun setVenmoButtonVisibility(isVisible: Boolean) {
        binding!!.contentIncluded.btnVenmo.isVisible = isVisible
        setLineSeparatorVisibility()
    }

    private fun setLineSeparatorVisibility() {
        binding!!.contentIncluded
            .viewLineSeparator.isVisible = mPresenter!!.isGooglePayEnabled || mPresenter!!.isPayPalEnabled() || mPresenter!!.isVenmoEnabled
    }

    override fun showErrorDialog(errorMessage: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.sorry)
            .setMessage(errorMessage)
            .setPositiveButton(R.string.okay) { dialog, _ -> dialog.dismiss() }.show()
    }

    override fun getAccountTokenResponse(data: FiservAnonResponse?) {
        fiservAnonResponse = data
        when (PAYMENT_SELECTION_TYPE) {
            GPAY -> requestGooglePayPayment()
            PAYPAL -> requestPayPalPayment()
            VENMO -> requestVenmoPayment()
        }
    }

    private fun requestPayPalPayment() {
        showLoadingLayer()
        val request = PayPalVaultRequest()
        request.merchantAccountId = BuildConfig.PAY_PAL_MERCHANT_ID
        mPresenter!!.payPalClient.tokenizePayPalAccount(this, request)
    }

    private fun requestVenmoPayment() {
        showLoadingLayer()
        val request = VenmoRequest(VenmoPaymentMethodUsage.MULTI_USE).apply {
            profileId = BuildConfig.VENMO_PROFILE_ID
            shouldVault = true
            collectCustomerShippingAddress = true
            totalAmount = viewModel.checkoutModel.order.totalWithTip.toString()
        }
        mPresenter!!.venmoClient.tokenizeVenmoAccount(this, request)
    }

    override fun goToConfirmation(orderData: Order<*>?) {
        showLoadingLayer(true)
        startActivity(OrderConfirmationActivity.createIntent(this, orderData))
        finish()
    }

    override fun goToDashboard() {
    }

    /**
     * Handle a resolved activity from the Google Pay payment sheet.
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> data?.let { intent ->
                        PaymentData.getFromIntent(intent)?.let(::handleGooglePaymentSuccess)
                    }

                    RESULT_CANCELED -> {
                        showErrorDialog("Payment canceled!")
                    }

                    AutoResolveHelper.RESULT_ERROR -> {
                        AutoResolveHelper.getStatusFromIntent(data)?.let {
                            handleError(it.statusCode, it.statusMessage)
                        }
                    }
                }
                binding!!.contentIncluded.btnGooglePay.isClickable = true
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * PaymentData response object contains the payment information, as well as any additional
     * requested information, such as billing and shipping address.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     * @see [Payment
     * Data](https://developers.google.com/pay/api/android/reference/object.PaymentData)
     */
    private fun handleGooglePaymentSuccess(paymentData: PaymentData) {
        onPaymentSuccess(paymentTypeData = PaymentTypeData.GooglePay(data = paymentData.toJson()))
    }

    private fun handleError(statusCode: Int, statusMessage: String?) {
        hideLoadingLayer()
        showErrorDialog(String.format("Error code: %d$statusCode Error Message $statusMessage"))
    }

    override fun onPayPalSuccess(payPalAccountNonce: PayPalAccountNonce) {
        onPaymentSuccess(paymentTypeData = PaymentTypeData.PayPal(payerId = payPalAccountNonce.payerId, accountNonce = payPalAccountNonce.string))
    }

    override fun onPayPalFailure(error: Exception) {
        onPaymentMethodFailed(error)
    }

    override fun onVenmoSuccess(venmoAccountNonce: VenmoAccountNonce) {
        onPaymentSuccess(paymentTypeData = PaymentTypeData.Venmo(venmoAccountNonce.string))
    }

    override fun onVenmoFailure(error: Exception) {
        onPaymentMethodFailed(error)
    }

    private fun onPaymentSuccess(paymentTypeData: PaymentTypeData) = kotlin.runCatching {
        hideLoadingLayer()
        when (paymentTypeData) {
            is PaymentTypeData.GooglePay -> lifecycleScope.launch {
                mPresenter!!.callSaleRequest(viewModel.checkoutModel, fiservAnonResponse!!.tokenId!!, paymentTypeData)
            }

            is PaymentTypeData.PayPal,
            is PaymentTypeData.Venmo -> {
                val intent = Intent(this, PlaceOrderActivity::class.java).apply {
                    putExtra(PaymentTypePlaceOrderArg, paymentTypeData)
                    putExtra(CheckoutModelPlaceOrderArg, viewModel.checkoutModel)
                    putExtra(TokenIDPlaceOrderArg, fiservAnonResponse!!.tokenId)
                }
                startActivity(intent)
            }

            is PaymentTypeData.Credit -> Unit
        }
    }.onFailure { hideLoadingLayer() }

    private fun onPaymentMethodFailed(error: Exception) {
        hideLoadingLayer()
        if (error is UserCanceledException) {
            showErrorDialog(getString(R.string.user_canceled_transaction_error_message))
        } else {
            showErrorDialog(getString(R.string.payment_cannot_be_processed_error_message))
        }
    }
}
