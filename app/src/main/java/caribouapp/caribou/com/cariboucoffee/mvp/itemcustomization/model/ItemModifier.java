package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model;

import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.common.EntityWithId;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;

/**
 * Created by asegurola on 3/21/18.
 */

public class ItemModifier extends EntityWithId<String> {

    private static final String TAG = ItemModifier.class.getSimpleName();

    @SerializedName("name")
    private String mName;

    @SerializedName("options")
    private List<ItemOption> mOptions = new ArrayList<>();

    @SerializedName("defaultOption")
    private ItemOption mDefaultOption;

    @Bindable
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
        notifyPropertyChanged(BR.name);
    }

    public List<ItemOption> getOptions() {
        return mOptions;
    }

    public void setOptions(List<ItemOption> options) {
        mOptions = options;
    }

    public ItemOption getDefaultOption() {
        return mDefaultOption;
    }

    public void setDefaultOption(ItemOption defaultOption) {
        mDefaultOption = defaultOption;
    }


    public ItemOption getOptionById(String id) {
        for (ItemOption itemOption : mOptions) {
            if (id.equals(itemOption.getId())) {
                return itemOption;
            }
        }
        return null;
    }

    public void checkForDefaultOption() {
        if (mDefaultOption != null) {
            return;
        }
        if (mOptions.size() == 0) {
            Log.e(TAG, new LogErrorException("Options empty for " + mName));
            return;
        }
        mDefaultOption = mOptions.get(0); // We select the first option if there are no default ones provided by the OMS
    }

    public boolean hasAdditionalCharges() {
        for (ItemOption itemOption : getOptions()) {
            if (itemOption.isAdditionalCharges()) {
                return true;
            }
        }
        return false;
    }
}
