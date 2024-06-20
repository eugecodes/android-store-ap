package caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.positouch;

import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsOrder;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderModel;

public class PositouchRecentOrderModel extends RecentOrderModel {

    private OmsOrder mOmsOrder;

    public OmsOrder getOmsOrder() {
        return mOmsOrder;
    }

    public void setOmsOrder(OmsOrder omsOrder) {
        mOmsOrder = omsOrder;
    }
}
