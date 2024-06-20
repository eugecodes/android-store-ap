package caribouapp.caribou.com.cariboucoffee.api.model.content.ncr;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NcrCustomField implements Serializable {

    @SerializedName("name")
    private String mName;

    @SerializedName("value")
    private String mValue;

    public NcrCustomField() {
    }

    public NcrCustomField(String name, String value) {
        mName = name;
        mValue = value;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }

    @Override
    public String toString() {
        return "NcrCustomField{"
                + "Name='" + mName + '\''
                + ", Value='" + mValue + '\''
                + '}';
    }
}
