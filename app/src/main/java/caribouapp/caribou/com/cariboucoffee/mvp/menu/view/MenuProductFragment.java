package caribouapp.caribou.com.cariboucoffee.mvp.menu.view;

import static android.app.Activity.RESULT_OK;
import static caribouapp.caribou.com.cariboucoffee.mvp.menu.view.MenuActivity.MenuOrigin.CART;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.databinding.FragmentMenuBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.BaseFragment;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.view.ItemActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.MenuContract;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategory;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategoryModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter.MenuFragmentPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.cart.view.CartActivity;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import icepick.Icepick;
import icepick.State;

/**
 * Created by jmsmuy on 2/9/18.
 */
public abstract class MenuProductFragment extends BaseFragment<FragmentMenuBinding> implements MenuContract.Fragments.View {

    public static final int REQUEST_CODE_FILTER = 1;
    public static final String FILTER_DATA = "filter_data";
    private static final String TAG = MenuProductFragment.class.getSimpleName();
    private static final int AMOUNT_CARD_COLUMNS = 2;
    private static final int ITEM_SPAN = 1;
    private static final int HEADER_SPAN = 2;
    @State
    MenuCategoryModel mModel;

    @Inject
    OrderService mOrderService;

    @Inject
    SettingsServices mSettingsServices;

    private MenuContract.Fragments.Presenter mPresenter;
    private HashMap<MenuCategory, TextView> mLevel2ButtonViewMap;
    private HashMap<MenuCategory, MenuThirdLevelCustomView> mLevel3ButtonViewMap;
    private MenuCategoryCardAdapter mCardAdapter;
    private boolean mFinishedLoadingFragment = false;
    private boolean mFilteredMenu;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        Icepick.restoreInstanceState(this, savedInstanceState);

        SourceApplication.get(getContext()).getComponent().inject(this);
        MenuFragmentPresenter presenter = getPresenter(mModel);
        SourceApplication.get(getContext()).getComponent().inject(presenter);
        mPresenter = presenter;

