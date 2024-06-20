package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.model

import android.os.Parcelable
import caribouapp.caribou.com.cariboucoffee.AppConstants
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class PaymentTypeData : Parcelable {
    abstract val type: String

    data class GooglePay(
        val data: String,
        override val type: String = AppConstants.GPAY,
    ) : PaymentTypeData()

    data class PayPal(
        val payerId: String,
        val accountNonce: String,
        override val type: String = AppConstants.PAYPAL,
    ) : PaymentTypeData()

    data class Venmo(
        val accountNonce: String,
        override val type: String = AppConstants.VENMO,
    ) : PaymentTypeData()

    data class Credit(
        val nonceToken: String,
        override val type: String = AppConstants.CREDIT,
    ) : PaymentTypeData()
}
