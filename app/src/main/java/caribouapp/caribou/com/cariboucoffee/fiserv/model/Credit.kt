package caribouapp.caribou.com.cariboucoffee.fiserv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Swapnil on 03/10/22.
 */
class Credit {
    @SerializedName("cardNumber")
    var cardNumber: String? = null

    @SerializedName("nameOnCard")
    var nameOnCard: String? = null

    @SerializedName("cardType")
    var cardType: String? = null

    @SerializedName("securityCode")
    var securityCode: String? = null

    @SerializedName("billingAddress")
    var billingAddress: BillingAddress? = null

    @SerializedName("expiryDate")
    var expiryDate: ExpiryDate? = null
}
