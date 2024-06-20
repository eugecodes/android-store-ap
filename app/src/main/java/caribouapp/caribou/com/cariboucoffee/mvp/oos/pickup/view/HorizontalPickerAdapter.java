package caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.databinding.LayoutSingleOptionBinding;

public class HorizontalPickerAdapter extends RecyclerView.Adapter<HorizontalPickerAdapter.OptionVH> {

    private List<SingleOption> mOptions = new ArrayList<>();

    private SingleOption mSelected;

    public interface SingleOptionListener {
        void singleOptionSelected(SingleOption singleOption);
    }

    private SingleOptionListener mListener;

    @NonNull
    @Override
    public OptionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OptionVH(LayoutSingleOptionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OptionVH holder, int position) {
        holder.bindTo(mOptions.get(position));
    }

    @Override
    public int getItemCount() {
        return mOptions.size();
    }

    public void setData(List<SingleOption> options) {
        mOptions = options;
        if (mSelected == null && options.size() > 0) {
            mSelected = options.get(0);
        }
        notifyDataSetChanged();
    }

    public void setSelected(SingleOption singleOption) {
        mSelected = singleOption;
        notifyDataSetChanged();
    }

    public void setListener(SingleOptionListener listener) {
        mListener = listener;
    }

    class OptionVH extends RecyclerView.ViewHolder {

        private final LayoutSingleOptionBinding mBinding;

        OptionVH(LayoutSingleOptionBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bindTo(SingleOption option) {
            Context context = mBinding.getRoot().getContext();
            mBinding.setModel(option);

            // Set selected radio button. Either there an option selected already or we set the first radio button as selected.
            mBinding.rbOption.setChecked(option.equals(mSelected));

            mBinding.getRoot().setOnClickListener((view) -> {
                setSelected(option);
                if (mListener != null) {
                    mListener.singleOptionSelected(option);
                }
            });
            mBinding.executePendingBindings();
        }
    }

    public List<SingleOption> getOptions() {
        return mOptions;
    }
}
