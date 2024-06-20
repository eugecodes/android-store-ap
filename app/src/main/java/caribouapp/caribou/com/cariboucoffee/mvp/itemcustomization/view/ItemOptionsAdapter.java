package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutItemOptionListItemBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;

/**
 * Created by asegurola on 3/22/18.
 */

public class ItemOptionsAdapter extends RecyclerView.Adapter<ItemOptionsAdapter.OptionVH> {

    public interface OptionListener {
        void modifierSelected(ItemOption itemOption);
    }

    private ItemOption mSelected;
    private List<ItemOption> mOptions = new ArrayList<>();
    private OptionListener mListener;

    @NonNull
    @Override
    public OptionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OptionVH(LayoutItemOptionListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OptionVH holder, int position) {
        holder.bindTo(mOptions.get(position));
    }

    public void setOptionListener(OptionListener listener) {
        mListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<ItemOption> options, ItemOption itemOption) {
        mOptions = new ArrayList<>(options);
        mSelected = itemOption;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mOptions.size();
    }

    class OptionVH extends RecyclerView.ViewHolder {

        private final LayoutItemOptionListItemBinding mBinding;

        OptionVH(LayoutItemOptionListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.getRoot().setOnClickListener((view) -> {
                ItemOption old = mSelected;
                mSelected = mBinding.getModel();
                notifyItemChanged(getAdapterPosition());
                if (old != null) {
                    notifyItemChanged(mOptions.indexOf(old));
                }
                if (mListener != null) {
                    mListener.modifierSelected(mBinding.getModel());
                }
            });
        }

        public void bindTo(ItemOption itemOption) {
            mBinding.setModel(itemOption);
            Context context = mBinding.getRoot().getContext();
            mBinding.rbOption.setChecked(mSelected != null && mSelected.equals(itemOption));
            mBinding.rbOption.setText(itemOption.isAdditionalCharges()
                    ? context.getResources().getString(R.string.add_charges_asterisk, itemOption.getLabel()) : itemOption.getLabel());
            mBinding.rbOption.setContentDescription(itemOption.isAdditionalCharges()
                    ? context.getResources().getString(R.string.add_charges_asterisk_cd, itemOption.getLabel()) : itemOption.getLabel());
            mBinding.executePendingBindings();
        }
    }
}
