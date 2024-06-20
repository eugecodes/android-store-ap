package caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.common.RewardsData;
import caribouapp.caribou.com.cariboucoffee.common.RewardsService;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.MenuContract;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategoryModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.MenuData;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.MenuDataService;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowPresenter;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.order.PreSelectedReward;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;


/**
 * Created by jmsmuy on 10/3/17.
 */
public class MenuPresenter extends OOSFlowPresenter<MenuContract.View> implements MenuContract.Presenter {

    public static final int ITEM = 2;
    public static final int HEADER = 1;
    private static final String TAG = MenuPresenter.class.getSimpleName();
    @Inject
    MenuDataService mMenuDataService;

    @Inject
    AppDataStorage mAppDataStorage;

    @Inject
    SettingsServices mSettingsServices;

    @Inject
    RewardsService mRewardsService;

    @Inject
    OrderService mOrderService;

    @Inject
    EventLogger mEventLogger;

    @Inject
    UserServices mUserServices;

    private MenuModel mMenuModel;

    private boolean mOrderLoaded;
    private boolean mRewardsLoaded;
    private boolean mMenuLoaded;
    private boolean mLoadingMenuFirstTime = true;

    private RewardItemModel mCheckInRewardItemModel;

    public MenuPresenter(MenuContract.View view) {
        super(view);
    }

    @Override
    public void init() {
        if (mMenuModel.isOrderAheadMenu()) {
            mAppDataStorage.setOrderLastScreen(AppScreen.PRODUCT_MENU);
        }
    }

    @Override
    public void setModel(MenuModel menuModel) {
        mMenuModel = menuModel;
        mCheckInRewardItemModel = getView().getCheckInReward();
    }

    @Override
    protected boolean shouldCheckForOrderDeletion() {
        return mMenuModel.isOrderAheadMenu();
    }

    @Override
    public void categorySelected(String categoryName) {
        mEventLogger.logMenuFilterLevel1(categoryName);
    }

    @Override
    public void loadData() {
        if (!mMenuModel.isOrderAheadMenu()) {
            loadFullMenu();
        } else {
            mOrderLoaded = false;
            loadOrder();
        }
    }

    private void loadFullMenu() {
        mMenuDataService.getMenuDataFiltered(new BaseViewResultCallback<MenuData>(getView()) {
            @Override
            protected void onSuccessViewUpdates(MenuData data) {
                mMenuModel.setMenuData(data);
                getView().recreateNavigation();
            }
        });
    }

    private void loadLocationMenu() {
        mMenuDataService.getOrderAheadMenuDataFiltered(mMenuModel.getStoreLocation().getId(), null,
                new BaseViewResultCallback<MenuData>(getView()) {
                    @Override
                    protected void onSuccessViewUpdates(MenuData data) {
                        mMenuModel.setMenuData(data);
                        mMenuLoaded = true;
                        checkLoadingFinished();
                    }
                });
    }

    private void loadRewards() {
        if (!shouldShowRewards()) {
            mRewardsLoaded = true;
            checkLoadingFinished();
            return;
        }
        mRewardsLoaded = false;
        mRewardsService.loadRewards(false, true, true,
                new BaseViewResultCallback<RewardsData>(getView()) {
                    @Override
                    protected void onSuccessViewUpdates(RewardsData data) {
                        mMenuModel.setMenuRewardsModel(data);
                        getView().sendToRewards(data);
                        mRewardsLoaded = true;
                        checkLoadingFinished();
                    }
                });
    }

    @Override
    public void filterMenuForPreselectedReward() {
        mMenuDataService
                .getOrderAheadMenuDataFiltered(mMenuModel.getStoreLocation().getId(),
                        String.valueOf(mMenuModel.getOrder().getPreSelectedReward().getRewardId()),
                        new BaseViewResultCallback<MenuData>(getView()) {
                            @Override
                            protected void onSuccessViewUpdates(MenuData data) {
                                mMenuModel.setFilteredMenuData(data);
                                getView().recreateNavigation();
                                getView().updateRewardBannerVisibility();
                            }
                        });
    }

    @Override
    public boolean isThisGuestFlow() {
        return (!mUserServices.isUserLoggedIn() && mUserServices.isGuestUserFlowStarted());
    }

