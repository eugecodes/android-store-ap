package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by asegurola on 3/26/18.
 */

public class MultipleModifiersAndQuantitiesModel extends BaseObservable implements Serializable {
    private ModifierGroup mModifierGroup;

    private Map<ItemModifier, ItemOption> mSelectedOptions = new HashMap<>();

    @Bindable
    public ModifierGroup getModifierGroup() {
        return mModifierGroup;
    }

    public void setModifierGroup(ModifierGroup modifierGroup) {
        mModifierGroup = modifierGroup;
        notifyPropertyChanged(BR.modifierGroup);
    }

    public Map<ItemModifier, ItemOption> getSelectedOptions() {
        return mSelectedOptions;
    }

    public void setSelectedOptions(Map<ItemModifier, ItemOption> selectedOptions) {
        mSelectedOptions = new HashMap<>(selectedOptions);
    }
}
