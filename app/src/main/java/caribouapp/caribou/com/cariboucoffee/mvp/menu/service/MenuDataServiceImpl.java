package caribouapp.caribou.com.cariboucoffee.mvp.menu.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import caribouapp.caribou.com.cariboucoffee.api.CmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsMenuCategory;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsMenuGroup;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsMenuProduct;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.ResultCallbackWrapper;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.RetrofitCallbackWrapper;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategory;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.NutritionalProductModel;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.ObjectCloner;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import retrofit2.Response;

/**
 * Created by asegurola on 5/3/18.
 */

public abstract class MenuDataServiceImpl implements MenuDataService {

    private static final String TAG = MenuDataServiceImpl.class.getSimpleName();

    private static final String NORTHERN_LITE_TAG = "Northern Lite";

    private static final String NL = "NL.";
    private static final String DOT = ".";
    private static final String UNDERSCORE_NL = "_NL.";

    private final CmsApi mCmsApi;

    private MenuData mCachedMenuData;

    private final SettingsServices mSettingsServices;

    public MenuDataServiceImpl(CmsApi cmsApi, SettingsServices settingsServices) {
        mCmsApi = cmsApi;
        mSettingsServices = settingsServices;
    }

    @Override
    public void clearCache() {
        mCachedMenuData = null;
    }


    public void getMenuData(ResultCallback<MenuData> resultCallback) {
        if (mCachedMenuData != null) {
            resultCallback.onSuccess(mCachedMenuData);
            return;
        }

        MenuData newModel = new MenuData();
        mCmsApi.getFilterableMenu().enqueue(new RetrofitCallbackWrapper<List<CmsMenuCategory>, MenuData>(resultCallback) {
            @Override
            protected void onSuccess(Response<List<CmsMenuCategory>> response) {
                for (CmsMenuCategory cmsMenuCategory : response.body()) {
                    addCategory(newModel, cmsMenuCategory);
                }
                mCachedMenuData = newModel;
                calculateProductsInCategory(mCachedMenuData.getCategories());
                resultCallback.onSuccess(mCachedMenuData);
            }
        });
    }

    @Override
    public void getMenuDataFiltered(ResultCallback<MenuData> resultCallback) {
        getMenuData(new ResultCallbackWrapper<MenuData>(resultCallback) {
            @Override
            public void onSuccess(MenuData menuDataToFilter) {
                MenuData menuDataCopy = deepCopyMenuData(menuDataToFilter);

                //Filter isOrderAhead attribute for menu that is not order ahead
                filterByIsOrderAheadAvailable(menuDataCopy.getCategories());
                removeEmptyCategories(menuDataCopy.getCategories());
                resultCallback.onSuccess(menuDataCopy);
            }
        });
    }

    protected void addCategory(MenuData menuData, CmsMenuCategory cmsMenuCategory) {
        MenuCategory menuCategory = new MenuCategory(cmsMenuCategory.getName());
        menuData.addCategory(menuCategory);
        if (cmsMenuCategory.getGroups() == null) {
            return;
        }
        for (CmsMenuGroup cmsMenuGroup : cmsMenuCategory.getGroups()) {
            addGroup(menuData, menuCategory, cmsMenuGroup);
        }
    }

    private void removeEmptyCategories(List<MenuCategory> menuCategories) {
        for (MenuCategory menuCategory: menuCategories) {
            menuCategory.setSubCategories(StreamSupport.stream(menuCategory.getSubCategories())
                    .filter(menuSubCategory -> !menuSubCategory.getProducts().isEmpty()).collect(Collectors.toList()));
        }
    }

    private void addGroup(MenuData menuData, MenuCategory menuParentCategory, CmsMenuGroup cmsMenuGroup) {
        MenuCategory menuCategory = new MenuCategory(cmsMenuGroup.getName());


        if (cmsMenuGroup.getProducts() == null) {
            return;
        }
        for (CmsMenuProduct cmsMenuProduct : cmsMenuGroup.getProducts()) {
            addProduct(menuData, menuCategory, cmsMenuProduct);
        }
        if (!menuCategory.getProducts().isEmpty()) {
            menuParentCategory.getSubCategories().add(menuCategory);
        }
    }

