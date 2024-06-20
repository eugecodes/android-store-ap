package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ncr;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrDefaultOption;
import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrLinkGroup;
import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrOmsData;
import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrSaleItem;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderLine;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrValueType;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ModifierGroup;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.order.ProductCustomizationData;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;

public class NcrOrderItem extends OrderItem<String> {

    private static final String TAG = NcrOrderItem.class.getSimpleName();

    @SerializedName("perSizeNcrSalesItems")
    private Map<SizeEnum, NcrSaleItem> mPerSizeNcrSalesItems = new HashMap<>();

    public NcrOrderItem(MenuCardItemModel productDetailsFromIntent) {
        super(productDetailsFromIntent);
    }

    @Override
    public void loadModifiers(ProductCustomizationData productCustomizationData) {
        NcrOmsData ncrOmsData = (NcrOmsData) productCustomizationData;

        if (ncrOmsData.getSalesItems().size() == 1) {
            mPerSizeNcrSalesItems.put(SizeEnum.ONE_SIZE, ncrOmsData.getSalesItems().get(0));
            setSize(SizeEnum.ONE_SIZE);
        } else {
            // Search for the default SalesItem.
            NcrSaleItem defautlNcrSaleItem = null;
            for (NcrSaleItem ncrSaleItem : ncrOmsData.getSalesItems()) {
                if (ncrSaleItem.getId().equals(ncrOmsData.getDefaultSalesItemId())) {
                    defautlNcrSaleItem = ncrSaleItem;
                }
                mPerSizeNcrSalesItems.put(SizeEnum.fromOmsOrderName(ncrSaleItem.getName()), ncrSaleItem);
            }

            // If there is no default SalesItem we assume that we should use the first one
            if (defautlNcrSaleItem == null) {
                defautlNcrSaleItem = ncrOmsData.getSalesItems().get(0);
            }
            setSize(SizeEnum.fromOmsOrderName(defautlNcrSaleItem.getName()));
        }
    }

    void setPerSizeNcrSalesItems(Map<SizeEnum, NcrSaleItem> perSizeNcrSalesItems) {
        mPerSizeNcrSalesItems = perSizeNcrSalesItems;
    }

    @Override
    public Set<SizeEnum> getAvailableSizes() {
        return mPerSizeNcrSalesItems.keySet();
    }

    public int getMaxQuantity() {
        if (mPerSizeNcrSalesItems != null && getSize() != null && mPerSizeNcrSalesItems.get(getSize()) != null) {
            return mPerSizeNcrSalesItems.get(getSize()).getQuantityLimit();
        } else {
            return AppConstants.ORDER_AHEAD_MAX_ITEM_QUANTITY;
        }
    }


    @Override
    public void setSize(SizeEnum size) {
        if (size == getSize()) {
            return;
        }
        NcrSaleItem ncrSaleItem = Objects.requireNonNull(mPerSizeNcrSalesItems.get(size));
        loadModifiersFromNcrSaleItem(ncrSaleItem);
        setPrice(ncrSaleItem.getCurrentPrice());
        super.setSize(size);
    }

    @Override
    public boolean isShowOneSize() {
        return false;
    }

    private void loadModifiersFromNcrSaleItem(NcrSaleItem ncrSaleItem) {
        //Save the previous customization and group modifier for later try to apply them to the new size
        Map<String, Map<String, ItemOption>> previousCustomization = new HashMap<>(getCustomizations());
        List<ModifierGroup> previousModifierGroupList = new ArrayList<>(getModifierGroups());
        getModifierGroups().clear();
        getCustomizations().clear();

        for (NcrLinkGroup linkGroup : ncrSaleItem.getLinkGroups()) {
            Set<String> defaultItemLinkIds = new HashSet<>();
            for (NcrDefaultOption ncrDefaultOption : ncrSaleItem.getDefaultOptions()) {
                if (ncrDefaultOption.getLinkGroupId().equals(linkGroup.getId())) {
                    defaultItemLinkIds.add(ncrDefaultOption.getSalesItemId());
                }
            }

            NcrModifierGroup ncrModifierGroup = new NcrModifierGroup(linkGroup, defaultItemLinkIds);
            if (ncrModifierGroup.getDefaultItemModifier() == null) {
                // we skip group modifiers with no default modifier
                continue;
            }

            getModifierGroups().add(ncrModifierGroup);
        }
        applyPreviousItemCustomizationToCurrentOne(previousCustomization, previousModifierGroupList);


    }

