package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.presenter.ncr;

import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.ItemContract;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ncr.NcrOrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.presenter.ItemCustomizationPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;

/**
 * Created by asegurola on 3/19/18.
 */

public class NcrItemCustomizationPresenter extends ItemCustomizationPresenter<NcrOrderItem> {

    private static final String TAG = NcrItemCustomizationPresenter.class.getSimpleName();

    public NcrItemCustomizationPresenter(ItemContract.View view) {
        super(view);
    }

    @Override
    public OrderItem newModel(MenuCardItemModel menuCardItemModel) {
        NcrOrderItem ncrOrderItem = new NcrOrderItem(menuCardItemModel);
        setModel(ncrOrderItem, true);
        return ncrOrderItem;
    }

}
