package caribouapp.caribou.com.cariboucoffee.fiserv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Swapnil on 09/11/22.
 */
class Data {
    @SerializedName("deviceId")
    var deviceId: String? = null

    constructor(deviceId: String?) {
        this.deviceId = deviceId
    }

    constructor()
}
