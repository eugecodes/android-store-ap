package caribouapp.caribou.com.cariboucoffee.mvp.menu.view;

import caribouapp.caribou.com.cariboucoffee.mvp.menu.MenuContract;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategoryModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter.MenuFragmentPresenter;

/**
 * Created by jmsmuy on 2/9/18.
 */
// TODO This framgent name has to be reviewed and generalized
public class SecondColumnMenuFragment extends MenuProductFragment implements MenuContract.Fragments.Beverages.View {

    @Override
    protected MenuFragmentPresenter getPresenter(MenuCategoryModel model) {
        return new MenuFragmentPresenter(this, model);
    }
}
