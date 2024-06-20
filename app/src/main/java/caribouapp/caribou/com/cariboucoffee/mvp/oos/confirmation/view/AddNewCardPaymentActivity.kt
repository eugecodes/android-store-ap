package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.core.widget.doOnTextChanged
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.library.baseAdapters.BR
import caribouapp.caribou.com.cariboucoffee.R
import caribouapp.caribou.com.cariboucoffee.SourceApplication
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen
import caribouapp.caribou.com.cariboucoffee.common.*
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityAddNewPaymentBinding
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.MainActivity
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.CheckoutModel
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.NewCardPaymentContract
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.presenter.NewCardPaymentPresenter
import caribouapp.caribou.com.cariboucoffee.order.Order
import caribouapp.caribou.com.cariboucoffee.util.StringUtils
import caribouapp.caribou.com.cariboucoffee.util.UIUtil
import caribouapp.caribou.com.cariboucoffee.util.serializableExtra
import com.google.android.material.textfield.TextInputLayout
import icepick.Icepick
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard
import java.math.BigDecimal
import java.util.*

/**
 * Created by Swapnil on 09/22/22.
 */
class AddNewCardPaymentActivity : BaseActivity<ActivityAddNewPaymentBinding?>(), NewCardPaymentContract.View, View.OnClickListener {
    private var mAppScreen: AppScreen? = null
    private var mPresenter: NewCardPaymentContract.Presenter? = null
    private var isCCRequiredErrorShown: Boolean = false

    var mModel: CCInformationModel? = null
    private var openScanActivity: ActivityResultLauncher<Intent>? = null

    object StaticIntent {
        const val EXTRA_CHECKOUT_MODEL = "checkout_model"
    }

