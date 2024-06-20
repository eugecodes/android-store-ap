package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.order.ProductCustomizationData;

/**
 * Created by asegurola on 3/14/18.
 */

public abstract class OrderItem<TId> extends BaseObservable implements Serializable {

    private static final String TAG = OrderItem.class.getSimpleName();

    @SerializedName("id")
    private TId mId;

    @SerializedName("menuData")
    private MenuCardItemModel mMenuData;

    @SerializedName("quantity")
    private int mQuantity = 1;

    @SerializedName("size")
    private SizeEnum mSize;

    @SerializedName("price")
    private BigDecimal mPrice;

    @SerializedName("modifierGroups")
    private List<ModifierGroup> mModifierGroups = new ArrayList<>();

    @SerializedName("customizations")
    private Map<String, Map<String, ItemOption>> mCustomizations = new HashMap<>();

    public OrderItem(MenuCardItemModel productDetailsFromIntent) {
        mMenuData = productDetailsFromIntent;
    }

    public TId getId() {
        return mId;
    }

    public void setId(TId id) {
        mId = id;
    }

    @Bindable
    public MenuCardItemModel getMenuData() {
        return mMenuData;
    }

    @Bindable
    public int getQuantity() {
        return mQuantity;
    }

    /**
     * @param quantity
     * @return T/F if quantity value was changed
     */
    public boolean setQuantity(int quantity) {
        if (quantity <= getMaxQuantity()) {
            mQuantity = quantity;
            notifyPropertyChanged(BR.quantity);
            return true;
        } else {
            return false;
        }
    }


    public void setSize(SizeEnum size) {
        mSize = size;
        notifyPropertyChanged(BR.size);
        notifyPropertyChanged(BR.showOneSize);
        notifyPropertyChanged(BR.showSizeSelector);
    }

    @Bindable
    public SizeEnum getSize() {
        return mSize;
    }

    @Bindable
    public boolean isShowOneSize() {
        return mSize == SizeEnum.ONE_SIZE;
    }

    @Bindable
    public boolean isShowSizeSelector() {
        return mSize != null && mSize != SizeEnum.ONE_SIZE;
    }

    @Bindable
    public BigDecimal getPrice() {
        return mPrice;
    }

    public void setPrice(BigDecimal price) {
        mPrice = price;
        notifyPropertyChanged(BR.price);
        notifyPropertyChanged(BR.subtotal);
    }

    public List<ModifierGroup> getModifierGroups() {
        return mModifierGroups;
    }

    public Map<String, Map<String, ItemOption>> getCustomizations() {
        return mCustomizations;
    }

    @Bindable
    public BigDecimal getSubtotal() {
        return mPrice == null ? null : mPrice.multiply(new BigDecimal(mQuantity)).add(calculateAdditionalCharges());
    }

    public abstract boolean isNewItem();

    public Map<ItemModifier, ItemOption> getCustomizationForGroup(ModifierGroup modifierGroup) {
        return getCustomizationForGroup(modifierGroup, getCustomizations());
    }

    public Map<ItemModifier, ItemOption> getCustomizationForGroup(ModifierGroup modifierGroup,
                                                                  Map<String, Map<String, ItemOption>> customization) {
        Map<String, ItemOption> selection = customization.get(modifierGroup.getId());
        Map<ItemModifier, ItemOption> selectionWithData = new HashMap<>();
        if (selection == null) {
            return null;
        }
        for (Map.Entry<String, ItemOption> entry : selection.entrySet()) {
            selectionWithData.put(modifierGroup.getModifierById(String.valueOf(entry.getKey())), entry.getValue());
        }
        return selectionWithData;
    }

    public Map<ItemModifier, ItemOption> calculateDefaultPlusCustomizations(ModifierGroup modifierGroup) {
        Map<ItemModifier, ItemOption> selection = modifierGroup.calculateDefaults();

        Map<ItemModifier, ItemOption> customizations = getCustomizationForGroup(modifierGroup);
        if (customizations != null && !customizations.isEmpty()) {
            if (modifierGroup.getModifierUiStyle() == ModifierGroup.ModifierUIStyle.INLINE
                    || modifierGroup.getModifierUiStyle() == ModifierGroup.ModifierUIStyle.OPTION_PLUS_QUANTITY) {
                selection.clear();
            }
            selection.putAll(customizations);
        }
        return selection;
    }

    public ModifierGroup getModifierGroupById(String id) {
        for (ModifierGroup modifierGroup : getModifierGroups()) {
            if (id.equals(modifierGroup.getId())) {
                return modifierGroup;
            }
        }
        return null;
    }

    public BigDecimal calculateAdditionalCharges() {
        BigDecimal additionalCharges = BigDecimal.ZERO;
        for (ModifierGroup modifierGroup : mModifierGroups) {
            additionalCharges = additionalCharges.add(calculateAdditionalCharges(modifierGroup));
        }
        return additionalCharges;
    }

    public BigDecimal calculateAdditionalCharges(ModifierGroup modifierGroup) {
        BigDecimal additionalCharges = BigDecimal.ZERO;
        for (Map.Entry<ItemModifier, ItemOption> entry : calculateDefaultPlusCustomizations(modifierGroup).entrySet()) {
            // TODO not sure why the entry.getValue() itemOption is different from the existing
            // object in the group->modifier->option tree. The price is missing in the entry.getValue() object.
            ItemOption existingOption = modifierGroup.getModifierById(entry.getKey().getId()).getOptionById(entry.getValue().getId());
            BigDecimal price = existingOption.getPrice();
            if (price == null) {
                continue;
            }
            additionalCharges = additionalCharges.add(price);
        }
        return additionalCharges.multiply(new BigDecimal(getQuantity()));
    }

    public void setCustomization(ModifierGroup modifierGroup, ItemModifier selectedItemModifier, ItemOption selectedItemOption) {
        Map<String, ItemOption> selectionsForModifier = new HashMap<>();
        selectionsForModifier.put(selectedItemModifier.getId(), selectedItemOption);
        mCustomizations.put(modifierGroup.getId(), selectionsForModifier);
    }


    public void setCustomizations(ModifierGroup modifierGroup, Map<ItemModifier, ItemOption> selectedModifiersAndOptions) {
        Map<String, ItemOption> selectionsForModifier = new HashMap<>();
        mCustomizations.put(modifierGroup.getId(), selectionsForModifier);
        for (Map.Entry<ItemModifier, ItemOption> entry : selectedModifiersAndOptions.entrySet()) {
            selectionsForModifier.put(entry.getKey().getId(), entry.getValue());
        }
    }

    public int getQuantityLessThanMax() {
        return getQuantity() <= getMaxQuantity() ? getQuantity() : getMaxQuantity();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OrderItem)) {
            return false;
        }

        OrderItem<TId> orderItem = (OrderItem<TId>) obj;

        return mId != null && mId.equals(orderItem.mId);
    }

    @Override
    public int hashCode() {
        return mId == null ? 0 : mId.hashCode();
    }

    public abstract Set<SizeEnum> getAvailableSizes();

    public abstract int getMaxQuantity();

    public abstract void loadModifiers(ProductCustomizationData productCustomizationData);

}