    protected void filterByIsOrderAheadAvailable(List<MenuCategory> menuCategories) {
        for (MenuCategory menuCategory : menuCategories) {
            menuCategory.setProducts(StreamSupport.stream(menuCategory.getProducts())
                    .filter(menuCardItemModel -> !menuCardItemModel.isOrderAheadMenuOnly()).collect(Collectors.toList()));

            if (menuCategory.getSubCategories() != null && !menuCategory.getSubCategories().isEmpty()) {
                filterByIsOrderAheadAvailable(menuCategory.getSubCategories());
            }
        }
    }

    protected int calculateProductsInCategory(Collection<MenuCategory> menuCategories) {
        int amountItems = 0;
        for (MenuCategory menuCategory : menuCategories) {
            menuCategory.setAmountProducts(menuCategory.getAmountProducts()
                    + menuCategory.getProducts().size()
                    + calculateProductsInCategory(menuCategory.getSubCategories()));
            amountItems = menuCategory.getAmountProducts();
        }
        return amountItems;
    }

    @Override
    public MenuCardItemModel getProductData(String omsProdId) {
        return mCachedMenuData.getMenuProductDataByOmsProdId().get(omsProdId);
    }

    protected void addProduct(MenuData menuData, MenuCategory menuCategory, CmsMenuProduct cmsMenuProduct) {
        MenuCardItemModel item = generateMenuCardItemModel(cmsMenuProduct);
        menuCategory.getProducts().add(item);
        for (String omsProdId : item.getOmsProdIdSet()) {
            menuData.getMenuProductDataByOmsProdId().put(omsProdId, item);
        }
    }

    private void processAlternateImagesForNorthernLite(MenuCardItemModel model) {
        // First we check whether "Northern Lite" is in the name
        // Then, because the enemy is within the backend, we also check the filename for
        // "_NL.png" and "NL.png"
        if (model.getImage() != null && model.getImage().contains(NL)) {
            String image = model.getImage();
            model.setImage(image.replace(UNDERSCORE_NL, DOT).replace(NL, DOT));
            model.setAltImage(image);
            image = model.getThumbnailImage();
            model.setThumbnailImage(image.replace(UNDERSCORE_NL, DOT).replace(NL, DOT));
            model.setAltThumbnailImage(image);
        } else if (model.getName().toLowerCase(Locale.US).contains(NORTHERN_LITE_TAG.toLowerCase(Locale.US))
                || model.getThumbnailImage() != null && model.getThumbnailImage().contains(NL)) {
            String image = model.getImage();
            model.setImage(image.replace(UNDERSCORE_NL, DOT).replace(NL, DOT));
            model.setAltImage(image);
            image = model.getThumbnailImage();
            model.setThumbnailImage(image.replace(UNDERSCORE_NL, DOT).replace(NL, DOT));
            model.setAltThumbnailImage(image);
        }
    }

    /**
     * Converts a CmsProduct (DTO) to the model used by the ui
     *
     * @param cmsMenuProduct
     * @return
     */
    MenuCardItemModel generateMenuCardItemModel(CmsMenuProduct cmsMenuProduct) {
        MenuCardItemModel newItem = new MenuCardItemModel(mSettingsServices);
        newItem.setThumbnailImage(cmsMenuProduct.getImageThumb());
        newItem.setImage(cmsMenuProduct.getImage());
        newItem.setName(cmsMenuProduct.getTitle());
        newItem.setDescription(cmsMenuProduct.getDescription());
        newItem.setOrderAheadMenuOnly(cmsMenuProduct.isOrderAhead());
        newItem.getAllergens().addAll(cmsMenuProduct.getAllergens());
        newItem.setPrepTimeInMins(cmsMenuProduct.getPrepTimeInMins());

        if (cmsMenuProduct.getServings() != null) {

            // Load nutritional data
            newItem.setNutritionalProductModel(new NutritionalProductModel(cmsMenuProduct.getServings()));

            // Check that there is a default serving set, if not we discard the nutritional data
            if (newItem.getDefaultNutritionalItemModel() == null) {
                Log.w(TAG, "Missing default serving for product: " + cmsMenuProduct.getTitle());
                // We discard any existing nutritional data for this product since the default serving couldn't be found.
                newItem.setNutritionalProductModel(null);
            }
        } else {
            Log.w(TAG, "Missing servings data for " + newItem.getName());
        }

        if (cmsMenuProduct.getOmsIds() != null) {
            newItem.getOmsProdIdSet().addAll(cmsMenuProduct.getOmsIds());
        }

        newItem.setNcrOmsData(cmsMenuProduct.getOmsData());

        if (newItem.getImage() == null) {
            Log.w(TAG, "Product with no Image: " + newItem.getName());
        }

        if (newItem.getThumbnailImage() == null) {
            Log.w(TAG, "Product with no ThumbnailImage: " + newItem.getName());
        }

        processAlternateImagesForNorthernLite(newItem);
        return newItem;
    }


