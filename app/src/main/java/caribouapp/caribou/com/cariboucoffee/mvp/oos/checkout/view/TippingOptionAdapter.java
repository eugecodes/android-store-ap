package caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.databinding.LayoutTippingBoxBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.TippingListener;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.PercentageTipOption;
import caribouapp.caribou.com.cariboucoffee.order.Order;

public class TippingOptionAdapter extends RecyclerView.Adapter<TippingOptionAdapter.ItemHolder> {

    private List<PercentageTipOption> mTippingOptions;
    private Order mOrder;
    private TippingListener mTippingListener;

    private static final String PERCENTAGE_FOR_VIEW = "%";
    private PercentageTipOption mSelectedTipOption;

    public TippingOptionAdapter(TippingListener listener, Order order) {
        mTippingOptions = new ArrayList<>();
        mTippingListener = listener;
        mOrder = order;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TippingOptionAdapter.ItemHolder(LayoutTippingBoxBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.bindTo(mTippingOptions.get(position));
    }

    public void setItems(List<PercentageTipOption> tippingOptions) {
        mTippingOptions.addAll(tippingOptions);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mTippingOptions.size();
    }

    public void setSelectedOption(PercentageTipOption selectedTippingOption) {
        mSelectedTipOption = selectedTippingOption;
        notifyDataSetChanged();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        private LayoutTippingBoxBinding mBinding;

        ItemHolder(LayoutTippingBoxBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
        }

        public void bindTo(PercentageTipOption tippingOption) {
            mBinding.setPercentageOfTip(tippingOption.getPercentage() + PERCENTAGE_FOR_VIEW);
            mBinding.setAmountOfTip(tippingOption.calculateTip(mOrder));
            mBinding.llTippingContainer.setOnClickListener(onclick -> {
                mBinding.llTippingContainer.setSelected(true);
                mTippingListener.setTipping(tippingOption);
            });
            mBinding.llTippingContainer.setSelected(mSelectedTipOption != null
                    && mSelectedTipOption.getPercentage().equals(tippingOption.getPercentage()));
        }
    }
}
