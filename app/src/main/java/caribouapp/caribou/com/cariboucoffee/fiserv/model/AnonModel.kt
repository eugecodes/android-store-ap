package caribouapp.caribou.com.cariboucoffee.fiserv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Swapnil on 03/10/22.
 */
class AnonModel {
    @SerializedName("deviceId")
    var deviceId: String? = null

    @SerializedName("isAnonymous")
    var isIsAnonymous = false
        private set

    constructor(deviceId: String?, isAnonymous: Boolean) {
        this.deviceId = deviceId
        isIsAnonymous = isAnonymous
    }

    fun setIsAnonymous(mIsAnonymous: Boolean) {
        isIsAnonymous = mIsAnonymous
    }
}
