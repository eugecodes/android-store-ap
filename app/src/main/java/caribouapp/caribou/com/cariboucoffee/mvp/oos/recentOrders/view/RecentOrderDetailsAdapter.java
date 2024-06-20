package caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.databinding.LayoutRecentOrderItemsBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderItem;

/**
 * Created by jmsmuy on 4/9/18.
 */

public class RecentOrderDetailsAdapter extends RecyclerView.Adapter {

    private List<RecentOrderItem> mItems;

    public void setData(List<RecentOrderItem> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderDetailsHolder(LayoutRecentOrderItemsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((OrderDetailsHolder) holder).bind(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private class OrderDetailsHolder extends RecyclerView.ViewHolder {

        private final LayoutRecentOrderItemsBinding mBinding;

        OrderDetailsHolder(LayoutRecentOrderItemsBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(RecentOrderItem model) {
            mBinding.setModel(model);
            mBinding.executePendingBindings();
        }
    }
}
