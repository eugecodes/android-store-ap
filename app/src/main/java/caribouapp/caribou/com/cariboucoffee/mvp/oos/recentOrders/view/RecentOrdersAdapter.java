package caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.CardRecentOrderBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.RecentOrderContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderModel;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.RecentOrderStore;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;

/**
 * Created by jmsmuy on 4/9/18.
 */

public class RecentOrdersAdapter extends RecyclerView.Adapter {

    private final RecentOrderContract.Presenter mPresenter;
    private List<RecentOrderModel> mOrderList;

    public RecentOrdersAdapter(RecentOrderContract.Presenter presenter) {
        mPresenter = presenter;
        mOrderList = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecentOrderHolder(CardRecentOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RecentOrderHolder) holder).bind(mOrderList.get(position));
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public void setOrderList(List<RecentOrderModel> list) {
        if (list == null) {
            return;
        }
        mOrderList.clear();
        mOrderList.addAll(list);
        notifyDataSetChanged();
    }

    private class RecentOrderHolder extends RecyclerView.ViewHolder {

        private final CardRecentOrderBinding mBinding;

        RecentOrderHolder(CardRecentOrderBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(RecentOrderModel recentOrderModel) {
            Context context = mBinding.getRoot().getContext();
            mBinding.getRoot().setContentDescription(context.getString(R.string.row_content_description, mOrderList.indexOf(recentOrderModel) + 1));
            mBinding.btnViewAndReorder.setOnClickListener(v -> {
                mPresenter.startReorder(recentOrderModel);
            });
            mBinding.rvOrderDetails.setLayoutManager(new LinearLayoutManager(context));
            mBinding.setModel(recentOrderModel);
            RecentOrderDetailsAdapter recentOrderDetailsAdapter = new RecentOrderDetailsAdapter();
            mBinding.rvOrderDetails.setAdapter(recentOrderDetailsAdapter);
            recentOrderDetailsAdapter.setData(recentOrderModel.getOrderItems());

            RecentOrderStore recentStoreLocation = recentOrderModel.getRecentOrderStore();
            mBinding.llRecentOrderContainer.setContentDescription(context.getString(R.string.recent_order_content_description,
                    mOrderList.indexOf(recentOrderModel) + 1,
                    recentStoreLocation.getName(),
                    recentStoreLocation.getAddress(), generateRecentOrderItemsString(recentOrderModel)));

            mBinding.executePendingBindings();
        }

        private String generateRecentOrderItemsString(RecentOrderModel recentOrderModel) {
            StringBuilder stringBuilderRecentOrderItems = new StringBuilder();
            if (recentOrderModel.getOrderItems() != null) {
                Iterator<RecentOrderItem> iter = recentOrderModel.getOrderItems().iterator();
                while (iter.hasNext()) {
                    RecentOrderItem recentOrderItem = iter.next();
                    StringBuilder currentItem = new StringBuilder();
                    currentItem.append(recentOrderItem.getQuantity()).append(" ");
                    SizeEnum currentSize = recentOrderItem.getSizeEnum();
                    if (currentSize != null) {
                        currentItem.append(currentSize.getOmsOrderingSizeName()).append(" ");
                    }
                    currentItem.append(recentOrderItem.getProductName()).append(", ");
                    currentItem.append(StringUtils.joinWith(recentOrderItem.getCustomizations(), ", ", true));
                    stringBuilderRecentOrderItems.append(currentItem);
                    if (iter.hasNext()) {
                        stringBuilderRecentOrderItems.append(", ");
                    }
                }
            }
            return stringBuilderRecentOrderItems.toString();
        }
    }
}
