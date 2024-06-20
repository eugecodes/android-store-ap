package caribouapp.caribou.com.cariboucoffee.order;

import android.content.Context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ModifierGroup;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ncr.NcrItemModifier;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;

public final class OrderItemUtil {

    private OrderItemUtil() {
    }

    public static List<String> getCurrentSelectionDescription(Context context, OrderItem<? extends OrderItem> orderItem, boolean showPrices) {
        List<String> modifierList = new ArrayList<>();

        for (ModifierGroup modifierGroup : orderItem.getModifierGroups()) {
            Map<ItemModifier, ItemOption> customizationForGroup = orderItem.calculateDefaultPlusCustomizations(modifierGroup);
            if (customizationForGroup == null) {
                continue;
            }
            modifierList.add(getCurrentSelectionDescriptionForModifier(context, modifierGroup, customizationForGroup, showPrices));
        }
        return modifierList;
    }

    public static String getCurrentSelectionDescriptionForModifier(Context context, ModifierGroup modifierGroup,
                                                                   Map<ItemModifier, ItemOption> orderItemSelections, boolean showPrices) {
        switch (modifierGroup.getModifierUiStyle()) {
            case INLINE: {
                NcrItemModifier itemModifier = null;
                if (orderItemSelections != null && orderItemSelections.size() > 0) {
                    itemModifier = (NcrItemModifier) orderItemSelections.keySet().iterator().next();
                }
                if (itemModifier == null) {
                    return null;
                } else {
                    String result = itemModifier.getName();
                    if (showPrices && (itemModifier.getPrice() != null && BigDecimal.ZERO.compareTo(itemModifier.getPrice()) != 0)) {
                        result += " ( + " + StringUtils.formatMoneyAmount(context, itemModifier.getPrice(), null) + " )";
                    }
                    return result;
                }
            }
            case OPTION_PLUS_QUANTITY: {
                NcrItemModifier itemModifier = null;
                ItemOption itemOption = null;
                if (orderItemSelections != null && orderItemSelections.size() > 0) {
                    itemModifier = (NcrItemModifier) orderItemSelections.keySet().iterator().next();
                    itemOption = orderItemSelections.get(itemModifier);
                }
                String result;
                if (itemOption != null && itemOption.isHideLabel()) {
                    result = itemModifier != null ? itemModifier.getName() : null;
                } else {
                    result = itemModifier != null ? StringUtils.format("%s %s", itemOption.getLabel(), itemModifier.getName()) : null;
                }
                if (showPrices && (itemModifier.getPrice() != null && BigDecimal.ZERO.compareTo(itemModifier.getPrice()) != 0)) {
                    result += " ( + " + StringUtils.formatMoneyAmount(context, itemModifier.getPrice(), null) + " )";
                }
                return result;
            }
            case MULTIPLE_QUANTITIES: {
                if (orderItemSelections == null) {
                    return null;
                }
                List<String> descriptions = new ArrayList<>();
                boolean allHide = true;
                Map<ItemModifier, ItemOption> selectionNoDefaults = filterDefaults(modifierGroup, orderItemSelections);
                for (Map.Entry<ItemModifier, ItemOption> orderItemSelection : selectionNoDefaults.entrySet()) {
                    ItemModifier itemModifier = orderItemSelection.getKey();
                    ItemOption itemOption = orderItemSelection.getValue();
                    if (itemOption != null && itemOption.isHideLabel()) {
                        descriptions.add(itemModifier != null ? itemModifier.getName() : null);
                    } else {
                        allHide = false;
                        descriptions.add(itemModifier != null
                                ? StringUtils.format("%s %s", itemOption.getLabel(), itemModifier.getName()) : null);
                    }
                }

                if (allHide) {
                    return null;
                } else {
                    return StringUtils.joinWith(descriptions, ", ");
                }
            }
        }
        return null;
    }

    private static Map<ItemModifier, ItemOption> filterDefaults(ModifierGroup modifierGroup, Map<ItemModifier, ItemOption> orderItemSelections) {
        Map<ItemModifier, ItemOption> filteredItemSelection = new HashMap<>(orderItemSelections);
        Map<ItemModifier, ItemOption> defaults = modifierGroup.calculateDefaults();
        for (Map.Entry<ItemModifier, ItemOption> defaultEntry : defaults.entrySet()) {
            ItemOption itemOption = orderItemSelections.get(defaultEntry.getKey());
            if (itemOption != null && itemOption.getId().equals(defaultEntry.getValue().getId())) {
                filteredItemSelection.remove(defaultEntry.getKey());
            }
        }
        return filteredItemSelection;
    }

}
