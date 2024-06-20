package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.presenter.positouch;

import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.ItemContract;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.positouch.PositouchOrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.presenter.ItemCustomizationPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;

/**
 * Created by asegurola on 3/19/18.
 */

public class PositouchItemCustomizationPresenter extends ItemCustomizationPresenter<PositouchOrderItem> {

    private static final String TAG = ItemCustomizationPresenter.class.getSimpleName();

    public PositouchItemCustomizationPresenter(ItemContract.View view) {
        super(view);
    }

    @Override
    public OrderItem newModel(MenuCardItemModel menuCardItemModel) {
        PositouchOrderItem positouchOrderItem = new PositouchOrderItem(menuCardItemModel);
        setModel(positouchOrderItem, true);
        return positouchOrderItem;
    }
}
