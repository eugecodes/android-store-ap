package caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter;

import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.MenuDetailsContract;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.util.nutritionnallergens.NutritionAllergensAware;

/**
 * Created by jmsmuy on 10/17/17.
 */

public class MenuDetailsPresenter extends BasePresenter<MenuDetailsContract.View> implements MenuDetailsContract.Presenter {


    private MenuCardItemModel mModel;

    public MenuDetailsPresenter(MenuDetailsContract.View view, MenuCardItemModel model) {
        super(view);
        mModel = model;
    }

    public MenuCardItemModel getModel() {
        return mModel;
    }

    @Override
    public void loadData() {
        NutritionAllergensAware.notifyAvailability(mModel, getView());
    }
}