    private void applyPreviousItemCustomizationToCurrentOne(Map<String, Map<String, ItemOption>> previousCustomization,
                                                            List<ModifierGroup> previousModifierGroupList) {
        if (previousModifierGroupList.isEmpty() || previousCustomization.isEmpty()) {
            return;
        }
        //Try to match previous customization to new customization, for this happen we need to pass this is 3 comparision :
        //By ModifierGroup name
        //Then by ItemModifier name
        //And by item option label
        for (ModifierGroup currentModifierGroup : getModifierGroups()) {
            // If GroupModifier name are the same, now we need to compare itemModifier name
            ModifierGroup previousModifierGroup = findGroupModifierByName(currentModifierGroup.getName(), previousModifierGroupList);
            if (previousModifierGroup == null) {
                continue;
            }
            Map<ItemModifier, ItemOption> previousCustomizationForModifier =
                    getCustomizationForGroup(previousModifierGroup, previousCustomization);
            // If there was no selection on this group modifier this will be null
            if (previousCustomizationForModifier == null) {
                continue;
            }
            matchCustomizationToGroupModifier(currentModifierGroup, previousCustomizationForModifier);


        }
    }

    private void matchCustomizationToGroupModifier(ModifierGroup currentGroupModifier, Map<ItemModifier, ItemOption> previousCustomization) {
        //Compare the old customization selection to the current group modifier items and item option by name
        for (Map.Entry<ItemModifier, ItemOption> previousCustomizationEntry : previousCustomization.entrySet()) {
            ItemModifier currentItemModifier = findItemModifierByName(previousCustomizationEntry.getKey().getName(),
                    currentGroupModifier.getItemModifiers());
            if (currentItemModifier == null) {
                continue;
            }
            ItemOption currentItemOption = findItemOptionByName(previousCustomizationEntry.getValue().getLabel(),
                    currentItemModifier.getOptions());
            if (currentItemOption == null) {
                continue;
            }
            try {
                setCustomization(currentGroupModifier, currentItemModifier, currentItemOption);
            } catch (RuntimeException e) {
                Log.e(TAG, "Couldn't load this customization" + e.getMessage());
            }
        }

    }


    private ModifierGroup findGroupModifierByName(String groupModifierName, List<ModifierGroup> groupModifierList) {
        for (ModifierGroup modifierGroup : groupModifierList) {
            if (StringUtils.compareSimilarStrings(modifierGroup.getName(), groupModifierName)) {
                return modifierGroup;
            }
        }
        return null;
    }

    private ItemModifier findItemModifierByName(String itemModifierName, List<ItemModifier> itemModifierList) {
        for (ItemModifier itemModifier : itemModifierList) {
            if (StringUtils.compareSimilarStrings(itemModifier.getName(), itemModifierName)) {
                return itemModifier;
            }
        }
        return null;
    }

    private ItemOption findItemOptionByName(String itemOptionName, List<ItemOption> itemOptionList) {
        for (ItemOption itemOption : itemOptionList) {
            if (StringUtils.compareSimilarStrings(itemOption.getLabel(), itemOptionName)) {
                return itemOption;
            }
        }
        return null;
    }

    public Map<SizeEnum, NcrSaleItem> getPerSizeNcrSalesItems() {
        return mPerSizeNcrSalesItems;
    }

    @Override
    public boolean isNewItem() {
        return getId() == null;
    }

    public List<NcrOrderLine> toServerData() {
        List<NcrOrderLine> lines = new ArrayList<>();

        NcrOrderLine ncrOrderLine = new NcrOrderLine();
        ncrOrderLine.setDescription(getMenuData().getName());
        ncrOrderLine.setLineId(getId());
        ncrOrderLine.setProductId(new NcrValueType<>(getSaleItem().getProductId()));
        ncrOrderLine.setQuantity(new NcrValueType<>(getQuantity()));
        lines.add(ncrOrderLine);


        // Add modifiers
        for (ModifierGroup modifierGroup : getModifierGroups()) {
            for (ItemModifier itemModifier : calculateDefaultPlusCustomizations(modifierGroup).keySet()) {
                NcrItemModifier ncrItemModifier = (NcrItemModifier) itemModifier;
                lines.add(ncrItemModifier.toServerData(getId()));
            }
        }

        return lines;
    }

    private NcrSaleItem getSaleItem() {
        return mPerSizeNcrSalesItems.get(getSize());
    }
}