        mLevel2ButtonViewMap = new HashMap<>();
        mLevel3ButtonViewMap = new HashMap<>();
        mCardAdapter = new MenuCategoryCardAdapter(this, mSettingsServices);
        getBinding().rvMenuCards.setAdapter(mCardAdapter);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), AMOUNT_CARD_COLUMNS);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int itemType = mCardAdapter.getItemViewType(position);
                switch (itemType) {
                    case MenuCategoryCardAdapter.HEADER_TYPE:
                        return HEADER_SPAN;
                    case MenuCategoryCardAdapter.ITEM_TYPE:
                        return ITEM_SPAN;
                }
                return -1;
            }
        });
        getBinding().rvMenuCards.setLayoutManager(mLayoutManager);
        getBinding().sv.getBinding().etSearchText
                .setContentDescription(getContext().getString(R.string.search_product_field_content_description));
        getBinding().sv.setPresenter(mPresenter);

        getBinding().setFilterButtonListener(view1 -> mPresenter.showFilterDialog());

        mPresenter.setData(mModel);

        if (mFilteredMenu) {
            mPresenter.selectSubCategoryWithMostItems();
        }

        mFinishedLoadingFragment = true;

        // This is just to remove focus from the search bar at the very beginning
        getBinding().rvMenuCards.requestFocus();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getBinding() != null) {
            // This is just to remove focus from the search bar when switching tabs
            getBinding().rvMenuCards.requestFocus();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    protected abstract MenuFragmentPresenter getPresenter(MenuCategoryModel model);

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_menu;
    }

    @Override
    public void addLevel2Filter(final MenuCategory category, int categoryIndex, int categoriesSize) {
        final TextView button = MenuTypeButtonBuilder.createToggleButton(getActivity(), getBinding().llToggleButtonLayout);
        button.setText(category.getName());
        button.setTag(category);
        button.setContentDescription(getString(R.string.product_category_level2_content_description,
                category.getName().toUpperCase(Locale.US), categoryIndex, categoriesSize));
        button.setOnClickListener(v -> {
            mModel.setSelectedLvl2Filter((MenuCategory) button.getTag());
            getBinding().sv.setText(null);
        });
        getBinding().llToggleButtonLayout.addView(button);
        mLevel2ButtonViewMap.put(category, button);
    }

    @Override
    public void updateSelectedLvl2Filter(MenuCategory category) {
        TextView selectedButton = mLevel2ButtonViewMap.get(category);
        for (MenuCategory menuCategory : mLevel2ButtonViewMap.keySet()) {
            TextView level2Button = mLevel2ButtonViewMap.get(menuCategory);
            level2Button.setActivated(false);
            //Check for old category that is not the same object that is on the Map
            // So we have to match the old category object with the current one
            if (selectedButton == null && menuCategory.getName().equals(category.getName())) {
                selectedButton = level2Button;
            }
        }
        selectedButton.setActivated(true);
    }

    @Override
    public void addLevel3Filter(final MenuCategory category) {
        final MenuThirdLevelCustomView filter = new MenuThirdLevelCustomView(getContext());
        filter.setFilter(category);
        filter.setPresenter(mPresenter);
        getBinding().llLvl3Filters.addView(filter);
        mLevel3ButtonViewMap.put(category, filter);
    }

    @Override
    public void removeLevel3Filters() {
        if (mLevel3ButtonViewMap == null) {
            return;
        }
        for (MenuThirdLevelCustomView filter : mLevel3ButtonViewMap.values()) {
            getBinding().llLvl3Filters.removeView(filter);
        }
        mLevel3ButtonViewMap = new HashMap<>();
    }

    @Override
    public void updateItems(List<MenuCardModel> modelCards) {
        Log.d(TAG, "Updating items: " + modelCards.size());
        mCardAdapter.updateData(modelCards);
        if (!modelCards.isEmpty()) {
            getBinding().rvMenuCards.setVisibility(View.VISIBLE);
            getBinding().tvEmptyMenu.setVisibility(View.GONE);
        } else {
            getBinding().rvMenuCards.setVisibility(View.GONE);
            getBinding().tvEmptyMenu.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void goToMenuDetails(MenuCardItemModel menuCardItemModel) {
        if (mModel.isOrderAvailable()) {
            mOrderService.updateLastActivity();
            startActivityForResult(
                    ItemActivity.createIntent(getContext(), menuCardItemModel, null), AppConstants.REQUEST_CODE_ITEM_CUSTOMIZATION);
        } else {
            startActivity(
                    MenuDetailsActivity.createIntent(getContext(), menuCardItemModel));
        }
    }

    public void setData(MenuCategoryModel menuCategoryModel, boolean filteredMenu) {
        mModel = menuCategoryModel;
        mFilteredMenu = filteredMenu;
        // This is a simple way to check if the fragment finished loading
        if (!mFinishedLoadingFragment) {
            return;
        }
        // We update the data if needed
        mPresenter.setData(mModel);
        if (filteredMenu) {
            mPresenter.selectSubCategoryWithMostItems();
        }
    }

    @Override
    public void removeLevel2Filters() {
        getBinding().llToggleButtonLayout.removeAllViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public void showFilterDialog(ArrayList<MenuCategory> filtersLevel3, HashSet<MenuCategory> currentFilters) {
        MenuFilterFragmentDialog dialog = MenuFilterFragmentDialogBuilder.newMenuFilterFragmentDialog(filtersLevel3, currentFilters);
        dialog.setTargetFragment(this, REQUEST_CODE_FILTER);
        dialog.show(getActivity().getSupportFragmentManager(), AppConstants.FILTER_DIALOG_FRAGMENT);
    }

    @Override
    public void level3FiltersListEnabled(boolean enabled) {
        getBinding().rlLvl3Filters.setVisibility(enabled ? View.VISIBLE : View.GONE);
        getBinding().rlSearchView.setVisibility(!enabled ? View.VISIBLE : View.GONE);
    }

    @Override
    public void level3FiltersButtonEnabled(boolean enabled) {
        getBinding().ivFilter.setVisibility(enabled ? View.VISIBLE : View.GONE);
        getBinding().ivFilter2.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.REQUEST_CODE_ITEM_CUSTOMIZATION && resultCode == AppConstants.RESULT_CODE_GO_TO_CART) {
            mPresenter.itemCustomizationFinished();
        } else if (requestCode == REQUEST_CODE_FILTER && resultCode == RESULT_OK) {
            HashSet<MenuCategory> filter = (HashSet<MenuCategory>) data.getSerializableExtra(FILTER_DATA);
            if (filter == null) {
                return;
            }
            mPresenter.applyLevel3Filter(filter);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void showItemNotAvailableDialog() {
        new AlertDialog.Builder(getContext())
                .setMessage(getString(R.string.menu_item_not_available_dialog_message))
                .setPositiveButton(R.string.okay, (dialog, which) -> dialog.dismiss())
                .show();
    }

    public int getProductCount() {
        return mPresenter.getItemCount();
    }

    @Override
    public void goToCart() {
        if (((MenuActivity) getActivity()).getOriginFromIntent() == CART) {
            getActivity().finish();
        }
        startActivity(CartActivity.createIntent(getContext()));
    }
}
