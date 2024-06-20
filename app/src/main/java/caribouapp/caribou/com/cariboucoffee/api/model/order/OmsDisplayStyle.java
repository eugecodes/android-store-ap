package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

/**
 * Created by asegurola on 3/28/18.
 */

public enum OmsDisplayStyle {

    @SerializedName("Sizes")
    SIZES,

    @SerializedName("Select One")
    SELECT_ONE,

    @SerializedName("Select One & Quantity")
    SELECT_ONE_AND_QUANTITY,

    @SerializedName("Select Multiple & Quantity")
    SELECT_MULTIPLE_AND_QUANTITY
}
