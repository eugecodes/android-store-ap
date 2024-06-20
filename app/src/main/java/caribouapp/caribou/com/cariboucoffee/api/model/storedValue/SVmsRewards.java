package caribouapp.caribou.com.cariboucoffee.api.model.storedValue;

import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalDate;

/**
 * Created by jmsmuy on 11/8/17.
 */

public class SVmsRewards {

    @SerializedName("code")
    private Long mCode;

    @SerializedName("name")
    private String mName;

    @SerializedName("expirationDate")
    private LocalDate mExpirationDate;

    @SerializedName("quantity")
    private Long mQuantity;

    public Long getCode() {
        return mCode;
    }

    public void setCode(Long code) {
        mCode = code;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public LocalDate getExpirationDate() {
        return mExpirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        mExpirationDate = expirationDate;
    }

    public Long getQuantity() {
        return mQuantity;
    }

    public void setQuantity(Long quantity) {
        mQuantity = quantity;
    }
}
