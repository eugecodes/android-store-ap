package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

public enum AmsResponseType {
    @SerializedName("Success")
    SUCCESS,
    @SerializedName("Failure")
    FAILURE
}
