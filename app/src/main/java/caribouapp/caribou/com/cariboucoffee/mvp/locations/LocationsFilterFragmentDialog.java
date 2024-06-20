package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.databinding.DialogLocationsFilterBinding;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutLocationsFilterItemBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;

/**
 * Created by andressegurola on 10/17/17.
 */
@FragmentWithArgs
public class LocationsFilterFragmentDialog extends DialogFragment {

    private DialogLocationsFilterBinding mBinding;

    @Arg
    HashSet<StoreFeature> mCurrentFilter;

    private Map<StoreFeature, LayoutLocationsFilterItemBinding> mItemBindings;

    private LocationsFilterListener mListener;

    @Inject
    SettingsServices mSettingsServices;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof LocationsFilterListener) {
            mListener = (LocationsFilterListener) getActivity();
        }
        SourceApplication.get(getActivity()).getComponent().inject(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.dialog_filter_locations_title)
                .setPositiveButton(R.string.dialog_apply_filter,
                        (dialog, which) -> mListener.applyFilters(createFilterOptions()))
                .setNegativeButton(R.string.dialog_close, (dialog, which) -> dismiss()).setView(createView()).create();
    }

    private Set<StoreFeature> createFilterOptions() {
        Set<StoreFeature> storeFeatures = new HashSet<>();
        for (Map.Entry<StoreFeature, LayoutLocationsFilterItemBinding> mapEntry : mItemBindings.entrySet()) {
            if (mapEntry.getValue().tvFilterName.isEnabled()) {
                storeFeatures.add(mapEntry.getKey());
            } else {
                storeFeatures.remove(mapEntry.getKey());
            }
        }
        return storeFeatures;
    }

    private View createView() {
        FragmentArgs.inject(this); // read @Arg fields
        mBinding = DialogLocationsFilterBinding.inflate(getLayoutInflater());
        mItemBindings = new HashMap<>();
        for (final StoreFeature storeFeature : StoreFeature.values()) {

            if (storeFeature == StoreFeature.ORDER_OUT_OF_STORE && !mSettingsServices.isOrderAhead()) {
                // Skip Order Ahead Filter option if not enabled.
                continue;
            }

            LayoutLocationsFilterItemBinding binding =
                    LayoutLocationsFilterItemBinding
                            .inflate(getLayoutInflater(),
                                    mBinding.llFeatureFilterContainer, true);

            binding.setListener(v -> toggleFilter(storeFeature));

            binding.setModel(storeFeature);
            mItemBindings.put(storeFeature, binding);
            setFilterSelected(storeFeature, mCurrentFilter.contains(storeFeature));
        }

        return mBinding.getRoot();
    }

    public void toggleFilter(StoreFeature storeFeature) {
        setFilterSelected(storeFeature, !mItemBindings.get(storeFeature).tvFilterName.isEnabled());
    }

    private void setFilterSelected(StoreFeature storeFeature, boolean selected) {
        LayoutLocationsFilterItemBinding itemBinding = mItemBindings.get(storeFeature);
        itemBinding.tvFilterName.setEnabled(selected);
        itemBinding.ivFilterApplied.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
        if (selected) {
            itemBinding.llFilterContainer.setContentDescription(getString(R.string.selected_cd, itemBinding.tvFilterName.getText()));
        } else {
            itemBinding.llFilterContainer.setContentDescription(itemBinding.tvFilterName.getText());
        }
    }
}
