package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.common.EntityWithId;

/**
 * Created by asegurola on 3/19/18.
 */

public abstract class ModifierGroup extends EntityWithId<String> {

    public enum ModifierUIStyle {

        @SerializedName("size")
        SIZE,

        @SerializedName("inline")
        INLINE,

        @SerializedName("optionPlusQuantity")
        OPTION_PLUS_QUANTITY,

        @SerializedName("multipleQuantities")
        MULTIPLE_QUANTITIES
    }

    @SerializedName("name")
    private String mName;

    @SerializedName("modifierUiStyle")
    private ModifierUIStyle mModifierUiStyle;

    @SerializedName("itemModifiers")
    private List<ItemModifier> mItemModifiers = new ArrayList<>();

    @SerializedName("defaultItemModifier")
    private ItemModifier mDefaultItemModifier;

    @SerializedName("additionalCharges")
    private boolean mAdditionalCharges;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public ModifierUIStyle getModifierUiStyle() {
        return mModifierUiStyle;
    }

    public void setModifierUiStyle(ModifierUIStyle modifierUiStyle) {
        mModifierUiStyle = modifierUiStyle;
    }

    public List<ItemModifier> getItemModifiers() {
        return mItemModifiers;
    }

    public void setItemModifiers(List<ItemModifier> itemModifiers) {
        mItemModifiers = itemModifiers;
    }

    public ItemModifier getDefaultItemModifier() {
        return mDefaultItemModifier;
    }

    public void setDefaultItemModifier(ItemModifier defaultItemModifier) {
        mDefaultItemModifier = defaultItemModifier;
    }


    public ItemModifier getModifierByName(String name) {
        for (ItemModifier modifier : mItemModifiers) {
            if (modifier.getName().equalsIgnoreCase(name)) {
                return modifier;
            }
        }
        return null;
    }

    public ItemModifier getModifierById(String id) {
        for (ItemModifier itemModifier : mItemModifiers) {
            if (id.equals(itemModifier.getId())) {
                return itemModifier;
            }
        }
        return null;
    }

    public boolean isAdditionalCharges() {
        return mAdditionalCharges;
    }

    public void setAdditionalCharges(boolean additionalCharges) {
        mAdditionalCharges = additionalCharges;
    }

    public Map<ItemModifier, ItemOption> calculateDefaults() {
        Map<ItemModifier, ItemOption> defaults = new HashMap<>();

        switch (mModifierUiStyle) {
            case INLINE:
            case SIZE:
            case OPTION_PLUS_QUANTITY:
                defaults.put(getDefaultItemModifier(), getDefaultItemModifier().getDefaultOption());
                break;
            case MULTIPLE_QUANTITIES:
                for (ItemModifier itemModifier : mItemModifiers) {
                    defaults.put(itemModifier, itemModifier.getDefaultOption());
                }
                break;
        }

        return defaults;
    }
}
