package caribouapp.caribou.com.cariboucoffee.mvp.menu.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by jmsmuy on 10/4/17.
 */

public class MenuCategoryModel extends BaseObservable implements Serializable {

    private boolean mOrderAvailable;
    private MenuCategory mCategory;
    private MenuCategory mSelectedLvl2Filter;
    private HashSet<MenuCategory> mSelectedLvl3Filters = new HashSet<>();
    private String mTextToSearch;

    public MenuCategoryModel() {
    }

    public MenuCategoryModel(MenuCategory category) {
        mCategory = category;
    }

    public MenuCategoryModel(MenuCategory menuCategory, boolean orderAvailable) {
        mCategory = menuCategory;
        mOrderAvailable = orderAvailable;
    }

    @Bindable
    public MenuCategory getCategory() {
        return mCategory;
    }

    public void setCategory(MenuCategory category) {
        mCategory = category;
        notifyPropertyChanged(BR.category);
    }

    public HashSet<MenuCategory> getSelectedLevel3Filters() {
        return mSelectedLvl3Filters;
    }

    public void setSelectedLevel3Filters(HashSet<MenuCategory> selectedLvl3Filters) {
        mSelectedLvl3Filters = selectedLvl3Filters;
    }

    public String getTextToSearch() {
        return mTextToSearch;
    }

    public void setTextToSearch(String text) {
        mTextToSearch = text;
    }

    public boolean isOrderAvailable() {
        return mOrderAvailable;
    }

    public void setOrderAvailable(boolean orderAvailable) {
        mOrderAvailable = orderAvailable;
    }

    @Bindable
    public MenuCategory getSelectedLvl2Filter() {
        return mSelectedLvl2Filter;
    }

    public void setSelectedLvl2Filter(MenuCategory selectedLvl2Filter) {
        mSelectedLvl2Filter = selectedLvl2Filter;
        notifyPropertyChanged(BR.selectedLvl2Filter);
    }


    public MenuCategory findSubCategoryByName(String subCategoryName) {
        for (MenuCategory menuCategory : getCategory().getSubCategories()) {
            if (menuCategory.getName().equals(subCategoryName)) {
                return menuCategory;
            }
        }
        return null;
    }
}
