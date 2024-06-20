package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;
import caribouapp.caribou.com.cariboucoffee.databinding.BindingAdapters;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutLocationVerticalListItemBinding;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;

/**
 * Created by andressegurola on 10/5/17.
 */

public abstract class LocationVerticalListAdapter extends RecyclerView.Adapter<LocationVerticalListAdapter.StoreLocationVH> {

    public interface LocationListListener {
        void openLocationDetails(StoreLocation storeLocation);

        void chooseLocation(StoreLocation storeLocation);

        void viewLocationOnMap(StoreLocation storeLocation);
    }

    private List<StoreLocation> mItems = new ArrayList<>();

    private LocationListListener mListener;

    public enum LocationListType {
        TOUCH_TO_OPEN,
        CHOOSE_LOCATION
    }

    private static final String PICK_UP_TYPE_SEPARATOR = ", ";

    private LocationListType mListType;

    public LocationVerticalListAdapter(LocationListType type) {
        mListType = type;
    }


    public void setListener(LocationListListener listener) {
        mListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<StoreLocation> locations) {
        mItems.clear();
        mItems.addAll(locations);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoreLocationVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return
                new StoreLocationVH(
                        LayoutLocationVerticalListItemBinding
                                .inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StoreLocationVH holder, int position) {
        holder.bindTo(mItems.get(position), position, getItemCount());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @StringRes
    protected abstract int getRowContentDescriptionStringRes();

    @SuppressLint("NotifyDataSetChanged")
    public void clearData() {
        mItems.clear();
        notifyDataSetChanged();
    }

    class StoreLocationVH extends RecyclerView.ViewHolder {

        private LayoutLocationVerticalListItemBinding mBinding;

        StoreLocationVH(LayoutLocationVerticalListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.setListener(mListener);
            mBinding.setListType(mListType);
        }

        public void bindTo(StoreLocation storeLocation, int position, int itemCount) {
            mBinding.setModel(storeLocation);
            Context context = mBinding.getRoot().getContext();

            List<YextPickupType> yextPickupTypes = storeLocation.getPickupTypes();
            String pickUpTypesFormatString = storePickUpTypeAvailable(yextPickupTypes, context);
            mBinding.tvPickUpTypes.setText(pickUpTypesFormatString);
            BindingAdapters.ClockWrapper clockWrapper = new BindingAdapters.ClockWrapper();
            SourceApplication.get(context).getComponent().inject(clockWrapper);
            mBinding.getRoot().setContentDescription(
                    context.getString(getRowContentDescriptionStringRes(),
                            BindingAdapters.getStoreContentDescription(storeLocation, context, clockWrapper.getClock())));
            mBinding.btnChooseLocation
                    .setContentDescription(context.getString(R.string.button_cd,
                            context.getString(R.string.choose_location_content_description, storeLocation.getName())));
            mBinding.tvViewOnMap
                    .setContentDescription(context.getString(R.string.button_cd,
                            context.getString(R.string.view_location_details_content_description, storeLocation.getName())));

            if (mListType == LocationListType.CHOOSE_LOCATION) {
                mBinding.mainContainer
                        .setBackgroundColor(
                                context.getResources().getColor(R.color.whiteColor));
                mBinding.btnChooseLocation.setOnClickListener(
                        view -> mListener.chooseLocation(storeLocation));
                mBinding.tvViewOnMap.setOnClickListener(view -> mListener.viewLocationOnMap(storeLocation));
            } else {
                mBinding.mainContainer.setOnClickListener(view -> mListener.openLocationDetails(storeLocation));
            }
            mBinding.executePendingBindings();
        }

        public String storePickUpTypeAvailable(List<YextPickupType> pickupTypes, Context context) {
            List<String> pickupTypeToShow = new ArrayList<>();

            if (pickupTypes.contains(YextPickupType.WalkIn)) {
                pickupTypeToShow.add(context.getString(YextPickupType.WalkIn.getDisplayNameStringId()));
            }
            if (pickupTypes.contains(YextPickupType.DriveThru)) {
                pickupTypeToShow.add(context.getString(YextPickupType.DriveThru.getDisplayNameStringId()));
            }
            if (pickupTypes.contains(YextPickupType.Curbside)) {
                pickupTypeToShow.add(context.getString(YextPickupType.Curbside.getDisplayNameStringId()));
            }
            if (pickupTypeToShow.isEmpty()) {
                return "";
            }
            return StringUtils.joinWith(pickupTypeToShow, PICK_UP_TYPE_SEPARATOR) + " " + context.getString(R.string.available);
        }
    }
}
