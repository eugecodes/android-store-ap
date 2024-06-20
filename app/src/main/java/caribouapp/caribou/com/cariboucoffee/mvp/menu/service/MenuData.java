package caribouapp.caribou.com.cariboucoffee.mvp.menu.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.BuildConfig;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategory;

/**
 * Created by asegurola on 5/3/18.
 */

public class MenuData implements Serializable {

    public static final String CATEGORY_FEATURED_MENU = "Featured";
    public static final String CATEGORY_SECOND_MENU = BuildConfig.SECOND_MENU_COLUMN_KEY;
    public static final String CATEGORY_THIRD_MENU = BuildConfig.THIRD_MENU_COLUMN_KEY;

    private List<MenuCategory> mCategories = new ArrayList<>();

    private Map<String, MenuCardItemModel> mMenuProductDataByOmsProdId = new HashMap<>();

    public List<MenuCategory> getCategories() {
        return mCategories;
    }

    public MenuCategory getCategoryByName(String categoryName) {
        if (mCategories == null) {
            return null;
        }
        for (MenuCategory menuCategory : mCategories) {
            if (menuCategory.getName().equalsIgnoreCase(categoryName)) {
                return menuCategory;
            }
        }
        return null;
    }

    public void addCategory(MenuCategory category) {
        mCategories.add(category);
    }

    public Map<String, MenuCardItemModel> getMenuProductDataByOmsProdId() {
        return mMenuProductDataByOmsProdId;
    }

    public void setMenuProductDataByOmsProdId(Map<String, MenuCardItemModel> menuProductData) {
        mMenuProductDataByOmsProdId = menuProductData;
    }

    public void setCategories(List<MenuCategory> categories) {
        mCategories = categories;
    }
}
