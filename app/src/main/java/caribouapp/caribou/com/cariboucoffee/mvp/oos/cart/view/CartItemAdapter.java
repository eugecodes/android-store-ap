package caribouapp.caribou.com.cariboucoffee.mvp.oos.cart.view;

import static android.view.View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.BindingAdapters;
import caribouapp.caribou.com.cariboucoffee.databinding.CardCartItemBinding;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutItemExtrasBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ModifierGroup;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.QuantitySelectorModel;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.view.QuantitySelectorView;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.order.OrderItemUtil;
import caribouapp.caribou.com.cariboucoffee.util.Log;

/**
 * Created by gonzalogelos on 3/26/18.
 */

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ItemHolder> {

    private static final String TAG = CartItemAdapter.class.getSimpleName();
    private Context mContext;
    private List<OrderItem> mItems;
    private CartItemAdapterListener mListener;

    public CartItemAdapter(Context context) {
        mContext = context;
        mItems = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<OrderItem> items) {
        mItems = new ArrayList<>(items);
        notifyDataSetChanged();
    }

    public void updateItem(OrderItem orderItem) {
        int position = mItems.indexOf(orderItem);
        mItems.remove(orderItem);
        mItems.add(position, orderItem);
        notifyItemChanged(position);
    }

    public void removeItem(OrderItem orderItem) {
        int position = mItems.indexOf(orderItem);
        mItems.remove(orderItem);
        notifyItemRemoved(position);
    }

    public void setListener(CartItemAdapterListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public CartItemAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(CardCartItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemAdapter.ItemHolder holder, int position) {
        OrderItem orderItem = mItems.get(position);
        holder.bindTo(orderItem, position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private void showDeleteItemFromCartDialog(OrderItem orderItem) {
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.delete_cart_item_title)
                .setMessage(mContext.getString(R.string.delete_cart_item_message))
                .setNegativeButton(R.string.not_redeem_offer, (dialog, which) -> {

                })
                .setPositiveButton(R.string.yes, (dialog, which) -> mListener.removeItem(orderItem))
                .show();
    }


    public interface CartItemAdapterListener {
        void removeItem(OrderItem orderItem);

        void changeQuantity(OrderItem orderItem, int newQuantity);

        void editItem(OrderItem orderItem);
    }

    class ItemHolder extends RecyclerView.ViewHolder implements QuantitySelectorView.QuantitySelectorListener {

        private CardCartItemBinding mBinding;

        ItemHolder(CardCartItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bindTo(OrderItem orderItem, int position) {
            mBinding.setModel(orderItem);

            setAdditionalCharges(orderItem, position);

            mBinding.ivDeleteItem.setOnClickListener(view -> showDeleteItemFromCartDialog(orderItem));

            mBinding.tvEditItem.setOnClickListener(view -> mListener.editItem(orderItem));

            mBinding.itemQuantityView.setQuantityListener(this);
            QuantitySelectorModel modelForQuantityView = new QuantitySelectorModel();
            modelForQuantityView.setMin(1);
            modelForQuantityView.setQuantity(orderItem.getQuantityLessThanMax());
            modelForQuantityView.setMax(orderItem.getMaxQuantity());
            mBinding.itemQuantityView.setModel(modelForQuantityView);

            setContentDescription(mBinding.getRoot().getContext(), orderItem, position);
            mBinding.executePendingBindings();
        }

        private void setContentDescription(Context context, OrderItem orderItem, int position) {
            String customizations = BindingAdapters.buildProductCustomization(mBinding.getRoot().getContext(), OrderItemUtil
                    .getCurrentSelectionDescription(mBinding.getRoot().getContext(), orderItem, false));
            String contentDescription = "";

            if (SizeEnum.ONE_SIZE == orderItem.getSize()) {
                contentDescription = context.getString(
                        R.string.cart_item_row_cd,
                        position + 1,
                        orderItem.getQuantity(),
                        orderItem.getMenuData().getName(),
                        customizations);
            } else {
                contentDescription = context.getString(
                        R.string.cart_item_row_size_cd,
                        position + 1,
                        orderItem.getQuantity(),
                        orderItem.getSize().toString(),
                        orderItem.getMenuData().getName(),
                        customizations);
            }
            itemView.setContentDescription(contentDescription);
        }

        private void setAdditionalCharges(OrderItem orderItem, int position) {
            mBinding.llExtrasContainer.removeAllViews();
            addAdditionalChargesView(orderItem, position);
            mBinding.llExtrasContainer.setVisibility(mBinding.llExtrasContainer.getChildCount() == 0 ? View.GONE : View.VISIBLE);
        }

        private void addAdditionalChargesView(OrderItem orderItem, int position) {
            String customizations = "";
            for (ModifierGroup modifierGroup : (List<ModifierGroup>) orderItem.getModifierGroups()) {
                LayoutItemExtrasBinding extraLayout = LayoutItemExtrasBinding.inflate(LayoutInflater.from(mContext), null);
                String customization = OrderItemUtil
                        .getCurrentSelectionDescriptionForModifier(mContext, modifierGroup,
                                orderItem.calculateDefaultPlusCustomizations(modifierGroup), false);
                extraLayout.tvExtraDescription.setText(customization == null ? mContext.getString(R.string.customization_none) : customization);
                BigDecimal additionalCharges = orderItem.calculateAdditionalCharges(modifierGroup);
                if (additionalCharges == null || additionalCharges.compareTo(BigDecimal.ZERO) == 0) {
                    extraLayout.tvExtraPrice.setText(null);
                } else {
                    BindingAdapters.setMoneyAmount(extraLayout.tvExtraPrice, additionalCharges, null, 0);
                }
                customizations += ", " + customization;
                Log.d(TAG, "Cart additional charges: " + additionalCharges);
                mBinding.llExtrasContainer.addView(extraLayout.llExtras);
            }

            mBinding.llExtrasContainer.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
            mBinding.llExtrasContainer.setVisibility(mBinding.llExtrasContainer.getChildCount() == 0 ? View.GONE : View.VISIBLE);
            String contentDescription;
            if (SizeEnum.ONE_SIZE == orderItem.getSize()) {
                contentDescription = mContext.getString(
                        R.string.cart_item_row_cd,
                        position + 1,
                        orderItem.getQuantity(),
                        orderItem.getMenuData().getName(),
                        customizations);
            } else {
                contentDescription = mContext.getString(
                        R.string.cart_item_row_size_cd,
                        position + 1,
                        orderItem.getQuantity(),
                        orderItem.getSize().toString(),
                        orderItem.getMenuData().getName(),
                        customizations);
            }
            Log.d(TAG, "contentDescription: " + contentDescription);
            mBinding.rlCartItemContainer.setContentDescription(contentDescription);
        }

        @Override
        public void quantityChanged(int newValue) {
            mListener.changeQuantity(mBinding.getModel(), newValue);
        }
    }
}
