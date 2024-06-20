package caribouapp.caribou.com.cariboucoffee.mvp.menu.service;

import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;

/**
 * Created by asegurola on 5/3/18.
 */

public interface MenuDataService {

    void clearCache();

    void getMenuDataFiltered(ResultCallback<MenuData> resultCallback);

    MenuCardItemModel getProductData(String omsProdId);

    void getOrderAheadMenuDataFiltered(String locationId, String rewardId,
                                       ResultCallback<MenuData> resultCallback);
}
