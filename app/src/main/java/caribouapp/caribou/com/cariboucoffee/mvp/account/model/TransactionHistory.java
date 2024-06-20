package caribouapp.caribou.com.cariboucoffee.mvp.account.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import caribouapp.caribou.com.cariboucoffee.api.model.ResponseWithHeader;

/**
 * Created by gonzalogelos on 8/9/18.
 */

public class TransactionHistory extends ResponseWithHeader implements Serializable {

    @SerializedName("transactionHistoryResult")
    private TransactionsHistoryContainer mTransactions;

    public TransactionsHistoryContainer getTransactions() {
        return mTransactions;
    }

    public void setTransactions(TransactionsHistoryContainer transactions) {
        mTransactions = transactions;
    }
}
