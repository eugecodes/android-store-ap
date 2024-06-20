package caribouapp.caribou.com.cariboucoffee.api.model.content;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CmsServingNutritionalData implements Serializable {
    @SerializedName("size")
    private String mSize;

    @SerializedName("nutrition")
    private List<CmsNutritionalDataEntry> mNutrition = new ArrayList<>();

    public String getSize() {
        return mSize;
    }

    public void setSize(String size) {
        mSize = size;
    }

    public List<CmsNutritionalDataEntry> getNutrition() {
        return mNutrition;
    }

    public void setNutrition(List<CmsNutritionalDataEntry> nutrition) {
        mNutrition = nutrition;
    }
}
