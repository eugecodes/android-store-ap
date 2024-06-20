package caribouapp.caribou.com.cariboucoffee.mvp.menu;

import java.util.List;
import java.util.Set;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.NutritionalRowData;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;

/**
 * Created by jmsmuy on 11/17/17.
 */

public interface MenuNutritionContract {

    interface View extends MvpView {

        void setAllergens(List<String> allergens);

        void setSize(SizeEnum size);

        void setPossibleSizes(Set<SizeEnum> possibleSizes);

        void refreshTable();

        void disableSizes();

        void setNutritionalValuesList(List<NutritionalRowData> nutritionalValues);

        void onNoNutritionAvailable();
    }

    interface Presenter extends MvpPresenter {

        void setModel(MenuCardItemModel productDetailsFromIntent);

        void loadData(SizeEnum productSelectedSize);

        void setSize(SizeEnum selectedSize);
    }

}
