package caribouapp.caribou.com.cariboucoffee.fiserv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Swapnil on 03/10/22.
 */
class MoveLoyaltyRequest {
    @SerializedName("data")
    private var data: Data? = null

    constructor(deviceId: String?) {
        data = Data()
        data?.deviceId = deviceId
    }
}
