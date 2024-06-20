package caribouapp.caribou.com.cariboucoffee.mvp.oos.confirmation.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.BindingAdapters;
import caribouapp.caribou.com.cariboucoffee.databinding.CardItemConfirmationBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.order.OrderItemUtil;

/**
 * Created by gonzalogelos on 3/16/18.
 */

public class OrderConfirmationItemsAdapter extends RecyclerView.Adapter<OrderConfirmationItemsAdapter.ItemHolder> {

    private List<OrderItem> mOrderItems = new ArrayList<>();
    private Context mContext;

    public OrderConfirmationItemsAdapter(Context context) {
        mContext = context;
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<OrderItem> selectedItems) {
        mOrderItems = new ArrayList<>(selectedItems);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(CardItemConfirmationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        final OrderItem orderItem = mOrderItems.get(position);
        if (orderItem != null) {
            holder.bindTo(orderItem, position);
        }
    }

    @Override
    public int getItemCount() {
        return mOrderItems != null ? mOrderItems.size() : 0;
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        private CardItemConfirmationBinding mBinding;

        ItemHolder(CardItemConfirmationBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bindTo(OrderItem orderItem, int position) {
            mBinding.setModel(orderItem);
            mBinding.tvCartItemQuantity.setText(mContext.getResources().getString(R.string.items_quantity, orderItem.getQuantity()));
            mBinding.isvSize.setSize(orderItem.getSize());
            setItemCustomizations(orderItem);
            setContentDescription(mBinding.getRoot().getContext(), orderItem, position);
            mBinding.executePendingBindings();
        }

        private void setItemCustomizations(OrderItem orderItem) {
            BindingAdapters.bindProductCustomization(mBinding.tvCartItemDescription,
                    OrderItemUtil
                            .getCurrentSelectionDescription(mBinding.getRoot().getContext(), orderItem, false));
        }

        private void setContentDescription(Context context, OrderItem orderItem, int position) {
            String customizations = BindingAdapters.buildProductCustomization(mBinding.getRoot().getContext(), OrderItemUtil
                    .getCurrentSelectionDescription(mBinding.getRoot().getContext(), orderItem, false));
            String contentDescription = "";

            if (SizeEnum.ONE_SIZE == orderItem.getSize()) {
                contentDescription = context.getString(
                        R.string.confirmation_item_row_cd,
                        position + 1,
                        orderItem.getQuantity(),
                        orderItem.getMenuData().getName(),
                        customizations);
            } else {
                contentDescription = context.getString(
                        R.string.confirmation_item_row_size_cd,
                        position + 1,
                        orderItem.getQuantity(),
                        orderItem.getSize().toString(),
                        orderItem.getMenuData().getName(),
                        customizations);
            }
            itemView.setContentDescription(contentDescription);
        }
    }
}
