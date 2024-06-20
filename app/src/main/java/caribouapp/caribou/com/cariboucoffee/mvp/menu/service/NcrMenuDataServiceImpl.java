package caribouapp.caribou.com.cariboucoffee.mvp.menu.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import caribouapp.caribou.com.cariboucoffee.api.CmsApi;
import caribouapp.caribou.com.cariboucoffee.api.CmsOrderApi;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsMenu;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsMenuCategory;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsMenuProduct;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsStoreReward;
import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrOmsData;
import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrSaleItem;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.RetrofitCallbackWrapper;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategory;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import retrofit2.Response;

public class NcrMenuDataServiceImpl extends MenuDataServiceImpl implements NcrMenuDataServices {

    private static final String TAG = NcrMenuDataServiceImpl.class.getSimpleName();

    private CmsOrderApi mCmsOrderApi;

    private NcrMenuData mCachedStoreMenu;

    private String mCacheKey;

    private String mNepEnterpriseUnit;

    public NcrMenuDataServiceImpl(CmsApi cmsApi, CmsOrderApi cmsOrderApi, SettingsServices settingsServices) {
        super(cmsApi, settingsServices);
        mCmsOrderApi = cmsOrderApi;
    }

    @Override
    public void getOrderAheadMenuDataFiltered(
            String locationId, String rewardId, ResultCallback<MenuData> resultCallback) {

        if (mCachedStoreMenu != null && mCacheKey.equals(locationId)) {
            if (rewardId == null) {
                resultCallback.onSuccess(mCachedStoreMenu);
                return;
            }
            resultCallback.onSuccess(filterMenuCopyForLocationProducts(mCachedStoreMenu, getUidSetForReward(mCachedStoreMenu, rewardId)));
        }

        mCmsOrderApi.getStoreMenu(locationId).enqueue(new RetrofitCallbackWrapper<CmsMenu, MenuData>(resultCallback) {
            @Override
            protected void onSuccess(Response<CmsMenu> response) {
                NcrMenuData ncrMenuData = new NcrMenuData();

                CmsMenu cmsMenu = response.body();
                mCmsOrderApi.getLocationRewards(locationId).enqueue(new RetrofitCallbackWrapper<List<CmsStoreReward>, MenuData>(resultCallback) {
                    @Override
                    protected void onSuccess(Response<List<CmsStoreReward>> productsInStoreData) {

                        for (CmsMenuCategory cmsMenuCategory : cmsMenu.getCategories()) {
                            addCategory(ncrMenuData, cmsMenuCategory);
                        }

                        mNepEnterpriseUnit = cmsMenu.getNepEnterpriseUnit();

                        calculateProductsInCategory(ncrMenuData.getCategories());
                        filterByIsOrderAheadAvailable(ncrMenuData.getCategories());
                        setOmsProdIdForCurrentLocation(ncrMenuData.getCategories());
                        ncrMenuData.setApplicableRewards(productsInStoreData.body());
                        mCachedStoreMenu = ncrMenuData;
                        mCacheKey = locationId;

                        if (rewardId == null) {
                            resultCallback.onSuccess(ncrMenuData);
                            return;
                        }

                        resultCallback.onSuccess(filterMenuCopyForLocationProducts(ncrMenuData, getUidSetForReward(mCachedStoreMenu, rewardId)));

                    }
                });

            }
        });
    }

    @Override
    public void clearCache() {
        super.clearCache();
        mCachedStoreMenu = null;
        mCacheKey = null;
        mNepEnterpriseUnit = null;
    }

    @Override
    public MenuCardItemModel getProductData(String omsProdId) {
        return mCachedStoreMenu.getMenuProductDataByOmsProdId().get(omsProdId);
    }

    @Override
    public MenuCardItemModel getProductDataBySaleItemId(String saleItemId) {
        return mCachedStoreMenu.getSalesItemDataBySalesId().get(saleItemId);
    }

    @Override
    protected void addProduct(MenuData menuData, MenuCategory menuCategory, CmsMenuProduct cmsMenuProduct) {
        super.addProduct(menuData, menuCategory, cmsMenuProduct);

        if (cmsMenuProduct.getOmsData() == null) {
            Log.e(TAG, new LogErrorException("Ncr menu product with no NCR data: " + cmsMenuProduct.getTitle()));
            return;
        }

        for (NcrSaleItem saleItem : cmsMenuProduct.getOmsData().getSalesItems()) {
            ((NcrMenuData) menuData).getSalesItemDataBySalesId().put(saleItem.getProductId(), generateMenuCardItemModel(cmsMenuProduct));
        }
    }

    private void setOmsProdIdForCurrentLocation(List<MenuCategory> categories) {
        for (MenuCategory menuCategory : categories) {
            for (MenuCardItemModel menuCardItemModel : menuCategory.getProducts()) {
                setOmsProdIdForCurrentLocation(menuCardItemModel);
            }
            setOmsProdIdForCurrentLocation(menuCategory.getSubCategories());
        }
    }

    private void setOmsProdIdForCurrentLocation(MenuCardItemModel menuCardItemModel) {
        NcrOmsData ncrOmsData = menuCardItemModel.getNcrOmsData();
        if (ncrOmsData == null) {
            Log.e(TAG, new LogErrorException("Product missing ncrData: " + menuCardItemModel.getName()));
            return;
        }
        menuCardItemModel.setOmsProdIdForCurrentLocation(ncrOmsData.getId());
    }

    private Set<String> getUidSetForReward(NcrMenuData ncrMenuData, String rewardId) {
        Set<String> uuidSet = new HashSet<>();
        for (CmsStoreReward reward : ncrMenuData.getApplicableRewards()) {
            if (reward.getRewardId().toString().equals(rewardId)) {
                uuidSet.addAll(reward.getOmsIds());
                break;
            }
        }
        return uuidSet;
    }

    public String getNepEnterpriseUnit() {
        return mNepEnterpriseUnit;
    }
}