    private void resetMenu() {
        mMenuModel.setFilteredMenuData(null);
        getView().recreateNavigation();
        getView().setNavigationPreselected();
        getView().updateRewardBannerVisibility();
    }

    @Override
    protected void setOrder(Order data) {
        mMenuModel.setStoreLocation(data.getStoreLocation());
        mMenuModel.setAmountItemsOnCart(data.getTotalItemsInCart());
        mMenuModel.setOrder(data);

        if (mOrderLoaded) {
            return;
        }

        // setOrder can be called several times since loadOrder is called more than once.
        // We only want to load rewards and locationMenu only the first time around
        mOrderLoaded = true;
        loadRewards();
        loadLocationMenu();
    }

    @Override
    public boolean shouldDefaultToRewards() {
        return mSettingsServices.isOrderAhead() // rewards don't exist when OOS is off
                && mSettingsServices.isOrderAheadRewardsDefaultTab()
                && mOrderService.isRewardsSupported()
                && mMenuModel.isOrderAheadMenu()
                && mMenuModel.getOrder().getPreSelectedReward() == null
                && mMenuModel.getOrder().getTotalItemsInCart() == 0
                && StreamSupport.stream(mMenuModel.getMenuRewardsModel().getRedeemedRewards())
                .filter(rewardItemModel -> !rewardItemModel.isAutoApply()).collect(Collectors.toList()).size() > 0;
    }

    private void checkLoadingFinished() {
        if (!mRewardsLoaded || !mMenuLoaded) {
            return;
        }

        if (mCheckInRewardItemModel != null && mMenuModel.getOrder().getPreSelectedReward() == null) {
            getOrderService().setPreSelectedReward(new PreSelectedReward(mCheckInRewardItemModel));
            mCheckInRewardItemModel = null;
            filterMenuForPreselectedReward();
        } else if (mLoadingMenuFirstTime) {
            resetMenu();
            mLoadingMenuFirstTime = false;
        } else {
            resetMenu();
        }
    }

    @Override
    public boolean shouldShowRewards() {
        return mSettingsServices.isOrderAhead() && mMenuModel.isOrderAheadMenu()
                && !isFilteredMenu() && mOrderService.isRewardsSupported();
    }


    @Override
    public boolean shouldShowFeatured() {
        return !isFilteredMenu() && getCurrentMenu().getCategoryByName(MenuData.CATEGORY_FEATURED_MENU) != null;
    }

    @Override
    public boolean shouldShowBeverages() {
        return getCurrentMenu().getCategoryByName(MenuData.CATEGORY_SECOND_MENU) != null;
    }

    @Override
    public boolean shouldShowFood() {
        return getCurrentMenu().getCategoryByName(MenuData.CATEGORY_THIRD_MENU) != null;
    }

    @Override
    public boolean isFilteredMenu() {
        return mMenuModel.isFilteredMenu();
    }

    @Override
    public void removeReward() {
        getOrderService().clearPreSelectedReward();
        resetMenu();
    }

    private MenuData getCurrentMenu() {
        return mMenuModel.getFilteredMenuData() == null ? mMenuModel.getMenuData() : mMenuModel.getFilteredMenuData();
    }

    @Override
    public void sendMenuDataToFragments() {
        MenuData currentMenuData = getCurrentMenu();

        if (currentMenuData.getCategories() == null || currentMenuData.getCategories().size() == 0) {
            return;
        }

        if (shouldShowFeatured()) {
            getView().sendToFeatured(
                    new MenuCategoryModel(currentMenuData.getCategoryByName(MenuData.CATEGORY_FEATURED_MENU),
                            mMenuModel.isOrderAheadMenu()));
        }

        if (shouldShowBeverages()) {
            getView().sendToBeverages(
                    new MenuCategoryModel(currentMenuData.getCategoryByName(MenuData.CATEGORY_SECOND_MENU),
                            mMenuModel.isOrderAheadMenu()));
        }

        if (shouldShowFood()) {
            getView().sendToFood(
                    new MenuCategoryModel(currentMenuData.getCategoryByName(MenuData.CATEGORY_THIRD_MENU),
                            mMenuModel.isOrderAheadMenu()));
        }
    }
}
