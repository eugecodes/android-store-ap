package caribouapp.caribou.com.cariboucoffee.mvp.account.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gonzalogelos on 8/1/18.
 */

public class TransactionModel extends BaseObservable implements Serializable {

    @SerializedName("id")
    private Long mId;
    @SerializedName("transactionDate")
    private DateTime mTransactionDate;
    @SerializedName("storeName")
    private String mTransactionStoreName;
    @SerializedName("storeCode")
    private String mStoreCode;
    @SerializedName("storeType")
    private String mStoreType;
    @SerializedName("transactionType")
    private TransactionTypeEnum mTransactionType;
    @SerializedName("details")
    private List<TransactionDetails> mTransactionDetailsList;

    public DateTime getTransactionDate() {
        return mTransactionDate;
    }

    public void setTransactionDate(DateTime transactionDate) {
        mTransactionDate = transactionDate;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    @Bindable
    public String getTransactionStoreName() {
        return mTransactionStoreName;
    }

    public void setTransactionStoreName(String transactionStoreName) {
        mTransactionStoreName = transactionStoreName;
        notifyPropertyChanged(BR.transactionStoreName);
    }

    public String getStoreCode() {
        return mStoreCode;
    }

    public void setStoreCode(String storeCode) {
        mStoreCode = storeCode;
    }

    public String getStoreType() {
        return mStoreType;
    }

    public void setStoreType(String storeType) {
        mStoreType = storeType;
    }

    public TransactionTypeEnum getTransactionType() {
        return mTransactionType;
    }

    public void setTransactionType(TransactionTypeEnum transactionType) {
        mTransactionType = transactionType;
    }

    public List<TransactionDetails> getTransactionDetailsList() {
        return mTransactionDetailsList;
    }

    public void setTransactionDetailsList(List<TransactionDetails> transactionDetailsList) {
        mTransactionDetailsList = transactionDetailsList;
    }
}
