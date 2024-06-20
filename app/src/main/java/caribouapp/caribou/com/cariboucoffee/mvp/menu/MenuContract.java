package caribouapp.caribou.com.cariboucoffee.mvp.menu;

import android.content.res.Resources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.common.RewardsData;
import caribouapp.caribou.com.cariboucoffee.common.SearchFieldViewPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.RewardsPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategory;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategoryModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowContract;

/**
 * Created by jmsmuy on 10/3/17.
 */

public interface MenuContract {

    interface View extends OOSFlowContract.View {

        Resources getResources();

        void sendToBeverages(MenuCategoryModel menuCategoryModel);

        void sendToFood(MenuCategoryModel menuCategoryModel);

        void sendToFeatured(MenuCategoryModel menuCategoryModel);

        void sendToRewards(RewardsData rewardsModel);

        void setNavigationPreselected();

        void recreateNavigation();

        void updateRewardBannerVisibility();

        RewardItemModel getCheckInReward();

    }

    interface Presenter extends OOSFlowContract.Presenter {

        void init();

        void setModel(MenuModel menuModel);

        void loadOrder();

        boolean shouldShowRewards();

        boolean shouldShowFeatured();

        boolean shouldShowBeverages();

        boolean shouldShowFood();

        boolean shouldDefaultToRewards();

        boolean isFilteredMenu();

        void removeReward();

        void categorySelected(String categoryName);

        void loadData();

        void sendMenuDataToFragments();

        void filterMenuForPreselectedReward();

        boolean isThisGuestFlow();
    }

    interface Fragments {

        interface View extends MvpView {

            void addLevel2Filter(MenuCategory category, int categoryIndex, int categoriesSize);

            void removeLevel2Filters();

            void updateSelectedLvl2Filter(MenuCategory category);

            void addLevel3Filter(MenuCategory category);

            void removeLevel3Filters();

            void updateItems(List<MenuCardModel> menuCards);

            void goToMenuDetails(MenuCardItemModel menuCardItemModel);

            void showFilterDialog(ArrayList<MenuCategory> filtersLevel3, HashSet<MenuCategory> currentFilters);

            void level3FiltersListEnabled(boolean enabled);

            void level3FiltersButtonEnabled(boolean enabled);

            void goToCart();

            void showItemNotAvailableDialog();
        }

        interface Presenter extends MvpPresenter, SearchFieldViewPresenter, Serializable {

            void applyLevel2Filter(MenuCategory filterId);

            void setData(MenuCategoryModel menuCategoryModel);

            void applyLevel3Filter(HashSet<MenuCategory> filterOptions);

            void removeLevel3Filter(MenuCategory categorymen);

            int getItemCount();

            void selectSubCategoryWithMostItems();

            void itemCustomizationFinished();
        }

        interface Rewards {

            interface View extends OOSFlowContract.View {
                void showCards(List<RewardModel> rewardsOrderedList);

                void showNewPreselectedRewardSet();
            }

            interface Presenter extends OOSFlowContract.Presenter, RewardsPresenter {

                void setModel(RewardsData data);
            }
        }

        interface Featured {

            interface View extends MvpView {

            }

        }

        interface Food {

            interface View extends MvpView {

            }

        }

        interface Beverages {

            interface View extends MvpView {

            }
        }
    }


}
