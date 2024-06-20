package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ncr;

import java.math.BigDecimal;
import java.util.UUID;

import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrLinkItem;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderLine;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrValueType;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;

public class NcrItemModifier extends ItemModifier {

    private NcrLinkItem mNcrLinkItem;

    public NcrItemModifier(NcrLinkItem linkItem) {
        setId(linkItem.getId());
        mNcrLinkItem = linkItem;
        setName(linkItem.getName());

        NcrItemOption ncrItemOption = new NcrItemOption(linkItem);
        setDefaultOption(ncrItemOption);
        getOptions().add(ncrItemOption);
    }

    public NcrOrderLine toServerData(String parentId) {
        NcrOrderLine ncrOrderLine = new NcrOrderLine();
        ncrOrderLine.setLineId(UUID.randomUUID().toString());
        ncrOrderLine.setParentLineId(parentId);
        ncrOrderLine.setQuantity(new NcrValueType<>(1));
        ncrOrderLine.setProductId(new NcrValueType<>(mNcrLinkItem.getProductId()));
        ncrOrderLine.setDescription(mNcrLinkItem.getName());
        return ncrOrderLine;
    }

    public BigDecimal getPrice() {
        return mNcrLinkItem.getPrice();
    }

    public boolean hasAdditionalCharges() {
        return mNcrLinkItem.getPrice() != null && mNcrLinkItem.getPrice().compareTo(BigDecimal.ZERO) > 0;
    }
}
