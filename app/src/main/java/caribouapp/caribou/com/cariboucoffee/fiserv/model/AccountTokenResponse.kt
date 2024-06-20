package caribouapp.caribou.com.cariboucoffee.fiserv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Swapnil on 03/10/22.
 */
class AccountTokenResponse {
    @SerializedName("type")
    var type: String? = null

    @SerializedName("token")
    var token: TokenResponse? = null
}
