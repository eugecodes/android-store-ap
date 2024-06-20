package caribouapp.caribou.com.cariboucoffee.mvp.menu.service;


import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;

public interface NcrMenuDataServices extends MenuDataService  {

    MenuCardItemModel getProductDataBySaleItemId(String saleItemId);
}
