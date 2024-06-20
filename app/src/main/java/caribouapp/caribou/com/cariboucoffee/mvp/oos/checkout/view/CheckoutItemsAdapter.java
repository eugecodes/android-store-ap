package caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.BindingAdapters;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutGrandTotalCheckoutItemBinding;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutOrderCheckoutItemBinding;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutOrderCheckoutRewardBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.order.DiscountLine;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.order.OrderItemUtil;


/**
 * Created by gonzalogelos on 4/5/18.
 */

public class CheckoutItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CHECKOUT_ORDER_ITEM_VIEW = 0;
    public static final int CHECKOUT_TOTAL_AND_TAXES_VIEW = 1;
    public static final int CHECKOUT_DISCOUNT_VIEW = 2;

    private static final String ITEM_MODIFIER_SEPARATOR = ",";

    public interface CheckoutItemsAdapterListener {
        void onRemoveReward();
    }

    private List rowList = new ArrayList<>();

    private CheckoutItemsAdapterListener mListener;

    @SuppressLint("NotifyDataSetChanged")
    public void setData(Order order) {
        rowList = new ArrayList(order.getItems());
        rowList.addAll(order.getDiscountLines());
        rowList.add(order);
        notifyDataSetChanged();
    }

    public void setListener(CheckoutItemsAdapterListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case CHECKOUT_ORDER_ITEM_VIEW:
                return new CheckoutItemsAdapter
                        .OrderItemHolder(
                        LayoutOrderCheckoutItemBinding
                                .inflate(LayoutInflater.from(parent.getContext()), parent, false));
            case CHECKOUT_DISCOUNT_VIEW:
                return new CheckoutItemsAdapter
                        .OrderRewardHolder(LayoutOrderCheckoutRewardBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false));
            case CHECKOUT_TOTAL_AND_TAXES_VIEW:
                return new CheckoutItemsAdapter
                        .OrderTotalAndTaxesHolder(
                        LayoutGrandTotalCheckoutItemBinding
                                .inflate(LayoutInflater.from(parent.getContext()), parent, false));
            default:
                throw new IllegalStateException("Unknown viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case CHECKOUT_ORDER_ITEM_VIEW:
                OrderItem orderItem = (OrderItem) rowList.get(position);
                ((CheckoutItemsAdapter.OrderItemHolder) holder).bindTo(orderItem, position);
                break;
            case CHECKOUT_TOTAL_AND_TAXES_VIEW:
                ((CheckoutItemsAdapter.OrderTotalAndTaxesHolder) holder).bindTo((Order) rowList.get(position));
                break;
            case CHECKOUT_DISCOUNT_VIEW:
                ((CheckoutItemsAdapter.OrderRewardHolder) holder).bindTo((DiscountLine) rowList.get(position));
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object row = rowList.get(position);
        if (row instanceof Order) {
            return CHECKOUT_TOTAL_AND_TAXES_VIEW;
        } else if (row instanceof DiscountLine) {
            return CHECKOUT_DISCOUNT_VIEW;
        } else {
            return CHECKOUT_ORDER_ITEM_VIEW;
        }
    }

    @Override
    public int getItemCount() {
        return rowList.size();
    }

    class OrderItemHolder extends RecyclerView.ViewHolder {

        private LayoutOrderCheckoutItemBinding mBinding;

        OrderItemHolder(LayoutOrderCheckoutItemBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
        }

        private void bindTo(OrderItem orderItem, int position) {
            Context context = itemView.getContext();
            mBinding.setModel(orderItem);
            setModifierDescription(orderItem);
            mBinding.tvOrderItemName.setText(orderItem.getMenuData().getName());
            if (orderItem.getQuantity() > 1) {
                mBinding.tvOrderItemName
                        .setText(context.getString(R.string.items_quantity_with_name,
                                orderItem.getQuantity(), orderItem.getMenuData().getName()));
            }
            setContentDescription(context, orderItem, position);
        }

        private void setModifierDescription(OrderItem orderItem) {
            BindingAdapters.bindProductCustomization(mBinding.tvModifiers,
                    OrderItemUtil
                            .getCurrentSelectionDescription(mBinding.getRoot().getContext(), orderItem, false));
        }

        private void setContentDescription(Context context, OrderItem orderItem, int position) {
            String customizations = BindingAdapters.buildProductCustomization(mBinding.getRoot().getContext(), OrderItemUtil
                    .getCurrentSelectionDescription(mBinding.getRoot().getContext(), orderItem, false));
            String contentDescription = "";

            if (SizeEnum.ONE_SIZE == orderItem.getSize()) {
                contentDescription = context.getString(
                        R.string.checkout_item_row_cd,
                        position + 1,
                        orderItem.getQuantity(),
                        orderItem.getMenuData().getName(),
                        customizations);
            } else {
                contentDescription = context.getString(
                        R.string.checkout_item_row_size_cd,
                        position + 1,
                        orderItem.getQuantity(),
                        orderItem.getSize().toString(),
                        orderItem.getMenuData().getName(),
                        customizations);
            }
            itemView.setContentDescription(contentDescription);
        }
    }

    class OrderTotalAndTaxesHolder extends RecyclerView.ViewHolder {

        private LayoutGrandTotalCheckoutItemBinding mBinding;

        OrderTotalAndTaxesHolder(LayoutGrandTotalCheckoutItemBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
        }

        private void bindTo(Order orderConfirmationModel) {
            mBinding.setModel(orderConfirmationModel);
        }
    }

    class OrderRewardHolder extends RecyclerView.ViewHolder {

        private LayoutOrderCheckoutRewardBinding mBinding;

        OrderRewardHolder(LayoutOrderCheckoutRewardBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;

        }

        private void bindTo(DiscountLine appliedReward) {
            if (appliedReward.isAutoApply()) {
                mBinding.btnRemoveReward.setOnClickListener(null);
                mBinding.btnRemoveReward.setVisibility(View.GONE);
            } else {
                mBinding.btnRemoveReward.setVisibility(View.VISIBLE);
                mBinding.btnRemoveReward.setOnClickListener(v -> mListener.onRemoveReward());
            }
            mBinding.setModel(appliedReward);
        }
    }
}
