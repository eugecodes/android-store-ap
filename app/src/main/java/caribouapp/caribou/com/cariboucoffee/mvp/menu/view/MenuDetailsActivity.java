package caribouapp.caribou.com.cariboucoffee.mvp.menu.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityMenuDetailsBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.MenuDetailsContract;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter.MenuDetailsPresenter;

/**
 * Created by jmsmuy on 10/16/17.
 */

public class MenuDetailsActivity extends BaseActivity<ActivityMenuDetailsBinding> implements MenuDetailsContract.View {

    private MenuDetailsContract.Presenter mPresenter;

    public static Intent createIntent(Context context, MenuCardItemModel menuProduct) {
        Intent intent = new Intent(context, MenuDetailsActivity.class);
        intent.putExtra(AppConstants.EXTRA_MENU_PRODUCT, menuProduct);
        return intent;
    }

    public MenuCardItemModel getProductDetailsFromIntent() {
        return (MenuCardItemModel) getIntent().getSerializableExtra(AppConstants.EXTRA_MENU_PRODUCT);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_menu_details;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MenuCardItemModel model = getProductDetailsFromIntent();
        mPresenter = new MenuDetailsPresenter(this, model);
        getBinding().setModel(model);

        getBinding().contentIncluded.tvNutritionLink.
                setOnClickListener(view -> startActivity(
                        MenuNutritionActivity.createIntent(MenuDetailsActivity.this, mPresenter.getModel())));

        // Sets up toolbar
        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mPresenter.loadData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void onNoNutritionAllergensAvailable() {
        getBinding().contentIncluded.tvNutritionLink.setVisibility(View.GONE);
    }

    @Override
    public void onNoAllergensAvailable() {
        getBinding().contentIncluded.tvNutritionLink.setText(R.string.view_nutrition);
    }

    @Override
    public void onNoNutritionAvailable() {
        getBinding().contentIncluded.tvNutritionLink.setText(R.string.view_allergens);
    }
}
