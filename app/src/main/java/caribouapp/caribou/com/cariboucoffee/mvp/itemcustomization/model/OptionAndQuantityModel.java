package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model;

import java.io.Serializable;

/**
 * Created by asegurola on 3/22/18.
 */

public class OptionAndQuantityModel implements Serializable {
    private ModifierGroup mModifierGroup;

    private ItemModifier mSelectedItemModifier;

    private ItemOption mSelectedItemOption;

    public ModifierGroup getModifierGroup() {
        return mModifierGroup;
    }

    public void setModifierGroup(ModifierGroup modifierGroup) {
        mModifierGroup = modifierGroup;
    }

    public ItemModifier getSelectedItemModifier() {
        return mSelectedItemModifier;
    }

    public void setSelectedItemModifier(ItemModifier selectedItemModifier) {
        mSelectedItemModifier = selectedItemModifier;
    }

    public ItemOption getSelectedItemOption() {
        return mSelectedItemOption;
    }

    public void setSelectedItemOption(ItemOption selectedItemOption) {
        mSelectedItemOption = selectedItemOption;
    }
}
