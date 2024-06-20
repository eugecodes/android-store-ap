package caribouapp.caribou.com.cariboucoffee.api.model.content.ncr;

import androidx.core.util.Pair;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;

public class NcrSaleItem implements Serializable {

    @SerializedName("name")
    private String mName;

    @SerializedName("id")
    private String mId;

    @SerializedName("productId")
    private String mProductId;

    @SerializedName("currentPrice")
    private BigDecimal mCurrentPrice;

    @SerializedName("available")
    private boolean mAvailable;

    @SerializedName("defaultOptions")
    private List<NcrDefaultOption> mDefaultOptions = new ArrayList<>();

    @SerializedName("linkGroups")
    private List<NcrLinkGroup> mLinkGroups = new ArrayList<>();

    @SerializedName("customFields")
    private List<NcrCustomField> mCustomFields = new ArrayList<>();

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getProductId() {
        return mProductId;
    }

    public void setProductId(String productId) {
        mProductId = productId;
    }

    public BigDecimal getCurrentPrice() {
        return mCurrentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        mCurrentPrice = currentPrice;
    }

    public boolean isAvailable() {
        return mAvailable;
    }

    public void setAvailable(boolean available) {
        mAvailable = available;
    }

    public List<NcrDefaultOption> getDefaultOptions() {
        return mDefaultOptions;
    }

    public void setDefaultOptions(List<NcrDefaultOption> defaultOptions) {
        mDefaultOptions = defaultOptions;
    }

    public List<NcrLinkGroup> getLinkGroups() {
        return mLinkGroups;
    }

    public void setLinkGroups(List<NcrLinkGroup> linkGroups) {
        mLinkGroups = linkGroups;
    }

    public List<NcrCustomField> getCustomFields() {
        return mCustomFields;
    }

    public void setCustomFields(List<NcrCustomField> customFields) {
        mCustomFields = customFields;
    }

    public Integer getQuantityLimit() {
        if (mCustomFields == null) {
            return AppConstants.ORDER_AHEAD_MAX_ITEM_QUANTITY;
        } else {
            for (NcrCustomField customField : mCustomFields) {
                if (customField.getName().equalsIgnoreCase("quantityLimit")) {
                    String value = customField.getValue();
                    if (StringUtils.isInteger(value)) {
                        return Integer.parseInt(value);
                    }
                }
            }
        }
        return AppConstants.ORDER_AHEAD_MAX_ITEM_QUANTITY;
    }

    public Pair<NcrLinkGroup, NcrLinkItem> findGroupAndItemByProductId(String productId) {
        for (NcrLinkGroup ncrLinkGroup : mLinkGroups) {
            NcrLinkItem ncrLinkItem = ncrLinkGroup.findLinkItemById(productId);
            if (ncrLinkItem != null) {
                return new Pair<>(ncrLinkGroup, ncrLinkItem);
            }
        }
        return null;
    }
}
