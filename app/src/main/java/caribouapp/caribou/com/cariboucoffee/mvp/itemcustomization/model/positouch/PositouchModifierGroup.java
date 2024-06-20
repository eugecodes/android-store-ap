package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.positouch;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsCustomModifier;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsGroupModifier;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsOrderGroupModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ModifierGroup;

public class PositouchModifierGroup extends ModifierGroup {

    @SerializedName("oms")
    private OmsGroupModifier mOms;

    public PositouchModifierGroup(OmsGroupModifier omsGroupModifier, List<Long> defaultOptions) {

        mOms = omsGroupModifier;

        setId(omsGroupModifier.getId() + "");
        setName(omsGroupModifier.getName());
        setAdditionalCharges(omsGroupModifier.isAdditionalCharges());

        ModifierUIStyle modifierUIStyle = null;
        switch (omsGroupModifier.getOmsDisplayStyle()) {
            case SIZES:
                modifierUIStyle = ModifierUIStyle.SIZE;
                break;
            case SELECT_ONE:
                modifierUIStyle = ModifierUIStyle.INLINE;
                break;
            case SELECT_ONE_AND_QUANTITY:
                modifierUIStyle = ModifierUIStyle.OPTION_PLUS_QUANTITY;
                break;
            case SELECT_MULTIPLE_AND_QUANTITY:
                modifierUIStyle = ModifierUIStyle.MULTIPLE_QUANTITIES;
                break;
        }
        setModifierUiStyle(modifierUIStyle);

        ItemModifier defaultItemModifier = null;
        for (OmsCustomModifier omsCustomModifier : omsGroupModifier.getCustomModifiers()) {
            ItemModifier itemModifier = new PositouchItemModifier(omsCustomModifier, defaultOptions);
            if (modifierUIStyle.equals(ModifierUIStyle.MULTIPLE_QUANTITIES)) {
                itemModifier.checkForDefaultOption();
            } else if (itemModifier.getDefaultOption() != null || defaultItemModifier == null) {
                defaultItemModifier = itemModifier;
            }
            getItemModifiers().add(itemModifier);
        }
        setDefaultItemModifier(defaultItemModifier);
    }

    public OmsOrderGroupModifier toOms() {
        OmsOrderGroupModifier omsGroupModifier = new OmsOrderGroupModifier();
        omsGroupModifier.setId(mOms.getId());
        omsGroupModifier.setName(mOms.getName());
        omsGroupModifier.setOmsDisplayStyle(mOms.getOmsDisplayStyle());
        return omsGroupModifier;
    }
}
