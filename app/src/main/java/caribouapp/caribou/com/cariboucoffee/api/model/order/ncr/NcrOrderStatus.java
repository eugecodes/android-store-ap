package caribouapp.caribou.com.cariboucoffee.api.model.order.ncr;

import com.google.gson.annotations.SerializedName;

public enum NcrOrderStatus {

    @SerializedName("InProgress")
    InProgress,

    @SerializedName("ReadyForValidation")
    ReadyForValidation,

    @SerializedName("Validated")
    Validated,

    @SerializedName("OrderPlaced")
    OrderPlaced,

    @SerializedName("ReceivedForFulfillment")
    ReceivedForFulfillment,

    @SerializedName("InFulfillment")
    InFulfillment,

    @SerializedName("ReadyForPickup")
    ReadyForPickup,

    @SerializedName("Finished")
    Finished,

    @SerializedName("Failed")
    Failed
}
