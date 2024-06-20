package caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.common.StartOrderContract;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrdersModel;

/**
 * Created by jmsmuy on 4/9/18.
 */

public interface RecentOrderContract {

    interface View extends StartOrderContract.ErrorDialogsView, StartOrderContract.View {

        void setOrderList(RecentOrdersModel model);

        void goToReOrder(StoreLocation storeLocation);

        void goToStartNewOrder();

        void showCantReorderErrorMessage();
    }

    interface Presenter extends MvpPresenter {

        void startReorder(RecentOrderModel recentOrderModel);

        void reorder(StoreLocation storeLocation);

        void loadData(List<RecentOrderModel> recentOmsOrderList);

        void startNewOrder();
    }

}
