package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.presenter

import caribouapp.caribou.com.cariboucoffee.AppConstants.VALID_EMAIL_ADDRESS_REGEX
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.LetsGetStartedContract
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.model.GuestInfoModel
import caribouapp.caribou.com.cariboucoffee.util.StringUtils
import caribouapp.caribou.com.cariboucoffee.util.ValidationUtils
import javax.inject.Inject

class LetsGetStartedPresenter(
    view: LetsGetStartedContract.View?,
    private var mModel: GuestInfoModel
) : BasePresenter<LetsGetStartedContract.View?>(view), LetsGetStartedContract.Presenter {
    @JvmField
    @Inject
    var mUserServices: UserServices? = null

    @JvmField
    @Inject
    var mSettingsServices: SettingsServices? = null

    /**
     * Return String from Let Get Started pop-up
     * configured title on screen
     * */
    override fun letsGetStartedMessage(): String? {
        return mSettingsServices!!.letsGetStartedMessage
    }

    /**
     * This method call when we click CONTINUE button from UI
     * First we check the validation and then proceed further
     * */
    override fun checkGuestDataAndContinue() {
        val errorDetected = ((
            view!!.firstNameErrorEnabled(mModel.guestFirstName.isNullOrEmpty())
                or view!!.lastNameErrorEnabled(mModel.guestLastName.isNullOrEmpty())
                or view!!.phoneNumberErrorEnabled(isPhoneNumberInvalid())
                or view!!.emailErrorEnabled(mModel.guestEmailId.isNullOrEmpty())
            ) || (
            view!!.emailValidationErrorEnabled(!checkMail())
            ))

        if (errorDetected) {
            return
        }
        /**
         * here we are storing data into memory
         * */
        mUserServices?.guestUser = mModel
        view!!.transferUserToBillingInfoScreen()
    }

    override fun setModel(model: GuestInfoModel?) {
        if (model != null) {
            mModel = model
        }
    }

    override fun isPaymentSelectionScreenEnabled(): Boolean {
        return mSettingsServices!!.isPaymentSelectionScreenEnabled
    }

    /**
     * Logic for validate email
     * */
    private fun checkMail(): Boolean {
        val email: String? = mModel.guestEmailId
        return email != null && email.matches(VALID_EMAIL_ADDRESS_REGEX.toRegex(RegexOption.IGNORE_CASE))
    }

    private fun isPhoneNumberInvalid(): Boolean {
        val phoneNumber: String = StringUtils.toPhoneNumberWithoutSymbols(mModel.guestPhoneNumber)
        return phoneNumber.isNotBlank() && !ValidationUtils.isValidPhoneNumber(phoneNumber)
    }
}