    private val mOnModelPropertyChangedCallback: OnPropertyChangedCallback = object : OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable, propertyId: Int) {
            if (propertyId == BR.state) {
                val selection = if (mModel!!.state == null) 0 else mModel!!.state.ordinal + 1
                binding!!.contentIncluded.spnAddState.setSelection(selection)
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_new_payment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Icepick.restoreInstanceState(this, savedInstanceState)
        mAppScreen = AppScreen.ADD_NEW_CARD_PAYMENT

        if (mModel == null) {
            mModel = CCInformationModel()
            binding!!.contentIncluded.model = mModel
        }

        SourceApplication[this].component.inject(this)
        val cardPaymentPresenter = NewCardPaymentPresenter(this, mModel!!)
        SourceApplication[this].component.inject(cardPaymentPresenter)

        mPresenter = cardPaymentPresenter
        // Sets up toolbar
        setSupportActionBar(binding!!.tb)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        // We fill the states spinner
        initStatesSpinner()
        arrangeUIFields()
        scanCardListener()
        setComponentsListeners()

        onBackPressedDispatcher.addCallback(this) {
            if (!isLoading) {
                finish()
            }
        }
    }

    /**
     * This method is used to scan card with scan.io lib
     * and process the result, set into view
     * */
    private fun scanCardListener() {
        val contentIncluded = binding!!.contentIncluded
        contentIncluded.btnScanCardFromCamera.setOnClickListener(this)
        openScanActivity =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it!!.data != null && it.data!!.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                    val scanResult: CreditCard? = IntentCompat.getParcelableExtra(it.data!!, CardIOActivity.EXTRA_SCAN_RESULT, CreditCard::class.java)
                    if (scanResult != null) {
                        if (!scanResult.cardNumber.isNullOrEmpty()) {
                            binding!!.contentIncluded.model!!.ccNumber = scanResult.cardNumber
                            binding!!.contentIncluded.etAddCcNumber.setText((scanResult.cardNumber).toString())
                        }

                        if (scanResult.isExpiryValid) {
                            if (scanResult.expiryMonth != 0) {
                                binding!!.contentIncluded.model!!.expirationMonth = scanResult.expiryMonth.toString()
                            }
                            if (scanResult.expiryYear != 0) {
                                val year: String = scanResult.expiryYear.toString()
                                val lastTwoDigits: String = year.substring(year.length - 2)
                                binding!!.contentIncluded.model!!.expirationYear = lastTwoDigits
                            }
                        }
                    }
                }
            }
    }

    /**
     * If flag is true then we will show all fields
     * if false then will show minimum fields
     * */
    private fun arrangeUIFields() {
        mPresenter!!.getGuestDetails()
        val visibility = if (mPresenter!!.isAllBillingFieldsShow()) View.VISIBLE else View.GONE

        binding!!.contentIncluded.tilAddBillingAddress1.visibility = visibility
        binding!!.contentIncluded.tilAddBillingAddress2.visibility = visibility
        binding!!.contentIncluded.llState.visibility = visibility
        binding!!.contentIncluded.tilAddCity.visibility = visibility
        binding!!.contentIncluded.tilAddZipCode.visibility = View.VISIBLE
    }

    /**
     * This method is used to add click listener
     * according to UI
     * */
    private fun setComponentsListeners() {
        binding!!.contentIncluded.btnPlaceOrderByNewGateway.setOnClickListener(this)
        binding!!.contentIncluded.btnCancelOrder.setOnClickListener(this)
        binding!!.contentIncluded.etAddCcNumber.transformationMethod = null
        binding!!.contentIncluded.etAddExpirationMonth.transformationMethod = null
        binding!!.contentIncluded.etAddExpirationYear.transformationMethod = null
        binding!!.contentIncluded.etAddCvv.transformationMethod = null
        binding!!.contentIncluded.etAddZipCode.setOnEditorActionListener { _, actionId, _ -> // v, event are replaced by _
            if (actionId == EditorInfo.IME_ACTION_GO) {
                binding!!.contentIncluded.btnPlaceOrderByNewGateway.performClick()
            }
            false
        }

        binding!!.contentIncluded.etAddCity.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                UIUtil.hideKeyboard(this@AddNewCardPaymentActivity)
                binding!!.contentIncluded.spnAddState.performClick()
                return@setOnEditorActionListener true
            }
            false
        }

        // start, before, count replace with _,_,_ because we are not using that variables
        binding!!.contentIncluded.etNameOnCard.doOnTextChanged { text, _, _, _ ->
            removeErrorValidation(text, binding!!.contentIncluded.tilNameOnCard)
        }
        binding!!.contentIncluded.etAddCcNumber.doOnTextChanged { text, _, _, _ ->
            removeErrorValidation(text, binding!!.contentIncluded.tilAddCcNumber)
        }
        binding!!.contentIncluded.etAddExpirationMonth.doOnTextChanged { text, _, _, _ ->
            removeErrorValidation(text, binding!!.contentIncluded.tilAddExpirationMonth)
        }
        binding!!.contentIncluded.etAddExpirationYear.doOnTextChanged { text, _, _, _ ->
            removeErrorValidation(text, binding!!.contentIncluded.tilAddExpirationYear)
        }
        binding!!.contentIncluded.etAddCvv.doOnTextChanged { text, _, _, _ ->
            removeErrorValidation(text, binding!!.contentIncluded.tilAddCvv)
        }
        binding!!.contentIncluded.etAddZipCode.doOnTextChanged { text, _, _, _ ->
            removeErrorValidation(text, binding!!.contentIncluded.tilAddZipCode)
        }
    }

    /**
     * This method is used remove validation of
     * TextInputLayout
     * @param text
     * @param textInputLayout
     * */
    private fun removeErrorValidation(text: CharSequence?, textInputLayout: TextInputLayout) {
        if (text!!.isNotEmpty()) {
            textInputLayout.error = null
            textInputLayout.isErrorEnabled = false
        }
    }

    /**
     * This method is used for
     * initiate the state spinner
     * add State in first position
     * render in adapter
     * */
    private fun initStatesSpinner() {
        binding!!.contentIncluded.spnAddState.adapter = StateEnumSpinnerAdapter(this, getString(R.string.select_a_state), StateEnum.values())
        if (mModel!!.state != null) {
            // Plus one for the header of the spinner
            binding!!.contentIncluded.spnAddState.setSelection(listOf(*StateEnum.values()).indexOf(mModel!!.state) + 1)
        }
        mModel!!.addOnPropertyChangedCallback(mOnModelPropertyChangedCallback)
        binding!!.contentIncluded.spnAddState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                if (position > 0) {
                    mModel!!.state = binding!!.contentIncluded.spnAddState.selectedItem as StateEnum
                    binding!!.contentIncluded.hintAddState.visibility = View.VISIBLE
                    binding!!.contentIncluded.etAddZipCode.requestFocus()
                } else {
                    binding!!.contentIncluded.hintAddState.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // NO-OP
            }
        }
    }

    override fun getScreenName(): AppScreen {
        return AppScreen.ADD_NEW_CARD_PAYMENT
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
    }

    companion object {
        private val TAG = AddNewCardPaymentActivity::class.java.simpleName
    }

    override fun cvvUnknownErrorEnabled(enabled: Boolean): Boolean {
        if (enabled) {
            binding!!.contentIncluded.tilAddCvv.error = getString(R.string.cvv_not_recognized)
        } else {
            binding!!.contentIncluded.tilAddCvv.isErrorEnabled = false
        }
        return enabled
    }

    override fun cardNumberErrorEnabled(enabled: Boolean): Boolean {
        if (!isCCRequiredErrorShown) {
            if (enabled) {
                binding!!.contentIncluded.tilAddCcNumber.error = getString(R.string.cc_number_error)
                isCCRequiredErrorShown = true
            } else {
                binding!!.contentIncluded.tilAddCcNumber.isErrorEnabled = false
                isCCRequiredErrorShown = false
            }
        }
        return enabled
    }

    override fun validCardNumberErrorEnabled(enabled: Boolean): Boolean {
        if (enabled) {
            binding!!.contentIncluded.tilAddCcNumber.error = getString(R.string.cc_number_error_special_char)
        } else {
            binding!!.contentIncluded.tilAddCcNumber.isErrorEnabled = false
        }
        return enabled
    }

    override fun nameErrorEnabled(enabled: Boolean): Boolean {
        if (enabled) {
            binding!!.contentIncluded.tilNameOnCard.error = getString(R.string.name_on_card_error)
        } else {
            binding!!.contentIncluded.tilNameOnCard.isErrorEnabled = false
        }
        return enabled
    }

    override fun expirationMonthErrorEnabled(enabled: Boolean): Boolean {
        if (enabled) {
            binding!!.contentIncluded.tilAddExpirationMonth.error = getString(R.string.expiring_month_error)
        } else {
            binding!!.contentIncluded.tilAddExpirationMonth.isErrorEnabled = false
        }
        return enabled
    }

    override fun expirationYearErrorEnabled(enabled: Boolean): Boolean {
        if (enabled) {
            binding!!.contentIncluded.tilAddExpirationYear.error = getString(R.string.expiring_year_error)
        } else {
            binding!!.contentIncluded.tilAddExpirationYear.isErrorEnabled = false
        }
        return enabled
    }

    override fun monthExpiredErrorEnabled(enabled: Boolean): Boolean {
        if (enabled) {
            binding!!.contentIncluded.tilAddExpirationMonth.error = getString(R.string.expire_month_error)
        } else {
            binding!!.contentIncluded.tilAddExpirationMonth.isErrorEnabled = false
        }
        return enabled
    }

    override fun yearExpiredErrorEnabled(enabled: Boolean): Boolean {
        if (enabled) {
            binding!!.contentIncluded.tilAddExpirationYear.error = getString(R.string.expire_year_error)
        } else {
            binding!!.contentIncluded.tilAddExpirationYear.isErrorEnabled = false
        }
        return enabled
    }

    override fun cvvErrorEnabled(enabled: Boolean): Boolean {
        if (enabled) {
            binding!!.contentIncluded.tilAddCvv.error = getString(R.string.cvv_error)
        } else {
            binding!!.contentIncluded.tilAddCvv.isErrorEnabled = false
        }
        return enabled
    }

    override fun validCvvErrorEnabled(enabled: Boolean): Boolean {
        if (enabled) {
            binding!!.contentIncluded.tilAddCvv.error = getString(R.string.cvv_error_special_char)
        } else {
            binding!!.contentIncluded.tilAddCvv.isErrorEnabled = false
        }
        return enabled
    }

    override fun billingAddressErrorEnabled(enabled: Boolean): Boolean {
        if (enabled) {
            binding!!.contentIncluded.tilAddBillingAddress1.error = getString(R.string.billing_address_error)
        } else {
            binding!!.contentIncluded.tilAddBillingAddress1.isErrorEnabled = false
        }
        return enabled
    }

    override fun zipErrorEnabled(enabled: Boolean): Boolean {
        if (enabled) {
            binding!!.contentIncluded.tilAddZipCode.error = getString(R.string.zip_error)
        } else {
            binding!!.contentIncluded.tilAddZipCode.isErrorEnabled = false
        }
        return enabled
    }

    override fun emptyZipErrorEnabled(enabled: Boolean): Boolean {
        if (enabled) {
            binding!!.contentIncluded.tilAddZipCode.error = getString(R.string.empty_zip_error)
        } else {
            binding!!.contentIncluded.tilAddZipCode.isErrorEnabled = false
        }
        return enabled
    }

    override fun cityErrorEnabled(enabled: Boolean): Boolean {
        if (enabled) {
            binding!!.contentIncluded.tilAddCity.error = getString(R.string.city_error)
        } else {
            binding!!.contentIncluded.tilAddCity.isErrorEnabled = false
        }
        return enabled
    }

    override fun stateErrorEnabled(enabled: Boolean): Boolean {
        binding!!.contentIncluded.tvAddSelectStateError.visibility = if (enabled) View.VISIBLE else View.GONE
        return enabled
    }

    override fun cardTypeUnknownErrorEnabled(enabled: Boolean): Boolean {
        if (enabled) {
            binding!!.contentIncluded.tilAddCcNumber.error = getString(R.string.cc_unknown_card_type_error)
            cvvUnknownErrorEnabled(false) // We shouldn't show cvv errors while we still have CC number errors
        } else {
            binding!!.contentIncluded.tilAddCcNumber.isErrorEnabled = false
        }
        return enabled
    }

    override fun cardDigitNumberErrorEnabled(enabled: Boolean): Boolean {
        if (!isCCRequiredErrorShown) {
            if (enabled) {
                binding!!.contentIncluded.tilAddCcNumber.error = getString(R.string.cc_number_error_special_char)
                isCCRequiredErrorShown = true
                cvvUnknownErrorEnabled(false) // We shouldn't show cvv errors while we still have CC number errors
            } else {
                binding!!.contentIncluded.tilAddCcNumber.isErrorEnabled = false
                isCCRequiredErrorShown = false
            }
        }
        return enabled
    }

    override fun setModel(model: CCInformationModel?) {
        binding!!.contentIncluded.model = model
    }

    override fun updateNameOnCard(name: String) {
        binding!!.contentIncluded.model!!.nameOnCard = name
    }

    override fun showNotValidSelectedPickupTime() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.oops))
            .setMessage(getString(R.string.pickup_time_outdated))
            .setNegativeButton(R.string.okay) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun showStoreClosedDialog() {
        DialogUtil.showStoreClosedAlert(this) {}
    }

    override fun showStoreNearClosingForBulk() {
        DialogUtil.showStoreNearClosingForBulk(this)
    }

    override fun showDeliveryClosedDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.oops))
            .setMessage(getString(R.string.delivery_closed))
            .setNegativeButton(R.string.okay) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun showDeliveryMinimumNotMetDialog(deliveryMinimum: BigDecimal?) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.oops))
            .setMessage(
                getString(
                    R.string.required_delivery_minimum_not_met,
                    StringUtils.formatMoneyAmount(this, deliveryMinimum, null)
                )
            )
            .setNegativeButton(R.string.okay) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun showChooseANewPickUpTimeDialog() {
        showMessage(R.string.choose_new_pick_up_time)
    }

    override fun showFailToPlaceOrderDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.sorry)
            .setMessage(R.string.problem_with_order)
            .setPositiveButton(R.string.okay) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun goToConfirmation(orderData: Order<*>?) {
        showLoadingLayer(true)
        startActivity(OrderConfirmationActivity.createIntent(this, orderData))
        finish()
    }

    override fun showErrorDialog(errorMessage: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.sorry)
            .setMessage(errorMessage)
            .setPositiveButton(R.string.okay) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun showCommonErrorDialog(errorMessage: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.sorry)
            .setMessage(R.string.pg_common_error)
            .setPositiveButton(R.string.okay) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun getCurrentYear(): Int {
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val lastTwoDigits: String = year.toString().substring(year.toString().length - 2)
        return lastTwoDigits.toInt()
    }

    override fun getCurrentMonth(): Int {
        val calendar: Calendar = Calendar.getInstance()
        return calendar.get(Calendar.MONTH) + 1
    }

    override fun goToDashboard() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_place_order_by_new_gateway -> {
                isCCRequiredErrorShown = false
                UIUtil.hideKeyboard(this)
                mPresenter!!.placeOrder(getCheckoutModel())
            }
            R.id.btn_scan_card_from_camera -> {
                var scanIntent = Intent(this, CardIOActivity::class.java)
                scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true)
                scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true)
                scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, true)
                scanIntent.putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false)
                scanIntent.putExtra(CardIOActivity.EXTRA_GUIDE_COLOR, ContextCompat.getColor(this, R.color.primaryColor))
                openScanActivity!!.launch(scanIntent)
            }
            R.id.btn_cancel_order -> {
                finish()
            }
        }
    }

    private fun getCheckoutModel(): CheckoutModel? = intent.serializableExtra(GuestUserDetailActivityDialog.EXTRA_CHECKOUT_MODEL)

    /**
     * Detach view when activity
     * is going to in destroy state
     */
    override fun onDestroy() {
        super.onDestroy()
        mModel!!.removeOnPropertyChangedCallback(mOnModelPropertyChangedCallback)
        mPresenter!!.detachView()
    }
}
