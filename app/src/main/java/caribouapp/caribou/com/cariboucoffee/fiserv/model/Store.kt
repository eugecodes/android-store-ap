package caribouapp.caribou.com.cariboucoffee.fiserv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Swapnil on 03/10/22.
 */
class Store {
    @SerializedName("timeZone")
    var timeZone: String? = null

    @SerializedName("mainPhone")
    var mainPhone: String? = null

    @SerializedName("address")
    var address: Address? = null
}
