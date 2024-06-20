package caribouapp.caribou.com.cariboucoffee.mvp.account.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gonzalogelos on 8/9/18.
 */

public class TransactionsHistoryContainer implements Serializable {

    @SerializedName("transactions")
    private List<TransactionModel> mTransactionModelList;

    public List<TransactionModel> getTransactionModelList() {
        return mTransactionModelList;
    }

    public void setTransactionModelList(List<TransactionModel> transactionModelList) {
        mTransactionModelList = transactionModelList;
    }
}
