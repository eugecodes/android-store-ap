package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation

import caribouapp.caribou.com.cariboucoffee.mvp.MvpView
import caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.model.GuestInfoModel
import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter

interface LetsGetStartedContract {
    interface View : MvpView {
        fun firstNameErrorEnabled(enabled: Boolean): Boolean
        fun lastNameErrorEnabled(enabled: Boolean): Boolean
        fun emailErrorEnabled(enabled: Boolean): Boolean
        fun emailValidationErrorEnabled(enabled: Boolean): Boolean
        fun phoneNumberErrorEnabled(enabled: Boolean): Boolean
        fun transferUserToBillingInfoScreen()
    }

    interface Presenter : MvpPresenter {
        fun letsGetStartedMessage(): String?
        fun checkGuestDataAndContinue()
        fun setModel(model: GuestInfoModel?)
        fun isPaymentSelectionScreenEnabled(): Boolean
    }
}
