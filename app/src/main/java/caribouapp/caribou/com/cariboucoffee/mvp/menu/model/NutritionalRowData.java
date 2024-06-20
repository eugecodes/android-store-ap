package caribouapp.caribou.com.cariboucoffee.mvp.menu.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jmsmuy on 11/20/17.
 */

public class NutritionalRowData implements Serializable {

    @SerializedName("nutritionalName")
    private String mNutritionalName;

    @SerializedName("nutritionalValue")
    private String mNutritionalValue;

    public NutritionalRowData(String name, String value) {
        mNutritionalName = name;
        mNutritionalValue = value;
    }

    public String getNutritionalName() {
        return mNutritionalName;
    }

    public void setNutritionalName(String nutritionalName) {
        mNutritionalName = nutritionalName;
    }

    public String getNutritionalValue() {
        return mNutritionalValue;
    }

    public void setNutritionalValue(String nutritionalValue) {
        mNutritionalValue = nutritionalValue;
    }
}
