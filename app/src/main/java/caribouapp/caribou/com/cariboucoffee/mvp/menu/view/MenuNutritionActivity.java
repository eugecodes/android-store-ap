package caribouapp.caribou.com.cariboucoffee.mvp.menu.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;
import java.util.Set;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityMenuNutritionBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.MenuNutritionContract;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.NutritionalRowData;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter.MenuNutritionPresenter;

/**
 * Created by jmsmuy on 11/16/17.
 */

public class MenuNutritionActivity extends BaseActivity<ActivityMenuNutritionBinding> implements MenuNutritionContract.View {

    private static final String MENU_NUTRITION_EXTRA = "menu_nutrition_extra";
    private static final String SELECTED_SIZE_EXTRA = "selected_size_extra";
    private static final String SPACE = " ";
    private static final String COMMA_SEPARATOR = ", ";

    private MenuNutritionContract.Presenter mPresenter;
    private MenuNutritionAdapter mAdapter;

    public static Intent createIntent(Context context, MenuCardItemModel menuProduct) {
        return createIntent(context, menuProduct, null);
    }

    public static Intent createIntent(Context context, MenuCardItemModel menuProduct, SizeEnum selectedSizeEnum) {
        Intent intent = new Intent(context, MenuNutritionActivity.class);
        intent.putExtra(MENU_NUTRITION_EXTRA, menuProduct);
        intent.putExtra(SELECTED_SIZE_EXTRA, selectedSizeEnum);
        return intent;
    }

    public MenuCardItemModel getProductDetailsFromIntent() {
        return (MenuCardItemModel) getIntent().getSerializableExtra(MENU_NUTRITION_EXTRA);
    }

    public SizeEnum getProductSelectedSizeFromIntent() {
        return (SizeEnum) getIntent().getSerializableExtra(SELECTED_SIZE_EXTRA);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets up toolbar
        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        MenuNutritionPresenter presenter = new MenuNutritionPresenter(this);
        SourceApplication.get(this).getComponent().inject(presenter);
        mPresenter = presenter;
        mPresenter.setModel(getProductDetailsFromIntent());

        // Sets up the adapter for the perks
        mAdapter = new MenuNutritionAdapter();
        getBinding().contentIncluded.rvNutritionItems.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        getBinding().contentIncluded.rvNutritionItems.setAdapter(mAdapter);


        // Initial size is medium when loading screen
        mPresenter.loadData(getProductSelectedSizeFromIntent());

        getBinding().contentIncluded.ssSize.setItemSizeListener(size -> mPresenter.setSize(size));

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_menu_nutrition;
    }

    @Override
    public void setAllergens(List<String> allergens) {
        if (allergens != null && allergens.size() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(getString(R.string.allergens));
            builder.append(SPACE);
            for (int i = 0; i < allergens.size(); i++) {
                builder.append(allergens.get(i));
                if (i < allergens.size() - 1) {
                    builder.append(COMMA_SEPARATOR);
                }
            }
            getBinding().contentIncluded.tvAllergens.setText(builder.toString());
        } else {
            getBinding().contentIncluded.tvAllergens.setText(getString(R.string.no_allergens));
        }
    }

    @Override
    public void setSize(SizeEnum size) {
        getBinding().contentIncluded.ssSize.setSize(size);
    }

    @Override
    public void setPossibleSizes(Set<SizeEnum> possibleSizes) {
        SizeSelectorView sizeSelectorView = getBinding().contentIncluded.ssSize;
        sizeSelectorView.setEnabledSizes(possibleSizes);
    }

    @Override
    public void refreshTable() {
        mAdapter.refreshData();
    }

    @Override
    public void disableSizes() {
        getBinding().contentIncluded.ssSize.setVisibility(View.GONE);
        getBinding().contentIncluded.tvOneSize.setVisibility(View.VISIBLE);
    }

    @Override
    public void setNutritionalValuesList(List<NutritionalRowData> nutritionalValues) {
        mAdapter.setList(nutritionalValues);
    }

    @Override
    public void onNoNutritionAvailable() {
        getBinding().contentIncluded.groupNutritionContainer.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

}
