package caribouapp.caribou.com.cariboucoffee.mvp.menu.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsNutritionalDataEntry;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsServingNutritionalData;

/**
 * Created by jmsmuy on 10/13/17.
 */

public class NutritionalItemModel extends BaseObservable implements Serializable {

    private static final String NUTRITIONAL_ENTRY_KEY = "calories";

    @SerializedName("size")
    private SizeEnum mSize;

    @SerializedName("calories")
    private String mCalories;

    @SerializedName("nutritionalValues")
    private List<NutritionalRowData> mNutritionalValues;

    public NutritionalItemModel(CmsServingNutritionalData serving) {
        mNutritionalValues = new ArrayList<>();
        for (CmsNutritionalDataEntry nutritionalDataEntry : serving.getNutrition()) {
            NutritionalRowData nutritionalRowData = new NutritionalRowData(nutritionalDataEntry.getLabel(), nutritionalDataEntry.getValue());
            if (NUTRITIONAL_ENTRY_KEY.equalsIgnoreCase(nutritionalDataEntry.getName())) {
                mCalories = nutritionalRowData.getNutritionalValue();
            }
            mNutritionalValues.add(nutritionalRowData);
        }

        mSize = SizeEnum.fromCSV(serving.getSize());
    }


    @Bindable
    public List<NutritionalRowData> getNutritionalValues() {
        return mNutritionalValues;
    }

    public void setNutritionalValues(List<NutritionalRowData> nutritionalValues) {
        mNutritionalValues = nutritionalValues;
    }

    @Bindable
    public SizeEnum getSize() {
        return mSize;
    }

    public void setSize(SizeEnum size) {
        mSize = size;
    }

    @Bindable
    public String getCalories() {
        return mCalories;
    }

    public void setCalories(String calories) {
        mCalories = calories;
    }

}
