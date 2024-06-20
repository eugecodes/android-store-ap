package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.databinding.LayoutModifierAndQuantityListItemBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OptionSelectorModel;

/**
 * Created by asegurola on 3/22/18.
 */

public class ModifierAndQuantityAdapter extends RecyclerView.Adapter<ModifierAndQuantityAdapter.OptionVH> {

    public interface ModifierQuantityListener {
        void optionChanged(ItemModifier itemModifier, ItemOption itemOption);
    }

    private List<ItemModifier> mModifiers = new ArrayList<>();
    private ModifierQuantityListener mListener;
    private Map<ItemModifier, ItemOption> mSelections;

    @NonNull
    @Override
    public OptionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OptionVH(LayoutModifierAndQuantityListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OptionVH holder, int position) {
        holder.bindTo(mModifiers.get(position));
    }

    public void setModifierListener(ModifierQuantityListener listener) {
        mListener = listener;
    }

    public void setData(List<ItemModifier> modifiers, Map<ItemModifier, ItemOption> selections) {
        mModifiers = modifiers;
        mSelections = selections;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mModifiers.size();
    }

    class OptionVH extends RecyclerView.ViewHolder {

        private LayoutModifierAndQuantityListItemBinding mBinding;

        OptionSelectorView.OptionSelectorListener mOptionSelectorListener = itemOption -> mListener.optionChanged(mBinding.getModel(), itemOption);

        OptionVH(LayoutModifierAndQuantityListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bindTo(ItemModifier modifier) {
            mBinding.setModel(modifier);

            OptionSelectorModel optionSelectorModel = new OptionSelectorModel();
            optionSelectorModel.setOptions(modifier.getOptions());
            optionSelectorModel.setDefaultOption(modifier.getDefaultOption());
            ItemOption selectedOption = mSelections.get(modifier);
            if (selectedOption != null) {
                optionSelectorModel.select(selectedOption);
            }
            mBinding.osvOptionName.setOptionListener(mOptionSelectorListener);
            mBinding.osvOptionName.setModel(optionSelectorModel);
            mBinding.executePendingBindings();
        }
    }
}
