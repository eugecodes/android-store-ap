package caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model;

import androidx.databinding.BaseObservable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jmsmuy on 4/9/18.
 */

public abstract class RecentOrderModel extends BaseObservable implements Serializable {

    private List<RecentOrderItem> mOrderItems = new ArrayList<>();
    private RecentOrderStore mRecentOrderStore;


    public List<RecentOrderItem> getOrderItems() {
        return mOrderItems;
    }

    public void setOrderItems(List<RecentOrderItem> orderItems) {
        mOrderItems = orderItems;
    }

    public RecentOrderStore getRecentOrderStore() {
        return mRecentOrderStore;
    }

    public void setRecentOrderStore(RecentOrderStore recentOrderStore) {
        mRecentOrderStore = recentOrderStore;
    }
}
