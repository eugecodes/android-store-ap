package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ncr;

import com.google.gson.annotations.SerializedName;

import java.util.Set;

import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrLinkGroup;
import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrLinkItem;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ModifierGroup;

public class NcrModifierGroup extends ModifierGroup {

    @SerializedName("ncrLinkGroup")
    private NcrLinkGroup mNcrLinkGroup;

    public NcrModifierGroup(NcrLinkGroup ncrLinkGroup, Set<String> defaultLinkItems) {
        mNcrLinkGroup = ncrLinkGroup;

        setId(ncrLinkGroup.getId());
        setName(ncrLinkGroup.getName());

        for (NcrLinkItem linkItem : ncrLinkGroup.getLinkItems()) {
            NcrItemModifier ncrItemModifier = new NcrItemModifier(linkItem);
            getItemModifiers().add(ncrItemModifier);
            if (defaultLinkItems.contains(linkItem.getId())) {
                setDefaultItemModifier(ncrItemModifier);
            }
        }

        setModifierUiStyle(ModifierUIStyle.INLINE);


        boolean additionalCharges = false;
        for (ItemModifier itemModifier : getItemModifiers()) {
            if (itemModifier.hasAdditionalCharges()) {
                additionalCharges = true;
                break;
            }
        }
        setAdditionalCharges(additionalCharges);
    }
}
