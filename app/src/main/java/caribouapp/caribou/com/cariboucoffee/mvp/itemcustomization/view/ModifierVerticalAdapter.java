package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.databinding.LayoutModifierOptionListItemBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;

/**
 * Created by asegurola on 3/22/18.
 */

public class ModifierVerticalAdapter extends RecyclerView.Adapter<ModifierVerticalAdapter.OptionVH> {

    public interface ModifierListener {
        void modifierSelected(ItemModifier itemModifier);
    }

    private ItemModifier mSelected;
    private List<ItemModifier> mModifiers = new ArrayList<>();
    private ModifierListener mListener;

    @NonNull
    @Override
    public OptionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OptionVH(LayoutModifierOptionListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OptionVH holder, int position) {
        holder.bindTo(mModifiers.get(position));
    }

    public void setModifierListener(ModifierListener listener) {
        mListener = listener;
    }

    public void setSelected(ItemModifier itemModifier) {
        mSelected = itemModifier;
        notifyDataSetChanged();
    }

    public void setData(List<ItemModifier> options) {
        mModifiers = new ArrayList<>(options);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mModifiers.size();
    }

    class OptionVH extends RecyclerView.ViewHolder {

        private final LayoutModifierOptionListItemBinding mBinding;

        OptionVH(LayoutModifierOptionListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.getRoot().setOnClickListener((view) -> {
                ItemModifier old = mSelected;
                mSelected = mBinding.getModel();
                notifyItemChanged(getAdapterPosition());
                if (old != null) {
                    notifyItemChanged(mModifiers.indexOf(old));
                }
                if (mListener != null) {
                    mListener.modifierSelected(mBinding.getModel());
                }
            });
        }

        public void bindTo(ItemModifier modifier) {
            mBinding.setModel(modifier);
            mBinding.ivSelect.setActivated(mSelected != null && mSelected.equals(modifier));
            mBinding.executePendingBindings();
        }
    }
}
