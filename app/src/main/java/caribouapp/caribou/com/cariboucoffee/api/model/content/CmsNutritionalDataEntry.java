package caribouapp.caribou.com.cariboucoffee.api.model.content;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CmsNutritionalDataEntry implements Serializable {

    @SerializedName("name")
    private String mName;

    @SerializedName("label")
    private String mLabel;

    @SerializedName("value")
    private String mValue;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }
}
