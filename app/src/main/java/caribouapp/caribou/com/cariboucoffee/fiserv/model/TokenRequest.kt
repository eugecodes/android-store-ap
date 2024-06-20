package caribouapp.caribou.com.cariboucoffee.fiserv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Swapnil on 03/10/22.
 */
class TokenRequest {
    @SerializedName("tokenType")
    var tokenType: String? = null
}
