package caribouapp.caribou.com.cariboucoffee.fiserv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Swapnil on 17/10/22.
 */
class FiservAnonResponse {
    @SerializedName("tokenId")
    var tokenId: String? = null
        private set

    @SerializedName("publicKey")
    var publicKey: String? = null
        private set

    @SerializedName("algorithm")
    var algorithm: String? = null
        private set
}
