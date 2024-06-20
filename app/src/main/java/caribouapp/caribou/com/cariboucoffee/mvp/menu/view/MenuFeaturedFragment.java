package caribouapp.caribou.com.cariboucoffee.mvp.menu.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.mvp.menu.MenuContract;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategoryModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter.MenuFragmentPresenter;

/**
 * Created by jmsmuy on 2/14/18.
 */
public class MenuFeaturedFragment extends MenuProductFragment implements MenuContract.Fragments.Featured.View {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getBinding().rlFilters.setVisibility(View.GONE);
        getBinding().rlSearchView.setVisibility(View.GONE);
        return view;
    }

    @Override
    protected MenuFragmentPresenter getPresenter(MenuCategoryModel model) {
        return new MenuFragmentPresenter(this, model);
    }

}
