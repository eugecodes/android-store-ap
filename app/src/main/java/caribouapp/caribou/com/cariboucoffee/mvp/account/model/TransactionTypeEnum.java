package caribouapp.caribou.com.cariboucoffee.mvp.account.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by gonzalogelos on 8/8/18.
 */

public enum TransactionTypeEnum {
    @SerializedName("Redeem Stored Value")
    REDEEM_STORED_VALUE,
    @SerializedName("Accrual / Redemption")
    ACCRUAL_REDEMPTION,
    @SerializedName("Add Stored Value")
    ADD_STORED_VALUE,
    @SerializedName("Admin Adjustment")
    ADMIN_ADJUSTMENT,
    @SerializedName("Web Reward Purchase")
    WEB_REWARD_PURCHASE
}
