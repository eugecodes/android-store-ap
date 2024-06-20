package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.databinding.LayoutScheduleLocationDetailsBinding;

/**
 * Created by jmsmuy on 10/10/17.
 */

public class LocationDetailsScheduleAdapter extends RecyclerView.Adapter<LocationDetailsScheduleAdapter.Holder> {

    List<LocationScheduleModel> mItems = null;


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutScheduleLocationDetailsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        if (mItems != null && mItems.size() > position) {
            holder.setData(mItems.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    public void loadItems(List<LocationScheduleModel> data) {
        if (mItems != null) {
            mItems.clear();
        } else {
            mItems = new ArrayList<>();
        }
        mItems.addAll(data);
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private final LayoutScheduleLocationDetailsBinding mBinding;

        public Holder(LayoutScheduleLocationDetailsBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void setData(LocationScheduleModel data) {
            mBinding.setModel(data);
            mBinding.executePendingBindings();
        }
    }
}
