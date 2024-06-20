package caribouapp.caribou.com.cariboucoffee.mvp.menu;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.util.nutritionnallergens.NutritionAllergensAware;

/**
 * Created by jmsmuy on 10/17/17.
 */

public interface MenuDetailsContract {

    interface View extends MvpView, NutritionAllergensAware {
    }

    interface Presenter extends MvpPresenter {

        MenuCardItemModel getModel();
        void loadData();
    }
}
