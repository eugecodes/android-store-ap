package caribouapp.caribou.com.cariboucoffee.api.model.order.ncr;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NcrReorderErrors implements Serializable {

    @SerializedName("type")
    private NcrReorderErrorEnum mNcrReorderErrorEnum;

    @SerializedName("product_id")
    private String mProductId;

    @SerializedName("message")
    private String mErrorMessage;
}
