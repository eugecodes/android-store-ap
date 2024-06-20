package caribouapp.caribou.com.cariboucoffee.mvp.menu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.databinding.LayoutThirdLevelFilterBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.MenuContract;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategory;

/**
 * Created by jmsmuy on 29/03/18.
 */

public class MenuThirdLevelCustomView extends LinearLayout {

    private LayoutThirdLevelFilterBinding mBinding;
    private MenuContract.Fragments.Presenter mPresenter;
    private MenuCategory mCategory;

    public MenuThirdLevelCustomView(Context context) {
        super(context);
        init();
    }

    public MenuThirdLevelCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MenuThirdLevelCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBinding = LayoutThirdLevelFilterBinding.inflate(LayoutInflater.from(getContext()), this, true);
        mBinding.ivRemoveFilter.setOnClickListener(view -> mPresenter.removeLevel3Filter(mCategory));
    }

    public void setPresenter(MenuContract.Fragments.Presenter presenter) {
        mPresenter = presenter;
    }

    public void setFilter(MenuCategory category) {
        mCategory = category;
        mBinding.tvFilterName.setText(category.getName());
    }
}
