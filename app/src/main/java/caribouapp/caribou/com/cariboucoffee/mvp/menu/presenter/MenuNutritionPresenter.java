package caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter;


import java.util.Set;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.MenuNutritionContract;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.NutritionalItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;

/**
 * Created by jmsmuy on 11/17/17.
 */

public class MenuNutritionPresenter extends BasePresenter<MenuNutritionContract.View> implements MenuNutritionContract.Presenter {

    private static final String TAG = MenuNutritionPresenter.class.getSimpleName();

    @Inject
    UserServices mUserServices;

    private MenuCardItemModel mModel;

    public MenuNutritionPresenter(MenuNutritionContract.View view) {
        super(view);
    }

    @Override
    public void setModel(MenuCardItemModel model) {
        this.mModel = model;
    }

    @Override
    public void loadData(SizeEnum productSelectedSize) {
        getView().setAllergens(mModel.getAllergens());
        if (mModel.getNutritionalItemModels().isEmpty()) {
            getView().onNoNutritionAvailable();
        } else if (mModel.getNutritionalItemModels().size() == 1) {
            getView().disableSizes();
            getView().setNutritionalValuesList(
                    mModel.getNutritionalItemModels().values().iterator().next().getNutritionalValues());
        } else {
            Set<SizeEnum> possibleSize = mModel.getNutritionalItemModels().keySet();
            getView().setPossibleSizes(possibleSize);
            if (productSelectedSize != null) {
                setSize(productSelectedSize);
            } else if (possibleSize.contains(AppConstants.DEFAULT_DRINK_SIZE)) {
                setSize(AppConstants.DEFAULT_DRINK_SIZE);
            } else {
                setSize(possibleSize.iterator().next());
            }
        }
    }

    @Override
    public void setSize(SizeEnum selectedSize) {
        NutritionalItemModel nutritionalItemModel = mModel.getNutritionalItemModels().get(selectedSize);

        if (nutritionalItemModel == null) {
            Log.e(TAG, new LogErrorException("Missing nutritional data for Product: " + mModel.getName() + " and size: " + selectedSize));
            getView().showWarning(R.string.nutritional_information_unavailable_for_size); // Shouldn't happen... but we never know
        }

        getView().setNutritionalValuesList(nutritionalItemModel.getNutritionalValues());
        getView().refreshTable();
        getView().setSize(selectedSize);
    }
}