    protected MenuData deepCopyMenuData(MenuData menuDataToFilter) {
        try {
            // TODO This is a quick and dirty solution to deep copy. We can do better.
            return ObjectCloner.deepCopy(menuDataToFilter);
        } catch (Exception e) {
            Log.e(TAG, new LogErrorException("Problems cloning menu data.", e));
            return menuDataToFilter;
        }
    }
    protected MenuData filterMenuCopyForLocationProducts(MenuData menuDataToFilter, Set<String> productUuidsToInclude) {
        MenuData menuData = deepCopyMenuData(menuDataToFilter);

        // Filter products data
        Map<String, MenuCardItemModel> filteredProdByOmsProdIdData = new HashMap<>();
        for (String uuid : productUuidsToInclude) {
            MenuCardItemModel menuCardItemModel = menuData.getMenuProductDataByOmsProdId().get(uuid);
            if (menuCardItemModel != null) {
                filteredProdByOmsProdIdData.put(uuid, menuCardItemModel);
            }
        }

        menuData.getMenuProductDataByOmsProdId().clear();
        menuData.getMenuProductDataByOmsProdId().putAll(filteredProdByOmsProdIdData);

        menuData.setCategories(filterMenuForLocation(menuData.getCategories(), productUuidsToInclude));

        return menuData;
    }

    protected List<MenuCategory> filterMenuForLocation(List<MenuCategory> categories, Set<String> productUuidsForLocation) {
        Iterator<MenuCategory> iterator = categories.iterator();
        while (iterator.hasNext()) {
            MenuCategory category = iterator.next();
            category.setAmountProducts(0);

            category.setSubCategories(filterMenuForLocation(category.getSubCategories(), productUuidsForLocation));

            for (MenuCategory menuSubCategory : category.getSubCategories()) {
                category.setAmountProducts(category.getAmountProducts() + menuSubCategory.getAmountProducts());
            }

            Iterator<MenuCardItemModel> productIterator = category.getProducts().iterator();
            while (productIterator.hasNext()) {
                MenuCardItemModel menuCardItemModel = productIterator.next();
                Log.d(TAG, "Processing product:" + menuCardItemModel.getName());

                String prodIdForLocation = null;
                // Look for the one omsProdId (if any) that is available for the current location
                for (String prodId : menuCardItemModel.getOmsProdIdSet()) {
                    if (productUuidsForLocation.contains(prodId.toLowerCase(Locale.US))) {
                        prodIdForLocation = prodId.toLowerCase(Locale.US);
                        break;
                    }
                }

                // Save the omsProdId that matched to be used later
                menuCardItemModel.setOmsProdIdForCurrentLocation(prodIdForLocation);

                if (prodIdForLocation == null) {
                    Log.d(TAG, "Product not in Location menu: " + menuCardItemModel.getOmsProdIdForCurrentLocation()
                            + " :: " + menuCardItemModel.getName());
                    productIterator.remove();
                } else {
                    Log.d(TAG, "Product included for Location : " + menuCardItemModel.getOmsProdIdForCurrentLocation()
                            + " :: " + menuCardItemModel.getName());
                }
            }

            category.setAmountProducts(category.getAmountProducts() + category.getProducts().size());

            if (category.getProducts().isEmpty() && category.getSubCategories().isEmpty()) {
                Log.d(TAG, "Removing empty product category: " + category.getName());
                iterator.remove();
            }
        }

        return categories;
    }
}
