package caribouapp.caribou.com.cariboucoffee.fiserv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Swapnil on 03/10/22.
 */
class AccountTokenRequest {
    @SerializedName("account")
    var account: Account? = null

    @SerializedName("token")
    var token: TokenRequest? = null

    @SerializedName("deviceInfo")
    var deviceInfo: DeviceInfo? = null
}
