package caribouapp.caribou.com.cariboucoffee.fiserv.model

import caribouapp.caribou.com.cariboucoffee.BuildConfig
import com.google.gson.annotations.SerializedName

/**
 * Created by Swapnil on 03/10/22.
 */
class TokenSignInRequest {
    // TODO: Remove mSourceApp param when service stops using it
    @SerializedName("sourceApp")
    private val mSourceApp = BuildConfig.SOURCE_APP

    @SerializedName("authenticate")
    private var tokenCredentials: TokenCredentials? = null

    constructor(deviceId: String?, isAnonymous: Boolean) {
        tokenCredentials = TokenCredentials()
        tokenCredentials!!.anonModel = AnonModel(deviceId, isAnonymous)
    }
}
