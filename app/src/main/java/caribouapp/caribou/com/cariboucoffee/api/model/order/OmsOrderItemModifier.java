package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OmsOrderItemModifier implements Serializable {
    @SerializedName("id")
    private long mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("option")
    private OmsOrderItemOption mOption;

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

    public OmsOrderItemOption getOption() {
        return mOption;
    }

    public void setOption(OmsOrderItemOption option) {
        mOption = option;
    }
}
