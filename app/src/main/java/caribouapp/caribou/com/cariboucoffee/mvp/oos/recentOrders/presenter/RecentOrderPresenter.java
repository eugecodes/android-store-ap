package caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.presenter;

import java.util.List;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.api.CmsApi;
import caribouapp.caribou.com.cariboucoffee.common.StartOrderPresenter;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.RecentOrderContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrdersModel;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;

import static caribouapp.caribou.com.cariboucoffee.AppConstants.AMOUNT_DAYS_RECENT_ORDERS;

/**
 * Created by jmsmuy on 4/9/18.
 */

public class RecentOrderPresenter extends StartOrderPresenter<RecentOrderContract.View> implements RecentOrderContract.Presenter {

    private static final String TAG = RecentOrderPresenter.class.getSimpleName();

    @Inject
    OrderService mOrderService;

    @Inject
    CmsApi mCmsApi;

    private RecentOrdersModel mModel;

    private RecentOrderModel mReorderSelected;

    public RecentOrderPresenter(RecentOrderContract.View view, RecentOrdersModel model) {
        super(view);
        mModel = model;

    }

    @Override
    public void startReorder(RecentOrderModel recentOrderModel) {
        mReorderSelected = recentOrderModel;
        startNewOrder(recentOrderModel.getRecentOrderStore().getId());
    }

    @Override
    public void reorder(StoreLocation storeLocation) {
        getOrderService().reorder(mReorderSelected, storeLocation, new BaseViewResultCallback<Order>(getView()) {
            @Override
            protected void onSuccessViewUpdates(Order data) {
                getEventLogger().logOrderStarted(true);
                getView().goToReOrder(storeLocation);
            }

            @Override
            protected void onFailView(int errorCode, String errorMessage) {
                onError(null);
            }

            @Override
            protected void onErrorView(Throwable throwable) {
                getView().showCantReorderErrorMessage();
            }
        });
    }

    @Override
    public void loadData(List<RecentOrderModel> recentOrderList) {
        if (!getSettingsServices().isReorder()) {
            mModel.setShowReorderError(true);
            getView().setOrderList(mModel);
            return;
        }
        if (recentOrderList == null) {
            loadRecentOrders();
        } else {
            mModel.setRecentOrderList(recentOrderList);
            getView().setOrderList(mModel);
        }
    }

    private void loadRecentOrders() {
        mOrderService.getRecentOrder(AMOUNT_DAYS_RECENT_ORDERS, new BaseViewResultCallback<List<RecentOrderModel>>(getView()) {
            @Override
            protected void onSuccessViewUpdates(List<RecentOrderModel> recentOrderModels) {
                mModel.setRecentOrderList(recentOrderModels);
                getView().setOrderList(mModel);
            }
        });
    }


    @Override
    public void startNewOrder() {
        getView().goToStartNewOrder();
    }
}
