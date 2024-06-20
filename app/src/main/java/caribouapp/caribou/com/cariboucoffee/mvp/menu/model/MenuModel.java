package caribouapp.caribou.com.cariboucoffee.mvp.menu.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;

import caribouapp.caribou.com.cariboucoffee.common.RewardsData;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.MenuData;
import caribouapp.caribou.com.cariboucoffee.order.Order;

/**
 * Created by jmsmuy on 10/4/17.
 */

public class MenuModel extends BaseObservable implements Serializable {

    private boolean mOrderAheadMenu = false;
    private StoreLocation mStoreLocation;
    private Integer mAmountItemsOnCart;
    private MenuData mMenuData;
    private MenuData mFilteredMenuData;
    private Order mOrder;
    private RewardsData mMenuRewardsModel;

    public boolean isOrderAheadMenu() {
        return mOrderAheadMenu;
    }

    public void setOrderAheadMenu(boolean orderAheadMenu) {
        mOrderAheadMenu = orderAheadMenu;
    }

    @Bindable
    public StoreLocation getStoreLocation() {
        return mStoreLocation;
    }

    public void setStoreLocation(StoreLocation storeLocation) {
        mStoreLocation = storeLocation;
        notifyPropertyChanged(BR.storeLocation);
    }

    @Bindable
    public Integer getAmountItemsOnCart() {
        return mAmountItemsOnCart;
    }

    public void setAmountItemsOnCart(Integer amountItemsOnCart) {
        mAmountItemsOnCart = amountItemsOnCart;
        notifyPropertyChanged(BR.amountItemsOnCart);
    }

    @Bindable
    public MenuData getMenuData() {
        return mMenuData;
    }

    public void setMenuData(MenuData menuData) {
        mMenuData = menuData;
        notifyPropertyChanged(BR.menuData);
    }

    @Bindable
    public Order getOrder() {
        return mOrder;
    }


    public void setOrder(Order order) {
        mOrder = order;
        notifyPropertyChanged(BR.order);
    }

    public RewardsData getMenuRewardsModel() {
        return mMenuRewardsModel;
    }

    public void setMenuRewardsModel(RewardsData menuRewardsModel) {
        mMenuRewardsModel = menuRewardsModel;
    }

    @Bindable
    public MenuData getFilteredMenuData() {
        return mFilteredMenuData;
    }

    public void setFilteredMenuData(MenuData filteredMenuData) {
        mFilteredMenuData = filteredMenuData;
        notifyPropertyChanged(BR.filteredMenuData);
        notifyPropertyChanged(BR.filteredMenu);
    }

    @Bindable
    public boolean isFilteredMenu() {
        return mFilteredMenuData != null;
    }
}
