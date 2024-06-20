package caribouapp.caribou.com.cariboucoffee.mvp.account.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by gonzalogelos on 8/8/18.
 */

public class TransactionDetails implements Serializable {

    @SerializedName("description")
    private String mDescription;
    @SerializedName("type")
    private TransactionDetailsTypeEnum mTransactionDetailsType;
    @SerializedName("amount")
    private String mAmount;
    @SerializedName("walletName")
    private String mName;
    @SerializedName("balanceAfter")
    private String mBalanceAfterTransaction;
    @SerializedName("orderId")
    private String mOrderId;

    public TransactionDetailsTypeEnum getTransactionDetailsType() {
        return mTransactionDetailsType;
    }

    public void setTransactionDetailsType(TransactionDetailsTypeEnum transactionDetailsType) {
        mTransactionDetailsType = transactionDetailsType;
    }

    public String getAmount() {
        return mAmount;
    }

    public void setAmount(String amount) {
        mAmount = amount;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getBalanceAfterTransaction() {
        return mBalanceAfterTransaction;
    }

    public void setBalanceAfterTransaction(String balanceAfterTransaction) {
        mBalanceAfterTransaction = balanceAfterTransaction;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getOrderId() {
        return mOrderId;
    }

    public void setOrderId(String orderId) {
        mOrderId = orderId;
    }
}
