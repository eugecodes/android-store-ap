package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OmsOrderGroupModifier implements Serializable {
    @SerializedName("id")
    private long mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("display_style")
    private OmsDisplayStyle mOmsDisplayStyle;

    @SerializedName("modifiers")
    private List<OmsOrderItemModifier> mModifiers = new ArrayList<>();

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

    public OmsDisplayStyle getOmsDisplayStyle() {
        return mOmsDisplayStyle;
    }

    public void setOmsDisplayStyle(OmsDisplayStyle omsDisplayStyle) {
        mOmsDisplayStyle = omsDisplayStyle;
    }

    public List<OmsOrderItemModifier> getModifiers() {
        return mModifiers;
    }

    public void setModifiers(List<OmsOrderItemModifier> modifiers) {
        mModifiers = modifiers;
    }

}
