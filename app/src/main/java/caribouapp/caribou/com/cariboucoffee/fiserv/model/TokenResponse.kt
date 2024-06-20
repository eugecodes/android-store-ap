package caribouapp.caribou.com.cariboucoffee.fiserv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Swapnil on 03/10/22.
 */
class TokenResponse {
    @SerializedName("tokenType")
    var tokenType: String? = null
        private set

    @SerializedName("tokenId")
    var tokenId: String? = null
        private set

    @SerializedName("tokenProvider")
    var tokenProvider: String? = null
        private set
}
