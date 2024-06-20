package caribouapp.caribou.com.cariboucoffee.mvp.menu.model;

import androidx.databinding.BaseObservable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsServingNutritionalData;

/**
 * Created by jmsmuy on 6/28/18.
 */

public class NutritionalProductModel extends BaseObservable implements Serializable {

    private static final String TAG = NutritionalProductModel.class.getSimpleName();

    @SerializedName("nutritionalItemModels")
    private Map<SizeEnum, NutritionalItemModel> mNutritionalItemModels = new HashMap<>();

    @SerializedName("defaultNutritionalItemModel")
    private NutritionalItemModel mDefaultNutritionalItemModel;

    public NutritionalProductModel(List<CmsServingNutritionalData> servings) {
        for (CmsServingNutritionalData serving : servings) {
            NutritionalItemModel nutritionalItemModel = new NutritionalItemModel(serving);
            mNutritionalItemModels.put(nutritionalItemModel.getSize(), nutritionalItemModel);
        }

        if (mNutritionalItemModels.size() == 1) {
            // If we have only one nutritional entry, we use that one as the default
            mDefaultNutritionalItemModel = mNutritionalItemModels.values().iterator().next();
        } else if (mNutritionalItemModels.size() > 1) {
            // If there is more than 1 then it should have at least 3 servings, one being a medium size one and that should be our default
            mDefaultNutritionalItemModel = mNutritionalItemModels.get(SizeEnum.MEDIUM);
        }
    }

    public Map<SizeEnum, NutritionalItemModel> getNutritionalItemModels() {
        return mNutritionalItemModels;
    }

    public void setNutritionalItemModels(Map<SizeEnum, NutritionalItemModel> nutritionalItemModels) {
        mNutritionalItemModels = nutritionalItemModels;
    }

    public NutritionalItemModel getDefaultNutritionalItemModel() {
        return mDefaultNutritionalItemModel;
    }

    public void setDefaultNutritionalItemModel(NutritionalItemModel defaultNutritionalItemModel) {
        mDefaultNutritionalItemModel = defaultNutritionalItemModel;
    }
}
