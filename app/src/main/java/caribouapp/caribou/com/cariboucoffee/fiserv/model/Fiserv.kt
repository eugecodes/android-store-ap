package caribouapp.caribou.com.cariboucoffee.fiserv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Swapnil on 03/10/22.
 */
class Fiserv {
    @SerializedName("clientToken")
    var clientToken: String? = null

    @SerializedName("nonceToken")
    var nonceToken: String? = null

    @SerializedName("fundingSourceType")
    var fundingSourceType: String? = null

    @SerializedName("version")
    var version: String? = null

    @SerializedName("data")
    var data: String? = null

    @SerializedName("signature")
    var signature: String? = null

    @SerializedName("billingAddress")
    var billingAddress: BillingAddress? = null

    @SerializedName("payerId")
    var payerId: String? = null

    @SerializedName("merchantId")
    var merchantId: String? = null

    @SerializedName("storeId")
    var storeId: String? = null
}
