package caribouapp.caribou.com.cariboucoffee.fiserv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Swapnil on 03/10/22.
 */
class SaleRequest {
    @SerializedName("fiserv")
    var fiserv: Fiserv? = null

    @SerializedName("customer")
    var customer: Customer? = null

    @SerializedName("store")
    var store: Store? = null

    @SerializedName("ncrOrderId")
    var ncrOrderId: String? = null

    @SerializedName("chargeAmount")
    var chargeAmount: String? = null

    @SerializedName("deviceId")
    var deviceId: String? = null
}
