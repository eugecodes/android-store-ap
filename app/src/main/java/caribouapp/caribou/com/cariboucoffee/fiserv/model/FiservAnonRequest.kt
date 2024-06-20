package caribouapp.caribou.com.cariboucoffee.fiserv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Swapnil on 17/10/22.
 */
class FiservAnonRequest {
    @SerializedName("deviceId")
    var deviceId: String? = null
        private set

    constructor(deviceId: String?) {
        this.deviceId = deviceId
    }
}
