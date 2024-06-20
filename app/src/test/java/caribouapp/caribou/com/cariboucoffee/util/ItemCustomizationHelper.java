package caribouapp.caribou.com.cariboucoffee.util;

import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;

public class ItemCustomizationHelper {

    public static void addCustomization(OrderItem orderItem, String groupId, String modifierId, String optionId) {
        Map<String, ItemOption> selection = new HashMap<>();
        ItemOption itemOption = orderItem.getModifierGroupById(groupId).getModifierById(modifierId).getOptionById(optionId);
        Assert.assertNotNull(itemOption);
        selection.put(modifierId, itemOption);
        orderItem.getCustomizations().put(groupId, selection);
    }
}
