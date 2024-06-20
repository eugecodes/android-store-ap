package caribouapp.caribou.com.cariboucoffee.api.model.order.ncr;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class NcrOrderApiData {

    @SerializedName(value = "Id", alternate = "id")
    private String mNcrOrderId;

    @SerializedName(value = "OrderLines", alternate = "orderLines")
    private List<NcrOrderLine> mNcrOrderLines = new ArrayList<>();

    @SerializedName(value = "ReferenceId", alternate = "referenceId")
    private String mReferenceId;

    @SerializedName(value = "Source", alternate = "source")
    private String mSource;

    @SerializedName(value = "Status", alternate = "status")
    private NcrOrderStatus mStatus;

    @SerializedName(value = "TaxExempt", alternate = "taxExempt")
    private boolean mTaxExempt;

    @SerializedName(value = "Customer", alternate = "customer")
    private NcrCustomer mCustomer;

    public List<NcrOrderLine> getNcrOrderLines() {
        return mNcrOrderLines;
    }

    public void setNcrOrderLines(List<NcrOrderLine> ncrOrderLines) {
        mNcrOrderLines = ncrOrderLines;
    }

    public String getReferenceId() {
        return mReferenceId;
    }

    public void setReferenceId(String referenceId) {
        mReferenceId = referenceId;
    }

    public String getSource() {
        return mSource;
    }

    public void setSource(String source) {
        mSource = source;
    }

    public NcrOrderStatus getStatus() {
        return mStatus;
    }

    public void setStatus(NcrOrderStatus status) {
        mStatus = status;
    }

    public boolean isTaxExempt() {
        return mTaxExempt;
    }

    public void setTaxExempt(boolean taxExempt) {
        mTaxExempt = taxExempt;
    }

    public NcrCustomer getCustomer() {
        return mCustomer;
    }

    public void setCustomer(NcrCustomer customer) {
        mCustomer = customer;
    }

    public String getNcrOrderId() {
        return mNcrOrderId;
    }

    public void setNcrOrderId(String ncrOrderId) {
        mNcrOrderId = ncrOrderId;
    }
}
