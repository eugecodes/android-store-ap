package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asegurola on 3/28/18.
 */

public class OmsGroupModifier implements Serializable {

    @SerializedName("id")
    private long mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("display_style")
    private OmsDisplayStyle mOmsDisplayStyle;

    @SerializedName("additional_charges")
    private boolean mAdditionalCharges;

    @SerializedName("custom_modifiers")
    private List<OmsCustomModifier> mCustomModifiers = new ArrayList<>();

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<OmsCustomModifier> getCustomModifiers() {
        return mCustomModifiers;
    }

    public void setCustomModifiers(List<OmsCustomModifier> customModifiers) {
        mCustomModifiers = customModifiers;
    }

    public OmsDisplayStyle getOmsDisplayStyle() {
        return mOmsDisplayStyle;
    }

    public void setOmsDisplayStyle(OmsDisplayStyle omsDisplayStyle) {
        mOmsDisplayStyle = omsDisplayStyle;
    }

    public boolean isAdditionalCharges() {
        return mAdditionalCharges;
    }

    public void setAdditionalCharges(boolean additionalCharges) {
        mAdditionalCharges = additionalCharges;
    }
}
