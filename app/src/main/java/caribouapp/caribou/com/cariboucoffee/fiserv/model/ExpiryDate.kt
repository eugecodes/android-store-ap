package caribouapp.caribou.com.cariboucoffee.fiserv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Swapnil on 03/10/22.
 */
class ExpiryDate {
    @SerializedName("month")
    var month: String? = null

    @SerializedName("year")
    var year: String? = null
}
