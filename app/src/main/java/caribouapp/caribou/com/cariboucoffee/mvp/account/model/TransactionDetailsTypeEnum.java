package caribouapp.caribou.com.cariboucoffee.mvp.account.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gonzalogelos on 8/8/18.
 */

public enum TransactionDetailsTypeEnum {
    @SerializedName("Accrued")
    ACCRUED,
    @SerializedName("Redeemed")
    REDEEM
}
