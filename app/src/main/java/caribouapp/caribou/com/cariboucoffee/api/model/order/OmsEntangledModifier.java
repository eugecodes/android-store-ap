package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by asegurola on 3/28/18.
 */

public class OmsEntangledModifier implements Serializable {

    @SerializedName("id")
    private long mId;

    @SerializedName("label")
    private String mLabel;

    @SerializedName("additional_charges")
    private boolean mAdditionalCharges;

    @SerializedName("hide_label")
    private boolean mHideLabel;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public boolean isAdditionalCharges() {
        return mAdditionalCharges;
    }

    public void setAdditionalCharges(boolean additionalCharges) {
        mAdditionalCharges = additionalCharges;
    }

    public boolean isHideLabel() {
        return mHideLabel;
    }

    public void setHideLabel(boolean hideLabel) {
        mHideLabel = hideLabel;
    }
}
