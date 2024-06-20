package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.SpannableStringBuilder
import android.util.TypedValue
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import caribouapp.caribou.com.cariboucoffee.R
import caribouapp.caribou.com.cariboucoffee.SourceApplication
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity
import caribouapp.caribou.com.cariboucoffee.common.BrandEnum
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutLetsGetStartedDialogBinding
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.CheckoutModel
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.LetsGetStartedContract
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.model.GuestInfoModel
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.presenter.LetsGetStartedPresenter
import caribouapp.caribou.com.cariboucoffee.mvp.termsandprivacy.view.TermsAndPrivacyActivity
import caribouapp.caribou.com.cariboucoffee.util.AppUtils
import caribouapp.caribou.com.cariboucoffee.util.UIUtil
import caribouapp.caribou.com.cariboucoffee.util.serializableExtra
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Created by Swapnil on 09/08/22.
 */
class GuestUserDetailActivityDialog : BaseActivity<LayoutLetsGetStartedDialogBinding?>(),
    LetsGetStartedContract.View, View.OnFocusChangeListener, View.OnClickListener {

    private var mPresenter: LetsGetStartedContract.Presenter? = null
    var mGuestInfoModel: GuestInfoModel? = null
    private var isTermsAccepted: Boolean = false

    override fun getLayoutId(): Int {
        return R.layout.layout_lets_get_started_dialog
    }

    companion object {
        const val EXTRA_CHECKOUT_MODEL = "checkout_model"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        if (mGuestInfoModel == null) {
            mGuestInfoModel = GuestInfoModel()
            binding!!.model = mGuestInfoModel
        }

        SourceApplication[this].component.inject(this)
        val letsGetStartedPresenter = LetsGetStartedPresenter(this, mGuestInfoModel!!)
        SourceApplication[this].component.inject(letsGetStartedPresenter)

        mPresenter = letsGetStartedPresenter
        binding!!.tvLetsGetsStarted.text = mPresenter!!.letsGetStartedMessage()
        binding!!.tvLetsGetsStarted.contentDescription = mPresenter!!.letsGetStartedMessage()

        // set background to default while creating the views
        changeBackground(binding!!.tilGuestUserFirstName)
        changeBackground(binding!!.tilGuestUserLastName)
        changeBackground(binding!!.tilGuestUserEmail)
        changeBackground(binding!!.tilGuestUserPhoneNumber)

        setComponentsListener()
        renderCloseButtonImage()
        renderFontSizeForHeader()
    }

    private fun renderCloseButtonImage() {
        var resourceId = when (AppUtils.getBrand()) {
            BrandEnum.EBB_BRAND -> {
                R.drawable.ic_ebb_close
            }
            BrandEnum.BRU_BRAND -> {
                R.drawable.ic_bb_close
            }
            BrandEnum.NNYB_BRAND -> {
                R.drawable.ic_noah_close
            }
            else -> {
                R.drawable.ic_ebb_close
            }
        }
        binding!!.ivCloseDialog.setImageDrawable(ContextCompat.getDrawable(this, resourceId))
    }

    private fun getCheckoutModel(): CheckoutModel? = intent.serializableExtra(EXTRA_CHECKOUT_MODEL)

    /**
     * This method is used for
     * Setting the UI component listener
     * */
    private fun setComponentsListener() {
        isTermsAccepted = false
        binding!!.edtTxtGuestUserFirstName.onFocusChangeListener = this
        binding!!.edtTxtGuestUserLastName.onFocusChangeListener = this
        binding!!.edtTxtGuestUserEmail.onFocusChangeListener = this
        binding!!.edtTxtGuestUserPhoneNumber.onFocusChangeListener = this
        binding!!.edtTxtGuestUserPhoneNumber.transformationMethod = null
        binding!!.ivCloseDialog.setOnClickListener(this)
        binding!!.tvGuestGoToTNC.setOnClickListener(this)
        binding!!.btnContinueGuestDetails.setOnClickListener(this)
//        binding!!.cbGuestAgreeTNC.setOnCheckedChangeListener(this)
        binding!!.tvIAccept.setOnClickListener(this)
        binding!!.ivCheckTerms.setOnClickListener(this)
        binding!!.tilGuestUserFirstName.setEndIconOnClickListener {
            changeHintColorAndBackgroundWhenCloseButtonClicked(
                binding!!.tilGuestUserFirstName,
                binding!!.edtTxtGuestUserFirstName,
                R.color.guestHintColor
            )
        }
        binding!!.tilGuestUserLastName.setEndIconOnClickListener {
            changeHintColorAndBackgroundWhenCloseButtonClicked(
                binding!!.tilGuestUserLastName,
                binding!!.edtTxtGuestUserLastName,
                R.color.guestHintColor
            )
        }
        binding!!.tilGuestUserEmail.setEndIconOnClickListener {
            changeHintColorAndBackgroundWhenCloseButtonClicked(
                binding!!.tilGuestUserEmail,
                binding!!.edtTxtGuestUserEmail,
                R.color.guestHintColor
            )
        }
        binding!!.tilGuestUserPhoneNumber.setEndIconOnClickListener {
            changeHintColorAndBackgroundWhenCloseButtonClicked(
                binding!!.tilGuestUserPhoneNumber,
                binding!!.edtTxtGuestUserPhoneNumber,
                R.color.guestHintColor
            )
        }

        // start, before, count replace with _,_,_ because we are not using that variables
        binding!!.edtTxtGuestUserFirstName.doOnTextChanged { text, _, _, _ ->
            removeErrorValidation(text, binding!!.tilGuestUserFirstName, R.color.whiteColor)
        }
        binding!!.edtTxtGuestUserLastName.doOnTextChanged { text, _, _, _ ->
            removeErrorValidation(text, binding!!.tilGuestUserLastName, R.color.whiteColor)
        }
        binding!!.edtTxtGuestUserEmail.doOnTextChanged { text, _, _, _ ->
            removeErrorValidation(text, binding!!.tilGuestUserEmail, R.color.whiteColor)
        }
        binding!!.edtTxtGuestUserPhoneNumber.doOnTextChanged { text, _, _, _ ->
            removeErrorValidation(text, binding!!.tilGuestUserPhoneNumber, R.color.whiteColor)
        }
        binding!!.edtTxtGuestUserPhoneNumber.doAfterTextChanged { text ->
            // Delete any non phone number characters from the input
            val nonPhoneChars = "[^0-9() -]".toRegex()
            if (text != null && text.contains(nonPhoneChars)) {
                val span = SpannableStringBuilder(text.replace(nonPhoneChars, ""))
                text.replace(0, text.length, span)
            }
        }
        binding!!.edtTxtGuestUserPhoneNumber.addTextChangedListener(PhoneNumberFormattingTextWatcher("US"))
    }

    /**
     * This method is used for
     * Remove validation of TextInputLayout
     * @param text
     * @param textInputLayout
     * */
    private fun removeErrorValidation(
        text: CharSequence?,
        textInputLayout: TextInputLayout,
        colorCode: Int
    ) {
        if (text!!.isNotEmpty()) {
            setDefaultBackground(textInputLayout)
            changeHintColor(textInputLayout, colorCode)
            textInputLayout.error = null
            textInputLayout.isErrorEnabled = false
        }
    }

    /**
     * @param TextInputLayout
     * @param TextInputEditText
     * @param guestHintColor
     * Used to change the hint color and background when we click on Cross close button from edit text
     */
    private fun changeHintColorAndBackgroundWhenCloseButtonClicked(
        textInputLayout: TextInputLayout,
        textInputEditText: TextInputEditText,
        guestHintColor: Int
    ) {
        textInputEditText.text?.clear()
        if (currentFocus?.id != textInputEditText.id) {
            changeHintColor(textInputLayout, guestHintColor)
            changeBackground(textInputLayout)
        }
    }

    private fun renderFontSizeForHeader() {
        when (AppUtils.getBrand()) {
            BrandEnum.EBB_BRAND -> {
                binding!!.tvLetsGetsStarted.setTextSize(TypedValue.COMPLEX_UNIT_PX, applicationContext.resources.getDimension(R.dimen.ebb_header_lets_get_started))
            }
            BrandEnum.BRU_BRAND -> {
                binding!!.tvLetsGetsStarted.setTextSize(TypedValue.COMPLEX_UNIT_PX, applicationContext.resources.getDimension(R.dimen.bru_header_lets_get_started))
            }
            BrandEnum.NNYB_BRAND -> {
                binding!!.tvLetsGetsStarted.setTextSize(TypedValue.COMPLEX_UNIT_PX, applicationContext.resources.getDimension(R.dimen.noah_header_lets_get_started))
            }
            else -> {
                binding!!.tvLetsGetsStarted.setTextSize(TypedValue.COMPLEX_UNIT_PX, applicationContext.resources.getDimension(R.dimen.bru_header_lets_get_started))
            }
        }
    }

    override fun getScreenName(): AppScreen {
        return AppScreen.GUEST_USER_DETAILS
    }

    override fun onFocusChange(view: View?, focusFlag: Boolean) {
        when (view?.id) {
            R.id.edt_txt_guest_user_first_name -> {
                if (!binding!!.tilGuestUserFirstName.isErrorEnabled) {
                    changeHintColorAndBackground(
                        binding!!.tilGuestUserFirstName,
                        binding!!.edtTxtGuestUserFirstName,
                        R.color.whiteColor, R.color.guestHintColor, focusFlag
                    )
                }
            }
            R.id.edt_txt_guest_user_last_name -> {
                if (!binding!!.tilGuestUserLastName.isErrorEnabled) {
                    changeHintColorAndBackground(
                        binding!!.tilGuestUserLastName,
                        binding!!.edtTxtGuestUserLastName,
                        R.color.whiteColor, R.color.guestHintColor, focusFlag
                    )
                }
            }
            R.id.edt_txt_guest_user_email -> {
                if (!binding!!.tilGuestUserEmail.isErrorEnabled) {
                    changeHintColorAndBackground(
                        binding!!.tilGuestUserEmail,
                        binding!!.edtTxtGuestUserEmail,
                        R.color.whiteColor, R.color.guestHintColor, focusFlag
                    )
                }
            }
            R.id.edt_txt_guest_user_phone_number -> {
                changeHintColorAndBackground(
                    binding!!.tilGuestUserPhoneNumber,
                    binding!!.edtTxtGuestUserPhoneNumber,
                    R.color.whiteColor, R.color.guestHintColor, focusFlag
                )
            }
        }
    }

    /**
     *@param textInputLayout
     *@param edtText
     *@param focusFlag
     * This function is used to change the Hint color
     * and background of input text layout as per param passed
     * */
    private fun changeHintColorAndBackground(
        textInputLayout: TextInputLayout,
        edtText: TextInputEditText,
        whiteColor: Int,
        guestHintColor: Int,
        focusFlag: Boolean
    ) {
        setDefaultBackground(textInputLayout)
        changeHintColor(textInputLayout, whiteColor)
        if (edtText.text.isNullOrBlank() && !focusFlag) {
            changeHintColor(textInputLayout, guestHintColor)
            changeBackground(textInputLayout)
        }
    }

    /**
     * Used to set default background i.e. empty background
     * */
    private fun setDefaultBackground(textInputLayout: TextInputLayout) {
        textInputLayout.background = null
    }

    private fun changeBackground(textInputLayout: TextInputLayout) {
        textInputLayout.setBackgroundResource(R.drawable.simple_edittext)
    }

    private fun changeHintColor(textInputLayout: TextInputLayout, colorId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textInputLayout.hintTextColor = getColorStateList(colorId)
            textInputLayout.defaultHintTextColor = getColorStateList(colorId)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.iv_close_dialog -> {
                finish()
            }
            R.id.btn_continue_guest_details -> {
                UIUtil.hideKeyboard(this)
                mPresenter?.setModel(binding!!.model)
                mPresenter?.checkGuestDataAndContinue()
            }
            R.id.tv_guest_go_to_t_n_c -> {
                startActivity(Intent(this, TermsAndPrivacyActivity::class.java))
            }
            R.id.tv_i_accept,
            R.id.iv_check_terms -> {
                UIUtil.hideKeyboard(this)
                if (isTermsAccepted) {
                    binding!!.ivCheckTerms.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bb_unchecked))
                    binding?.btnContinueGuestDetails?.setTextColor(ContextCompat.getColor(this, R.color.halfMidWhiteColor))
                    binding?.btnContinueGuestDetails?.setTextColor(ContextCompat.getColor(this, R.color.halfMidWhiteColor))
                    binding?.btnContinueGuestDetails?.setBackgroundResource(R.drawable.commerce_primary_button_disabled)
                    binding?.btnContinueGuestDetails?.isEnabled = false
                    isTermsAccepted = false
                } else {
                    binding!!.ivCheckTerms.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bb_checked))
                    binding?.btnContinueGuestDetails?.setTextColor(ContextCompat.getColor(this, R.color.whiteColor))
                    binding?.btnContinueGuestDetails?.setTextColor(ContextCompat.getColor(this, R.color.whiteColor))
                    binding?.btnContinueGuestDetails?.setBackgroundResource(R.drawable.lets_get_started_continue_button)
                    binding?.btnContinueGuestDetails?.isEnabled = true
                    isTermsAccepted = true
                }
            }
        }
    }

    /**
     * This method used for handling the error validation
     * of input text layout
     * */
    override fun firstNameErrorEnabled(enabled: Boolean): Boolean {
        if (enabled) {
            setDefaultBackground(binding!!.tilGuestUserFirstName)
            changeHintColor(binding!!.tilGuestUserFirstName, R.color.whiteColor)
            binding!!.tilGuestUserFirstName.error = getString(R.string.error_first_name_required)
            binding!!.tilGuestUserFirstName.isErrorEnabled = true
        } else {
            binding!!.tilGuestUserFirstName.error = null
            binding!!.tilGuestUserFirstName.isErrorEnabled = false
        }
        return enabled
    }

    override fun lastNameErrorEnabled(enabled: Boolean): Boolean {
        if (enabled) {
            setDefaultBackground(binding!!.tilGuestUserLastName)
            changeHintColor(binding!!.tilGuestUserLastName, R.color.whiteColor)
            binding!!.tilGuestUserLastName.error = getString(R.string.error_last_name_required)
            binding!!.tilGuestUserLastName.isErrorEnabled = true
        } else {
            binding!!.tilGuestUserLastName.error = null
            binding!!.tilGuestUserLastName.isErrorEnabled = false
        }
        return enabled
    }

    override fun emailErrorEnabled(enabled: Boolean): Boolean {
        if (enabled) {
            setDefaultBackground(binding!!.tilGuestUserEmail)
            changeHintColor(binding!!.tilGuestUserEmail, R.color.whiteColor)
            binding!!.tilGuestUserEmail.error = getString(R.string.email_required)
            binding!!.tilGuestUserEmail.isErrorEnabled = true
        } else {
            binding!!.tilGuestUserEmail.error = null
            binding!!.tilGuestUserEmail.isErrorEnabled = false
        }
        return enabled
    }

    override fun emailValidationErrorEnabled(enabled: Boolean): Boolean {
        if (enabled) {
            setDefaultBackground(binding!!.tilGuestUserEmail)
            changeHintColor(binding!!.tilGuestUserEmail, R.color.whiteColor)
            binding!!.tilGuestUserEmail.error = getString(R.string.email_not_valid)
            binding!!.tilGuestUserEmail.isErrorEnabled = true
        } else {
            binding!!.tilGuestUserEmail.error = null
            binding!!.tilGuestUserEmail.isErrorEnabled = false
        }
        return enabled
    }

    override fun phoneNumberErrorEnabled(enabled: Boolean): Boolean {
        if (enabled) {
            setDefaultBackground(binding!!.tilGuestUserPhoneNumber)
            changeHintColor(binding!!.tilGuestUserPhoneNumber, R.color.whiteColor)
            binding!!.tilGuestUserPhoneNumber.error = getString(R.string.guest_phone_not_valid)
            binding!!.tilGuestUserPhoneNumber.isErrorEnabled = true
        } else {
            binding!!.tilGuestUserPhoneNumber.error = null
            binding!!.tilGuestUserPhoneNumber.isErrorEnabled = false
        }
        return enabled
    }

    override fun transferUserToBillingInfoScreen() {
        if (mPresenter!!.isPaymentSelectionScreenEnabled()) {
            val intent = Intent(this, PaymentTypeSelectionActivity::class.java)
            intent.putExtra(PaymentTypeSelectionActivity.StaticIntent.EXTRA_CHECKOUT_MODEL, getCheckoutModel())
            startActivity(intent)
        } else {
            val intent = Intent(this, AddNewCardPaymentActivity::class.java)
            intent.putExtra(AddNewCardPaymentActivity.StaticIntent.EXTRA_CHECKOUT_MODEL, getCheckoutModel())
            startActivity(intent)
        }
    }

    /**
     * Detach view when activity
     * is going to in destroy state
     */
    override fun onDestroy() {
        super.onDestroy()
        mPresenter!!.detachView()
    }
}
