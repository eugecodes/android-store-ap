package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.positouch;

import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsEntangledModifier;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsOrderItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;

public class PositouchItemOption extends ItemOption {

    public PositouchItemOption(OmsEntangledModifier omsEntangledModifier) {
        setId(omsEntangledModifier.getId() + "");
        setLabel(omsEntangledModifier.getLabel());
        setAdditionalCharges(omsEntangledModifier.isAdditionalCharges());
        setHideLabel(omsEntangledModifier.isHideLabel());
    }


    public OmsOrderItemOption toOms() {
        OmsOrderItemOption omsEntangledModifier = new OmsOrderItemOption();
        omsEntangledModifier.setId(Long.parseLong(getId()));
        omsEntangledModifier.setLabel(getLabel());
        return omsEntangledModifier;
    }
}
