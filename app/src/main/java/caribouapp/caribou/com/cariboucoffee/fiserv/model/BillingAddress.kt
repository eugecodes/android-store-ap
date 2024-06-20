package caribouapp.caribou.com.cariboucoffee.fiserv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Swapnil on 03/10/22.
 */
class BillingAddress {
    @SerializedName("type")
    var type: String? = null

    @SerializedName("streetAddress")
    var streetAddress: String? = null

    @SerializedName("locality")
    var locality: String? = null

    @SerializedName("region")
    var region: String? = null

    @SerializedName("postalCode")
    var postalCode: String? = null

    @SerializedName("country")
    var country: String? = null

    @SerializedName("formatted")
    var formatted: String? = null

    @SerializedName("primary")
    var primary: Boolean? = false
}
