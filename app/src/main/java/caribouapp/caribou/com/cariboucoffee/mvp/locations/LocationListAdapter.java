package caribouapp.caribou.com.cariboucoffee.mvp.locations;

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
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutLocationListItemBinding;

/**
 * Created by andressegurola on 10/5/17.
 */

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.StoreLocationVH> {

    private static final int NUMBER_OF_LINES_TO_BREAK_LINE = 2;

    public interface LocationListListener {
        void openLocationDetails(StoreLocation storeLocation);

        void setSelectedLocation(StoreLocation storeLocation);

        void startNewOrder(StoreLocation storeLocation);
    }

    private List<StoreLocation> mItems = new ArrayList<>();

    private LocationListListener mListener;

    private boolean mOrderAheadEnabled;

    public void setListener(LocationListListener listener) {
        mListener = listener;
    }

    public void setData(List<StoreLocation> storeLocations) {
        mItems.clear();
        mItems.addAll(storeLocations);
        notifyDataSetChanged();
    }

    public void setOrderAheadEnabled(boolean orderAheadEnabled) {
        mOrderAheadEnabled = orderAheadEnabled;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoreLocationVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return
                new StoreLocationVH(
                        LayoutLocationListItemBinding
                                .inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StoreLocationVH holder, int position) {
        holder.bindTo(mItems.get(position));
    }

    public List<StoreLocation> getItems() {
        return mItems;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void clearData() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public int getPosition(StoreLocation store) {
        return mItems.indexOf(store);
    }

    class StoreLocationVH extends RecyclerView.ViewHolder {

        private LayoutLocationListItemBinding mBinding;

        StoreLocationVH(LayoutLocationListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.setListener(mListener);
        }

        public void bindTo(StoreLocation storeLocation) {
            Context context = mBinding.getRoot().getContext();
            mBinding.setModel(storeLocation);
            mBinding.btnStartOrder
                    .setContentDescription(
                            context.getString(
                                    R.string.order_now_location_button_content_description, storeLocation.getName()));
            mBinding.getRoot().post(() -> {
                if (mBinding.btnViewDetailsLocation.getText().equals(mBinding.getRoot().getContext().getString(R.string.view_location_details))
                        && mBinding.btnViewDetailsLocation.getLineCount() == NUMBER_OF_LINES_TO_BREAK_LINE) {
                    mBinding.btnStartOrder.setText(mBinding.getRoot().getContext().getString(R.string.start_new_order_with_break_line));
                }
            });
            mBinding.btnStartOrder.setVisibility(mOrderAheadEnabled && storeLocation.isOrderOutOfStore() ? View.VISIBLE : View.GONE);
            if (getItemCount() == 1) {
                mBinding.cvLocationContainer.getLayoutParams().width = RecyclerView.LayoutParams.MATCH_PARENT;
                mBinding.cvLocationContainer.requestLayout();
            } else {
                BindingAdapters.setWidthPercentageOfScreen(mBinding.cvLocationContainer, 0.9f);
            }
            mBinding.executePendingBindings();
        }
    }
}
