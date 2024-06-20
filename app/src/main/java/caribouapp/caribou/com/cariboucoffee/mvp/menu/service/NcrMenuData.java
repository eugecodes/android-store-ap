package caribouapp.caribou.com.cariboucoffee.mvp.menu.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsStoreReward;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;

public class NcrMenuData extends MenuData {

    private Map<String, MenuCardItemModel> mSaleItemDataBySalesId = new HashMap<>();

    private List<CmsStoreReward> mApplicableRewards;

    public Map<String, MenuCardItemModel> getSalesItemDataBySalesId() {
        return mSaleItemDataBySalesId;
    }

    public void setSaleItemDataBySalesId(Map<String, MenuCardItemModel> saleItemDataBySalesId) {
        mSaleItemDataBySalesId = saleItemDataBySalesId;
    }

    public List<CmsStoreReward> getApplicableRewards() {
        return mApplicableRewards;
    }

    public void setApplicableRewards(List<CmsStoreReward> applicableRewards) {
        mApplicableRewards = applicableRewards;
    }
}
