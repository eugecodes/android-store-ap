package caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.ncr;

import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderModel;

public class NcrRecentOrderModel extends RecentOrderModel {

    private String mCheckNumber;

    public String getCheckNumber() {
        return mCheckNumber;
    }

    public void setCheckNumber(String checkNumber) {
        mCheckNumber = checkNumber;
    }
}
