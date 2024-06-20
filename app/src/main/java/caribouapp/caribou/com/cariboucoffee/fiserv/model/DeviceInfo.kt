package caribouapp.caribou.com.cariboucoffee.fiserv.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Swapnil on 03/10/22.
 */
class DeviceInfo {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("kind")
    var kind: String? = null
}
