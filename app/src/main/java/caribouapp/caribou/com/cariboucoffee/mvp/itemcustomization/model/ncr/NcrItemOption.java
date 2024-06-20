package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ncr;

import java.math.BigDecimal;

import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrLinkItem;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;

public class NcrItemOption extends ItemOption {

    private NcrLinkItem mNcrLinkItem;

    public NcrItemOption(NcrLinkItem linkItem) {
        mNcrLinkItem = linkItem;
        setId(linkItem.getId());
        setLabel(linkItem.getName());
        setAdditionalCharges(linkItem.getPrice() != null && linkItem.getPrice().compareTo(BigDecimal.ZERO) > 0);
        setHideLabel(false);
        setPrice(linkItem.getPrice());
    }
}
