package caribouapp.caribou.com.cariboucoffee.mvp.menu.model;

import caribouapp.caribou.com.cariboucoffee.common.BaseRewardsModel;
import caribouapp.caribou.com.cariboucoffee.order.Order;

public class MenuRewardsModel extends BaseRewardsModel {
    private Order mOrder;

    public Order getOrder() {
        return mOrder;
    }

    public void setOrder(Order order) {
        mOrder = order;
    }
}
