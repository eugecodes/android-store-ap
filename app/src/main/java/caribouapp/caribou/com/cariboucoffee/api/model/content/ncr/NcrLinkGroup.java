package caribouapp.caribou.com.cariboucoffee.api.model.content.ncr;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class NcrLinkGroup implements Serializable {

    @SerializedName("name")
    private String mName;

    @SerializedName("id")
    private String mId;

    @SerializedName("restriction")
    private NcrLinkGroupRestriction mLinkGroupRestriction;

    @SerializedName("linkedItems")
    private List<NcrLinkItem> mLinkItems;

    public String getName() {
        return mName;
    }

    public String getId() {
        return mId;
    }

    public NcrLinkGroupRestriction getLinkGroupRestriction() {
        return mLinkGroupRestriction;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setLinkGroupRestriction(NcrLinkGroupRestriction linkGroupRestriction) {
        mLinkGroupRestriction = linkGroupRestriction;
    }

    public List<NcrLinkItem> getLinkItems() {
        return mLinkItems;
    }

    public void setLinkItems(List<NcrLinkItem> linkItems) {
        mLinkItems = linkItems;
    }

    public NcrLinkItem findLinkItemById(String productId) {
        for (NcrLinkItem ncrLinkItem : mLinkItems) {
            if (ncrLinkItem.getProductId().equals(productId)) {
                return ncrLinkItem;
            }
        }
        return null;
    }
}
