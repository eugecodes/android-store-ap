package caribouapp.caribou.com.cariboucoffee.mvp.menu.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.DialogLocationsFilterBinding;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutMenuFilterItemBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategory;

import static android.app.Activity.RESULT_OK;

/**
 * Created by andressegurola on 10/17/17.
 */
@FragmentWithArgs
public class MenuFilterFragmentDialog extends DialogFragment {

    private DialogLocationsFilterBinding mBinding;

    @Arg
    HashSet<MenuCategory> mCurrentFilter;

    @Arg
    ArrayList<MenuCategory> mAllFilters;

    private Map<MenuCategory, LayoutMenuFilterItemBinding> mItemBindings;
    private MenuCategory mAllCategory;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_filter_locations_title)
                .setPositiveButton(R.string.dialog_apply_filter,
                        (dialog, which) -> {
                            if (getTargetFragment() == null) {
                                return;
                            }
                            Intent intent = new Intent();
                            intent.putExtra(MenuProductFragment.FILTER_DATA, createFilterOptions());
                            getTargetFragment().onActivityResult(MenuProductFragment.REQUEST_CODE_FILTER, RESULT_OK, intent);
                            dismiss();
                        })
                .setNegativeButton(R.string.dialog_close, (dialog, which) -> dismiss()).setView(createView()).create();
    }

    private HashSet<MenuCategory> createFilterOptions() {
        HashSet<MenuCategory> filters = new HashSet<>();
        for (Map.Entry<MenuCategory, LayoutMenuFilterItemBinding> mapEntry : mItemBindings.entrySet()) {
            if (mapEntry.getValue().tvFilterName.isEnabled() && !mapEntry.getKey().equals(mAllCategory)) {
                filters.add(mapEntry.getKey());
            } else {
                filters.remove(mapEntry.getKey());
            }
        }
        return filters;
    }

    private View createView() {
        FragmentArgs.inject(this); // read @Arg fields
        mBinding = DialogLocationsFilterBinding.inflate(LayoutInflater.from(getContext()));
        mItemBindings = new HashMap<>();
        addAllCategory();
        for (final MenuCategory filter : mAllFilters) {
            LayoutMenuFilterItemBinding binding = LayoutMenuFilterItemBinding
                    .inflate(LayoutInflater.from(getContext()),
                            mBinding.llFeatureFilterContainer, true);

            binding.setListener(v -> selectFilter(filter));

            binding.setModel(filter);
            mItemBindings.put(filter, binding);
            setFilterSelected(filter, mCurrentFilter.contains(filter));
        }

        return mBinding.getRoot();
    }

    private void addAllCategory() {
        mAllCategory = new MenuCategory(getString(R.string.all));
        LayoutMenuFilterItemBinding binding =
                LayoutMenuFilterItemBinding
                        .inflate(LayoutInflater.from(getContext()),
                                mBinding.llFeatureFilterContainer, true);
        binding.setListener(v -> selectAllFilter());

        binding.setModel(mAllCategory);
        mItemBindings.put(mAllCategory, binding);
        setFilterSelected(mAllCategory, mCurrentFilter == null || mCurrentFilter.isEmpty());
    }

    private void selectAllFilter() {
        for (Map.Entry<MenuCategory, LayoutMenuFilterItemBinding> mapEntry : mItemBindings.entrySet()) {
            setFilterSelected(mapEntry.getKey(), mapEntry.getKey().equals(mAllCategory));
        }
    }

    private void selectFilter(MenuCategory filter) {
        setFilterSelected(filter, !mItemBindings.get(filter).tvFilterName.isEnabled());
        setFilterSelected(mAllCategory, isAllDeselected(mItemBindings));
    }

    private boolean isAllDeselected(Map<MenuCategory, LayoutMenuFilterItemBinding> itemBindings) {
        for (Map.Entry<MenuCategory, LayoutMenuFilterItemBinding> mapEntry : itemBindings.entrySet()) {
            if (!mapEntry.getKey().equals(mAllCategory) && mapEntry.getValue().tvFilterName.isEnabled()) {
                return false;
            }
        }
        return true;
    }

    private void setFilterSelected(MenuCategory filter, boolean selected) {
        LayoutMenuFilterItemBinding itemBinding = mItemBindings.get(filter);
        itemBinding.tvFilterName.setEnabled(selected);
        itemBinding.ivFilterApplied.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
    }
}
