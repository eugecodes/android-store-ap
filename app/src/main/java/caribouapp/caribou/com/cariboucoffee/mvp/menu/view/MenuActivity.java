package caribouapp.caribou.com.cariboucoffee.mvp.menu.view;

import static caribouapp.caribou.com.cariboucoffee.AppConstants.EXTRA_CHECKIN_REWARD_SELECTED;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.RewardsData;
import caribouapp.caribou.com.cariboucoffee.common.TabFactory;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityMenuBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.BaseFragment;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.MenuContract;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategory;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategoryModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter.MenuPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.RemoveRewardListener;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.cart.view.CartActivity;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import icepick.Icepick;

/**
 * Created by jmsmuy on 10/3/17.
 */

public class MenuActivity extends OOSFlowActivity<ActivityMenuBinding> implements
        MenuContract.View, RemoveRewardListener, MenuRewardsFragment.OnNoRewardTodayClickListener {

    private static final String TAG = MenuActivity.class.getSimpleName();

    private static final int FEATURED_TAB = 1;
    private final List<TabFactory.TabContent<View, BaseFragment>> mTabsList = new ArrayList<>();
    MenuModel mModel;
    private MenuContract.Presenter mPresenter;
    private MenuRewardsFragment mRewardFragment;
    private MenuProductFragment mFeaturedFragment;
    private MenuProductFragment mBeverageFragment;
    private MenuProductFragment mFoodFragment;
    private TabFactory.TabContent<View, BaseFragment> mRewardTab;
    private TabFactory.TabContent<View, BaseFragment> mFeaturedTab;
    private TabFactory.TabContent<View, BaseFragment> mBeverageTab;
    private TabFactory.TabContent<View, BaseFragment> mFoodTab;

    private TabLayout.OnTabSelectedListener mTabSelectedListener;
    private TabFactory.TabContent<View, BaseFragment> mLastTabSelected;
    private MenuCategory mLastCategorySelected;
    private int mMostPopulatedFragmentIndex;
    private BroadcastReceiver mBroadcastReceiver;
    private boolean isAppGoesInBackground;

    public static Intent createIntent(Context context, boolean orderFlow, MenuOrigin origin, RewardItemModel rewardItemModel) {
        Intent intent = new Intent(context, MenuActivity.class);
        intent.putExtra(AppConstants.EXTRA_MENU_ORDER_FLOW, orderFlow);
        intent.putExtra(AppConstants.EXTRA_ORIGIN, origin);
        intent.putExtra(EXTRA_CHECKIN_REWARD_SELECTED, rewardItemModel);
        return intent;
    }

    public static Intent createIntent(Context context, boolean orderFlow, MenuOrigin origin) {
        Intent intent = new Intent(context, MenuActivity.class);
        intent.putExtra(AppConstants.EXTRA_MENU_ORDER_FLOW, orderFlow);
        intent.putExtra(AppConstants.EXTRA_ORIGIN, origin);
        return intent;
    }

    private boolean isMenuOrderAheadFlow() {
        return getIntent().getBooleanExtra(AppConstants.EXTRA_MENU_ORDER_FLOW, false);
    }

    public MenuOrigin getOriginFromIntent() {
        return (MenuOrigin) getIntent().getSerializableExtra(AppConstants.EXTRA_ORIGIN);
    }

    private RewardItemModel getSelectedRewardFromIntent() {
        return (RewardItemModel) getIntent().getSerializableExtra(EXTRA_CHECKIN_REWARD_SELECTED);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_menu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Icepick.restoreInstanceState(this, savedInstanceState);

        SourceApplication.get(this).getComponent().inject(this);

        // Sets up toolbar
        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (mModel == null) {
            mModel = new MenuModel();
        }

        mModel.setOrderAheadMenu(isMenuOrderAheadFlow());

        MenuPresenter presenter = new MenuPresenter(this);
        getBinding().setModel(mModel);
        SourceApplication.get(this).getComponent().inject(presenter);

        mPresenter = presenter;
        mPresenter.setModel(mModel);

        setOOSFlowPresenter(presenter);
        isAppGoesInBackground = false;
        getBinding().cvCart.setOnClickListener(v -> {
            Intent intent = CartActivity.createIntent(MenuActivity.this);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });


        getBinding().rewardAddedBanner.setSecondaryText(R.string.choose_one_to_add);
        getBinding().rewardAddedBanner.setRemoveRewardListener(this);
        createTabAndFragments();

        mPresenter.init();

        if (!mModel.isOrderAheadMenu()) {
            // For the normal full Menu we load the data once when creating the view
            mPresenter.loadData();
        }

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (AppConstants.BROADCAST_INTENT_ACTION_NEW_PRESELECTED_REWARD_SET.equals(intent.getAction())) {
                    mPresenter.filterMenuForPreselectedReward();
                }
            }
        };
        IntentFilter intentFilter =
                new IntentFilter(AppConstants.BROADCAST_INTENT_ACTION_NEW_PRESELECTED_REWARD_SET);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mModel.isOrderAheadMenu() && (!isAppGoesInBackground)) {
            // For order ahead menu we need to refresh some data when coming back to the menu screen
            isAppGoesInBackground = false;
            mPresenter.loadData();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected AppScreen getScreenName() {
        return mModel.isOrderAheadMenu() ? AppScreen.MENU_OA : AppScreen.MENU;
    }

    private void selectTab(int tabNumber) {
        TabLayout.Tab tab = getBinding().tlTabs.getTabAt(tabNumber);
        if (tab == null) {
            Log.e(TAG, new LogErrorException("Tab " + tabNumber + " is null, existing tabs are: " + getBinding().tlTabs.getTabCount()));
            return;
        }
        tab.select();
        // @@
    }

    private void modCategoryTab(int drawable, int text, int tabNumber, boolean selectedTab, String contentDescription) {
        TabFactory.modTab(drawable, text, tabNumber, selectedTab, getLayoutInflater(), this, getBinding().tlTabs, false, contentDescription);
    }

    @Override
    public void sendToBeverages(MenuCategoryModel menuCategoryModel) {
        mBeverageFragment.setData(menuCategoryModel, mPresenter.isFilteredMenu());
    }

    @Override
    public void sendToFood(MenuCategoryModel menuCategoryModel) {
        mFoodFragment.setData(menuCategoryModel, mPresenter.isFilteredMenu());
    }

    @Override
    public void sendToFeatured(MenuCategoryModel menuCategoryModel) {
        mFeaturedFragment.setData(menuCategoryModel, mPresenter.isFilteredMenu());
    }

    @Override
    public void sendToRewards(RewardsData rewardsModel) {
        mRewardFragment.setData(rewardsModel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void noRewardTodayClick() {
        selectTab(FEATURED_TAB);
    }

    private void setupViewPager() {
        getBinding().contentIncluded.vpMenu.setAdapter(TabFactory.createWithContent(getSupportFragmentManager(), mTabsList));

        getBinding().contentIncluded.vpMenu.setOffscreenPageLimit(mTabsList.size());

        getBinding().tlTabs.setupWithViewPager(getBinding().contentIncluded.vpMenu);
    }

    private int getStartingTabIndex() {
        return mPresenter.isFilteredMenu() ? mMostPopulatedFragmentIndex
                : mPresenter.shouldDefaultToRewards() ? mTabsList.indexOf(mRewardTab) : mTabsList.indexOf(mFeaturedTab);
    }

    private int getMorePopulatedTab() {
        int maxItems = 0;
        int mostPopulatedFragmentIndex = 0;
        for (int tabIndex = 0; tabIndex < mTabsList.size(); tabIndex++) {
            MenuProductFragment currentTab = (MenuProductFragment) mTabsList.get(tabIndex).getContent();
            if (currentTab != null && !currentTab.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.DESTROYED)
                    && currentTab.getProductCount() > maxItems) {
                maxItems = currentTab.getProductCount();
                mostPopulatedFragmentIndex = tabIndex;
            }
        }
        return mostPopulatedFragmentIndex;
    }

    @Override
    public void removeReward() {
        saveCurrentSelectedTabAndCategory();
        mPresenter.removeReward();
    }

    @Override
    public void recreateNavigation() {
        createTabAndFragments();
        buildTabList();
        mPresenter.sendMenuDataToFragments();
        setupViewPager();
        if (mPresenter.isFilteredMenu()) {
            mMostPopulatedFragmentIndex = getMorePopulatedTab();
        }
        modTabs();
    }

    @Override
    public RewardItemModel getCheckInReward() {
        return getSelectedRewardFromIntent();
    }

    public void saveCurrentSelectedTabAndCategory() {
        if (!mTabsList.isEmpty() && mPresenter.isFilteredMenu()) {
            mLastTabSelected = mTabsList.get(getBinding().tlTabs.getSelectedTabPosition());
            mLastCategorySelected = ((MenuProductFragment) mLastTabSelected.getContent()).mModel.getSelectedLvl2Filter();
        }
    }

    @Override
    public void updateRewardBannerVisibility() {
        getBinding().rewardAddedBanner.setVisibility(mPresenter.isFilteredMenu() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setNavigationPreselected() {
        if (mLastTabSelected != null && mLastCategorySelected != null) {
            selectTab(getIndexOfTabToSelect(mLastTabSelected));
            MenuCategoryModel menuCategoryModel = ((MenuProductFragment) mTabsList.get(getIndexOfTabToSelect(mLastTabSelected)).getContent()).mModel;
            menuCategoryModel.setSelectedLvl2Filter(menuCategoryModel.findSubCategoryByName(mLastCategorySelected.getName()));
            mLastCategorySelected = null;
            mLastTabSelected = null;
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        isAppGoesInBackground = true;
    }

    private void createTabAndFragments() {
        if (mRewardFragment == null) {
            mRewardFragment = new MenuRewardsFragment();
        }
        if (mFeaturedFragment == null) {
            mFeaturedFragment = new MenuFeaturedFragment();
        }
        if (mBeverageFragment == null) {
            mBeverageFragment = new SecondColumnMenuFragment();
        }
        if (mFoodFragment == null) {
            mFoodFragment = new ThirdColumnMenuFragment();
        }

        mRewardTab = new TabFactory.TabContent<>(mRewardFragment);
        mFeaturedTab = new TabFactory.TabContent<>(mFeaturedFragment);
        mBeverageTab = new TabFactory.TabContent<>(mBeverageFragment);
        mFoodTab = new TabFactory.TabContent<>(mFoodFragment);
    }

    private void buildTabList() {
        mTabsList.clear();
        if (!mPresenter.isThisGuestFlow()) {
            addRewardTab();
        }
        addFeatureTab();
        addFoodTab();
        addBeverageTab();
    }

    private void addRewardTab() {
        if (mPresenter.shouldShowRewards()) {
            mTabsList.add(mRewardTab);
        }
    }

    private void addFeatureTab() {
        if (mPresenter.shouldShowFeatured()) {
            mTabsList.add(mFeaturedTab);
        }
    }

    private void addBeverageTab() {
        if (mPresenter.shouldShowBeverages()) {
            mTabsList.add(mBeverageTab);
        }
    }

    private void addFoodTab() {
        if (mPresenter.shouldShowFood()) {
            mTabsList.add(mFoodTab);
        }
    }

    private void modTabs() {
        if (mPresenter.shouldShowRewards() && !mPresenter.isThisGuestFlow()) {
            int tabIndex = mTabsList.indexOf(mRewardTab);
            modCategoryTab(R.drawable.ic_rewards, R.string.home_column_first, tabIndex, tabIndex == getStartingTabIndex(),
                    getString(R.string.menu_tabs, getString(R.string.menu_rewards_tab)));
        }

        if (mPresenter.shouldShowFeatured()) {
            int tabIndex = mTabsList.indexOf(mFeaturedTab);
            modCategoryTab(R.drawable.ic_menu_first_column, R.string.home_column_second, tabIndex,
                    tabIndex == getStartingTabIndex(), getString(R.string.menu_tabs, getString(R.string.menu_feature_tab)));
        }

        if (mPresenter.shouldShowBeverages()) {
            int tabIndex = mTabsList.indexOf(mBeverageTab);
            modCategoryTab(R.drawable.ic_menu_second_column, R.string.home_column_third, tabIndex,
                    tabIndex == getStartingTabIndex(), getString(R.string.menu_tabs, getString(R.string.menu_beverage_tab)));
        }

        if (mPresenter.shouldShowFood()) {
            int tabIndex = mTabsList.indexOf(mFoodTab);
            modCategoryTab(R.drawable.ic_menu_third_column, R.string.home_column_fourth, tabIndex,
                    tabIndex == getStartingTabIndex(), getString(R.string.menu_tabs, getString(R.string.menu_food_tab)));
        }
        selectTab(getStartingTabIndex());
        mTabSelectedListener = TabFactory.createFadeTabListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getTag() != null) {
                    mPresenter.categorySelected(tab.getTag().toString());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        }, false);
        getBinding().tlTabs.addOnTabSelectedListener(mTabSelectedListener);
    }

    private int getIndexOfTabToSelect(TabFactory.TabContent<View, BaseFragment> lastTabSelected) {
        if (lastTabSelected.getContent() instanceof SecondColumnMenuFragment) {
            return mTabsList.indexOf(mBeverageTab);
        }
        if (lastTabSelected.getContent() instanceof ThirdColumnMenuFragment) {
            return mTabsList.indexOf(mFoodTab);
        }
        return 0;
    }

    public enum MenuOrigin {
        CART, OTHER
    }
}
