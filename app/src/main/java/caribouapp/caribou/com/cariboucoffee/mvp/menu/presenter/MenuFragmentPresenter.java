package caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter;

import android.text.TextUtils;

import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.MenuContract;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardHeaderModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategory;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategoryModel;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;

/**
 * Created by jmsmuy on 2/9/18.
 */

public class MenuFragmentPresenter extends BasePresenter<MenuContract.Fragments.View> implements MenuContract.Fragments.Presenter {

    private static final String TAG = MenuFragmentPresenter.class.getSimpleName();

    @Inject
    EventLogger mEventLogger;

    @Inject
    OrderService mOrderService;

    MenuCategoryModel mMenuModel;

    public MenuFragmentPresenter(MenuContract.Fragments.View view, MenuCategoryModel model) {
        super(view);
        mMenuModel = model == null ? new MenuCategoryModel() : model;
    }

    private Observable.OnPropertyChangedCallback mOnPropertyChangedCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (getView() == null) {
                return;
            }

            if (propertyId == BR.selectedLvl2Filter) {
                applyLevel2Filter(mMenuModel.getSelectedLvl2Filter());
            }
        }
    };

    /**
     * Updates cards when a toggle button is pressed
     *
     * @param category selected level 2 filter
     */
    @Override
    public void applyLevel2Filter(MenuCategory category) {

        // Load default selected Lvl2 filter (picking the first option if available)
        getView().updateSelectedLvl2Filter(category);

        resetLevel3FiltersAndSearchText();
        updateLevel3FiltersAndApplyThem();
        applyFiltersToData();
        mEventLogger.logMenuFilterLevel2(category);
        getView().level3FiltersButtonEnabled(hasLevel3Categories(mMenuModel));
    }

    @Override
    public void setData(MenuCategoryModel menuCategoryModel) {
        if (menuCategoryModel == null) {
            return;
        }
        mMenuModel = menuCategoryModel;
        mMenuModel.addOnPropertyChangedCallback(mOnPropertyChangedCallback);
        setupFilters();
    }

    private boolean hasLevel3Categories(MenuCategoryModel menuModel) {
        if (mMenuModel == null || menuModel.getCategory() == null
                || mMenuModel.getCategory().getSubCategories() == null
                || mMenuModel.getCategory().getSubCategories().isEmpty()) {
            return false;
        }

        MenuCategory selectedLevel2 = menuModel.getSelectedLvl2Filter();
        return selectedLevel2 != null
                && selectedLevel2.getSubCategories() != null
                && !selectedLevel2.getSubCategories().isEmpty();
    }


    /**
     * Applies the filters to the data, also separates if this is a featured or not tab
     */
    private void applyFiltersToData() {
        if (getView() == null) {
            return;
        }

        List<MenuCardModel> flattenedModel = flattenAndFilterModel(mMenuModel);

        if (flattenedModel == null) {
            // NOTE: not sure if this could happen, probably not. But just in case...
            flattenedModel = new ArrayList<>();
        }

        getView().updateItems(flattenedModel);
    }

    /**
     * Flattens the hash into a list of items to be shown
     *
     * @param model
     * @return
     */
    private List<MenuCardModel> flattenAndFilterModel(MenuCategoryModel model) {
        List<MenuCardModel> flattenedModel = new ArrayList<>();
        if (model == null) {
            return flattenedModel;
        }
        if (model.getCategory().getSubCategories() == null) {
            return null;
        }

        if (mMenuModel.getSelectedLvl2Filter() == null) {
            return null;
        }

        // Now we get all subcategories (and the category itself if needed)
        // that are not filtered within the selected lvl2 category
        List<MenuCategory> categoriesToShow = getCategoriesToShow(mMenuModel.getSelectedLvl2Filter(), model.getSelectedLevel3Filters());
        // Finally we flatten everything while we check if we need to filter via text
        for (MenuCategory category : categoriesToShow) {
            List<MenuCardItemModel> productList = filterProducts(category.getProducts(), model.getTextToSearch());
            if (productList != null && !productList.isEmpty()) {
                flattenedModel.add(new MenuCardHeaderModel(category.getName()));
                flattenedModel.addAll(productList);
            }
        }
        return flattenedModel;
    }

    private List<MenuCardItemModel> filterProducts(List<MenuCardItemModel> products, String textToSearch) {
        List<MenuCardItemModel> filteredProducts = new ArrayList<>();
        for (MenuCardItemModel product : products) {
            if (TextUtils.isEmpty(textToSearch)
                    || StringUtils.containsSimilarString(product.getName(), textToSearch)) {
                filteredProducts.add(product);
            }
        }
        return filteredProducts;
    }

    /**
     * Receives a Lvl2 Category, decides which MenuCategories are to be filtered or not
     * If there are not lvl3 filters then all categories are returned
     * In case there are lvl3 filters then we just return these categories
     *
     * @param category
     * @param selectedLevel3Filters
     * @return
     */
    private List<MenuCategory> getCategoriesToShow(MenuCategory category, HashSet<MenuCategory> selectedLevel3Filters) {
        if (selectedLevel3Filters == null || selectedLevel3Filters.isEmpty()) {
            List<MenuCategory> categories = new ArrayList<>();
            categories.add(category);
            if (category.getSubCategories() != null) {
                categories.addAll(category.getSubCategories());
            }
            return categories;
        } else {
            return new ArrayList<>(selectedLevel3Filters);
        }
    }

    /**
     * Sets the default values for the filter buttons and the map that represents them on the model
     * (All pressed, others not pressed)
     * requires menuModel to be loaded
     */
    private void setupFilters() {
        if (getView() == null) {
            return;
        }

        if (mMenuModel.getCategory().getSubCategories().isEmpty()) {
            applyFiltersToData();
            return;
        }

        getView().removeLevel2Filters(); // Clears previous toggle buttons
        List<MenuCategory> menuSubCategories = mMenuModel.getCategory().getSubCategories();
        for (MenuCategory menuCategory : menuSubCategories) {
            getView().addLevel2Filter(menuCategory, menuSubCategories.indexOf(menuCategory) + 1, menuSubCategories.size());
        }

        // Sets default selected level 2 filter (the first one)
        mMenuModel.setSelectedLvl2Filter(mMenuModel.getCategory().getSubCategories().get(0));
    }

    private void setLevel3FiltersEnabled() {
        getView().level3FiltersListEnabled(TextUtils.isEmpty(mMenuModel.getTextToSearch())
                && mMenuModel.getSelectedLevel3Filters() != null
                && !mMenuModel.getSelectedLevel3Filters().isEmpty());
    }

    @Override
    public void search(String text) {
        mMenuModel.setTextToSearch(TextUtils.isEmpty(text) ? null : text);
        updateLevel3FiltersAndApplyThem();
        if (TextUtils.isEmpty(text)) {
            return;
        }
        mEventLogger.logMenuSearch(text);
    }

    @Override
    public void applyLevel3Filter(HashSet<MenuCategory> filterOptions) {
        mMenuModel.setSelectedLevel3Filters(filterOptions);
        updateLevel3FiltersAndApplyThem();
        if (filterOptions == null || filterOptions.size() == 0) {
            return;
        }
        mEventLogger.logMenuFiltersLevel3(filterOptions);
    }

    @Override
    public void removeLevel3Filter(MenuCategory category) {
        mMenuModel.getSelectedLevel3Filters().remove(category);
        updateLevel3FiltersAndApplyThem();
    }

    @Override
    public int getItemCount() {
        return mMenuModel.getCategory().getAmountProducts();
    }

    @Override
    public void selectSubCategoryWithMostItems() {
        if (getView() == null) {
            return;
        }
        mMenuModel.setSelectedLvl2Filter(getMostPopulatedSubCategory());
        getView().updateSelectedLvl2Filter(getMostPopulatedSubCategory());
    }

    private MenuCategory getMostPopulatedSubCategory() {
        int maxItems = 0;
        MenuCategory mostPopulatedCategory = null;
        for (MenuCategory category : mMenuModel.getCategory().getSubCategories()) {
            if (category.getAmountProducts() > maxItems) {
                maxItems = category.getAmountProducts();
                mostPopulatedCategory = category;
            }
        }
        return mostPopulatedCategory;
    }

    @Override
    public void searchCleared() {
        resetLevel3FiltersAndSearchText();
        updateLevel3FiltersAndApplyThem();
    }

    @Override
    public void searchTextEmpty(boolean isEmpty) {
        getView().level3FiltersButtonEnabled(isEmpty && hasLevel3Categories(mMenuModel));
    }

    private void updateLevel3FiltersOnView() {
        getView().removeLevel3Filters();
        for (MenuCategory category : mMenuModel.getSelectedLevel3Filters()) {
            getView().addLevel3Filter(category);
        }
    }

    @Override
    public void showFilterDialog() {
        MenuCategory selectedCategory = getCurrentSelectedCategory();
        if (selectedCategory == null) {
            return;
        }
        getView().showFilterDialog((ArrayList<MenuCategory>) (selectedCategory.getSubCategories()), mMenuModel.getSelectedLevel3Filters());
    }

    /**
     * Returns the currently selected category (Level2)
     *
     * @return
     */
    private MenuCategory getCurrentSelectedCategory() {
        return mMenuModel.getSelectedLvl2Filter();
    }

    private void updateLevel3FiltersAndApplyThem() {
        setLevel3FiltersEnabled();
        updateLevel3FiltersOnView();
        applyFiltersToData();
    }

    private void resetLevel3FiltersAndSearchText() {
        mMenuModel.setSelectedLevel3Filters(new HashSet<>());
        mMenuModel.setTextToSearch(null);
    }

    @Override
    public void itemCustomizationFinished() {
        getView().goToCart();
    }

    @Override
    public void detachView() {
        mMenuModel.removeOnPropertyChangedCallback(mOnPropertyChangedCallback);
        super.detachView();
    }
}
