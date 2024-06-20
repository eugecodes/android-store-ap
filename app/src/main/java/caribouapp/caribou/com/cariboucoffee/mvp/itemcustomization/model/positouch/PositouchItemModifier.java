package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.positouch;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsCustomModifier;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsEntangledModifier;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsOrderItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;

public class PositouchItemModifier extends ItemModifier {

    @SerializedName("oms")
    private OmsCustomModifier mOms;

    public PositouchItemModifier(OmsCustomModifier omsCustomModifier, List<Long> defaultOptions) {
        setId(omsCustomModifier.getId() + "");
        mOms = omsCustomModifier;
        setName(omsCustomModifier.getName());

        ItemOption defaultItemOption = null;
        for (OmsEntangledModifier omsEntangledModifier : omsCustomModifier.getEntangledModifier()) {
            ItemOption itemOption = new PositouchItemOption(omsEntangledModifier);
            if (defaultOptions.contains(Long.parseLong(itemOption.getId()))) {
                defaultItemOption = itemOption;
            }
            getOptions().add(itemOption);
        }

        setDefaultOption(defaultItemOption);
    }

    public OmsOrderItemModifier toOms() {
        OmsOrderItemModifier omsCustomModifier = new OmsOrderItemModifier();
        omsCustomModifier.setId(mOms.getId());
        omsCustomModifier.setName(mOms.getName());
        return omsCustomModifier;
    }
}
