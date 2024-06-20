package caribouapp.caribou.com.cariboucoffee.mvp.menu.view;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutMenuNutritionItemBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.NutritionalRowData;

/**
 * Created by jmsmuy on 10/4/17.
 */

public class MenuNutritionAdapter extends RecyclerView.Adapter<MenuNutritionAdapter.ItemHolder> {

    private List<NutritionalRowData> mNutritionalValuesList;

    public void setList(List<NutritionalRowData> list) {
        mNutritionalValuesList = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public MenuNutritionAdapter.ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutMenuNutritionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuNutritionAdapter.ItemHolder holder, int position) {
        final NutritionalRowData rowData = mNutritionalValuesList.get(position);
        holder.setModel(rowData);
        String nutritionName = rowData.getNutritionalName();
        if (rowData.getNutritionalName().contains("(g)")) {
            nutritionName = nutritionName.replace("(g)", "");
            holder.mBinding.tvItemContainer.setContentDescription(nutritionName + "g" + rowData.getNutritionalValue());
        } else {
            holder.mBinding.tvItemContainer.setContentDescription(
                    holder.mBinding.getRoot().getContext()
                            .getString(R.string.item_cd,
                                    rowData.getNutritionalName(), rowData.getNutritionalValue()));

        }


    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshData() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mNutritionalValuesList != null) {
            return mNutritionalValuesList.size();
        }
        return 0;
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        LayoutMenuNutritionItemBinding mBinding;

        ItemHolder(LayoutMenuNutritionItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void setModel(NutritionalRowData model) {
            mBinding.setModel(model);
            mBinding.executePendingBindings();
        }
    }

}
