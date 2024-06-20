package caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jmsmuy on 11/04/18.
 */

public class RecentOrdersModel extends BaseObservable implements Serializable {
    private List<RecentOrderModel> mRecentOrderList = new ArrayList<>();
    private boolean mShowReorderError = false;

    @Bindable
    public List<RecentOrderModel> getRecentOrderList() {
        return mRecentOrderList;
    }

    public void setRecentOrderList(List<RecentOrderModel> recentOrderList) {
        mRecentOrderList = recentOrderList;
        notifyPropertyChanged(BR.recentOrderList);
    }

    @Bindable
    public boolean isShowReorderError() {
        return mShowReorderError;
    }

    public void setShowReorderError(boolean showReorderError) {
        mShowReorderError = showReorderError;
        notifyPropertyChanged(BR.showReorderError);
    }

    public void addRecentOrder(RecentOrderModel recentOrderModel) {
        mRecentOrderList.add(recentOrderModel);
    }
}
