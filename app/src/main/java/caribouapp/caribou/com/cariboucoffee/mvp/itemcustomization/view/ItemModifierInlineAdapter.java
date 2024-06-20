package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutEditInlineModifierValueBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ModifierGroup;

/**
 * Created by asegurola on 3/30/18.
 */

public class ItemModifierInlineAdapter extends RecyclerView.Adapter<ItemModifierInlineAdapter.InlineVH> {

    private ModifierGroup mModifierGroup;

    private ItemModifier mSelected;

    public interface ItemModifierInlineListener {
        void inlineItemOptionSelected(ModifierGroup modifier, ItemModifier itemModifier);
    }


    private ItemModifierInlineListener mListener;

    @NonNull
    @Override
    public InlineVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InlineVH(LayoutEditInlineModifierValueBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InlineVH holder, int position) {
        holder.bindTo(mModifierGroup.getItemModifiers().get(position));
    }

    @Override
    public int getItemCount() {
        return mModifierGroup.getItemModifiers().size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(ModifierGroup modifierGroup, ItemModifier selected) {
        mSelected = selected;
        mModifierGroup = modifierGroup;
        if (mSelected == null) {
            mSelected = modifierGroup.getDefaultItemModifier();
        }
        notifyDataSetChanged();
    }

    public void setListener(ItemModifierInlineListener listener) {
        mListener = listener;
    }

    class InlineVH extends RecyclerView.ViewHolder {

        private final LayoutEditInlineModifierValueBinding mBinding;

        InlineVH(LayoutEditInlineModifierValueBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        @SuppressLint("NotifyDataSetChanged")
        public void bindTo(ItemModifier itemModifier) {
            Context context = mBinding.getRoot().getContext();
            mBinding.setModel(itemModifier);

            // Set selected radio button. Either there an option selected already or we set the first radio button as selected.
            mBinding.rbOption.setChecked(itemModifier.equals(mSelected));

            mBinding.getRoot().setOnClickListener((view) -> {
                if (mListener != null) {
                    mSelected = itemModifier;
                    mListener.inlineItemOptionSelected(mModifierGroup, itemModifier);
                    notifyDataSetChanged();
                }
            });
            for (ItemOption itemOption : itemModifier.getOptions()) {
                if (itemOption.isAdditionalCharges()) {
                    mBinding.rbOption.setText(context.getString(R.string.add_charges_asterisk, itemModifier.getName()));
                    mBinding.rbOption.setContentDescription(context.getString(R.string.add_charges_asterisk_cd, itemModifier.getName()));
                } else {
                    mBinding.rbOption.setText(itemModifier.getName());
                    mBinding.rbOption.setContentDescription(itemModifier.getName());
                }
            }

            String selectedOption = mSelected == null ? context.getString(R.string.customization_none_cd) : mSelected.getName();

            mBinding.getRoot()
                    .setContentDescription(
                            context.getString(
                                    R.string.single_selection_modifier_content_description,
                                    itemModifier.getName(), selectedOption));

            mBinding.executePendingBindings();
        }
    }
}
