package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

public class ServerDeliveryData implements Serializable {

    @SerializedName("address_line_1")
    private String mAddressLine1;

    @SerializedName("address_line_2")
    private String mAddressLine2;

    @SerializedName("zip_code")
    private String mZipCode;

    @SerializedName("contact")
    private String mContact;

    @SerializedName("instructions")
    private String mInstructions;

    @SerializedName("fee")
    private BigDecimal mFee;

    public String getAddressLine1() {
        return mAddressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        mAddressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return mAddressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        mAddressLine2 = addressLine2;
    }

    public String getZipCode() {
        return mZipCode;
    }

    public void setZipCode(String zipCode) {
        mZipCode = zipCode;
    }

    public String getContact() {
        return mContact;
    }

    public void setContact(String contact) {
        mContact = contact;
    }

    public String getInstructions() {
        return mInstructions;
    }

    public void setInstructions(String instructions) {
        mInstructions = instructions;
    }

    public BigDecimal getFee() {
        return mFee;
    }

    public void setFee(BigDecimal fee) {
        mFee = fee;
    }
}
