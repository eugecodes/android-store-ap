package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

public enum OmsOrderStatus {

    @SerializedName("canceled")
    Canceled,

    @SerializedName("completed")
    Completed,

    @SerializedName("failed_sale_reversed")
    FailedSaleReversed,

    @SerializedName("in_progress")
    @Deprecated
    InProgress,

    @SerializedName("bounced")
    Bounced,

    @SerializedName("placed")
    Placed,

    @SerializedName("failed")
    Failed,

    @SerializedName("scheduled")
    Scheduled,

    @SerializedName("bounce_in_progress")
    BounceInProgress,

    @SerializedName("place_in_progress")
    PlaceInProgress
}
