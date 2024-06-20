package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutModifierGroupListItemBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ModifierGroup;
import caribouapp.caribou.com.cariboucoffee.order.OrderItemUtil;

/**
 * Created by asegurola on 3/19/18.
 */

public class ItemModifierGroupAdapter extends RecyclerView.Adapter<ItemModifierGroupAdapter.ItemVH> {

    private static final String TAG = ItemModifierGroupAdapter.class.getSimpleName();

    private ModifierGroup mInlineEditingItem;

    public interface ItemModifierListener {
        void itemSelected(ModifierGroup modifier);

        void inlineItemOptionSelected(ModifierGroup modifier, ItemModifier itemModifier);
    }

    private ItemModifierListener mListener;

    private List<ModifierGroup> mModifierGroups = new ArrayList<>();

    private Map<ModifierGroup, Map<ItemModifier, ItemOption>> mCurrentSelectionForModifier = new HashMap<>();

    public void setListener(ItemModifierListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemVH(LayoutModifierGroupListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setModifiers(List<ModifierGroup> modifiers) {
        mModifierGroups = new ArrayList<>(modifiers);
        notifyDataSetChanged();
    }

    public void setCustomization(ModifierGroup modifierGroup, Map<ItemModifier, ItemOption> currentSelection) {
        mCurrentSelectionForModifier.put(modifierGroup, currentSelection);
        int position = mModifierGroups.indexOf(modifierGroup);
        notifyItemChanged(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemVH holder, int position) {
        holder.bindTo(mModifierGroups.get(position));
    }

    @Override
    public int getItemCount() {
        return mModifierGroups.size();
    }

    public void enterInlineEditMode(ModifierGroup modifier) {
        mInlineEditingItem = modifier;
        int position = mModifierGroups.indexOf(modifier);
        notifyItemChanged(position);
    }


    public void exitInlineEditMode() {
        int editingItemPosition = -1;
        if (mInlineEditingItem != null) {
            // Save currently item position in edit mode
            editingItemPosition = mModifierGroups.indexOf(mInlineEditingItem);
        }

        mInlineEditingItem = null;

        if (editingItemPosition != -1) {
            // Notifies that the previously editing row has changed
            notifyItemChanged(editingItemPosition);
        }
    }

    class ItemVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        private LayoutModifierGroupListItemBinding mBinding;

        ItemVH(LayoutModifierGroupListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bindTo(ModifierGroup modifierGroup) {
            Context context = mBinding.getRoot().getContext();
            mBinding.setModel(modifierGroup);
            String customization = OrderItemUtil.
                    getCurrentSelectionDescriptionForModifier(context, modifierGroup,
                            mCurrentSelectionForModifier.get(modifierGroup), true);
            String rowDescription = customization == null ? context.getString(R.string.customization_none_cd) : customization;
            mBinding.setRowDescription(rowDescription);
            boolean inlineEditing = mInlineEditingItem != null && mInlineEditingItem.getId().equals(modifierGroup.getId());
            mBinding.setInlineEditing(inlineEditing);
            mBinding.getRoot().setOnClickListener(null);

            if (inlineEditing) {
                setupEditingMode();
            } else {
                mBinding.getRoot().setOnClickListener(this);
                mBinding.getRoot().setContentDescription(
                        context.getString(R.string.group_modifier_content_description, modifierGroup.getName(), rowDescription));
            }
            mBinding.executePendingBindings();
        }

        private void setupEditingMode() {
            ModifierGroup modifierGroup = mBinding.getModel();
            Context context = mBinding.getRoot().getContext();

            Map<ItemModifier, ItemOption> selection = mCurrentSelectionForModifier.get(modifierGroup);
            ItemModifier selectedItemModifier = selection == null || selection.isEmpty() ? null : selection.keySet().iterator().next();

            ItemModifierInlineAdapter adapter = new ItemModifierInlineAdapter();
            adapter.setData(modifierGroup, selectedItemModifier);
            adapter.setListener((modifier, itemModifier) -> {
                if (mListener != null) {
                    mListener.inlineItemOptionSelected(modifier, itemModifier);
                }
            });
            //TODO this should change read some flag to show this and not to match with the name
            mBinding.inlineEditingIncluded.tvAvailabilityNotGuaranteed.setVisibility(
                    modifierGroup.getName().equalsIgnoreCase(AppConstants.OMS_NAME_OF_NOT_GUARANTEED_PRODUCTS) ? View.VISIBLE : View.GONE);
            RecyclerView recyclerView = mBinding.inlineEditingIncluded.rvItemModifier;
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

            if (selectedItemModifier != null) {
                recyclerView.scrollToPosition(modifierGroup.getItemModifiers().indexOf(selectedItemModifier));
                if (selectedItemModifier.hasAdditionalCharges()) {
                    mBinding.getRoot().setContentDescription(
                            context.getString(R.string.single_selection_edit_charges_content_description,
                                    modifierGroup.getName(), selectedItemModifier.getName()));
                } else {
                    mBinding.getRoot().setContentDescription(
                            context.getString(R.string.single_selection_edit_content_description, modifierGroup.getName(),
                                    selectedItemModifier.getName()));
                }
            } else {
                mBinding.getRoot().setContentDescription(
                        context.getString(R.string.single_selection_edit_content_description,
                                modifierGroup.getName(), context.getString(R.string.customization_none_cd)));
            }
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.itemSelected(mBinding.getModel());
            }
        }
    }
}
