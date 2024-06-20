package caribouapp.caribou.com.cariboucoffee.mvp.checkIn.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.databinding.CardRewardBinding;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutGenericListBottomButtonBinding;
import caribouapp.caribou.com.cariboucoffee.databinding.LayoutSectionHeaderBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.RewardsPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardBottomButtonModel;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardHeaderModel;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardModel;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.presenter.CheckInPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.view.MenuRewardsFragment;

/**
 * Created by jmsmuy on 10/4/17.
 */

public class RewardsCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEADER_TYPE = 1;
    private static final int ITEM_TYPE = 2;
    private static final int BOTTOM_BUTTON_TYPE = 3;
    private final RewardsPresenter mPresenter;
    private List<RewardModel> mList;
    private MenuRewardsFragment.OnNoRewardTodayClickListener mOnNoRewardTodayClickListener;

    public RewardsCardAdapter(RewardsPresenter presenter) {
        mPresenter = presenter;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<RewardModel> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void setNoRewardTodayListener(MenuRewardsFragment.OnNoRewardTodayClickListener listener) {
        mOnNoRewardTodayClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == CheckInPresenter.HEADER) {
            return new HeaderHolder(LayoutSectionHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == CheckInPresenter.ITEM) {
            return new ItemHolder(CardRewardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == BOTTOM_BUTTON_TYPE) {
            return new BottomButtonHolder(LayoutGenericListBottomButtonBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            throw new IllegalStateException("Unknown viewType");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).setHeaderModel((RewardHeaderModel) mList.get(position));
        } else if (holder instanceof ItemHolder) {
            ((ItemHolder) holder).setItemModel((RewardItemModel) mList.get(position));
        } else if (holder instanceof BottomButtonHolder) {
            ((BottomButtonHolder) holder).setBottomButtonModel((RewardBottomButtonModel) mList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mList.get(position) instanceof RewardHeaderModel) {
            return HEADER_TYPE;
        } else if (mList.get(position) instanceof RewardItemModel) {
            return ITEM_TYPE;
        } else if (mList.get(position) instanceof RewardBottomButtonModel) {
            return BOTTOM_BUTTON_TYPE;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    private class HeaderHolder extends RecyclerView.ViewHolder {

        private LayoutSectionHeaderBinding mBindingHeader;

        HeaderHolder(LayoutSectionHeaderBinding binding) {
            super(binding.getRoot());
            mBindingHeader = binding;
        }

        void setHeaderModel(RewardHeaderModel headerModel) {
            Context context = mBindingHeader.getRoot().getContext();
            mBindingHeader.setSectionTitle(context.getString(headerModel.getStringResource()));
            mBindingHeader.executePendingBindings();
        }

    }

    private class BottomButtonHolder extends RecyclerView.ViewHolder {

        private LayoutGenericListBottomButtonBinding mBindingBottomButton;

        BottomButtonHolder(LayoutGenericListBottomButtonBinding binding) {
            super(binding.getRoot());
            mBindingBottomButton = binding;
        }

        void setBottomButtonModel(RewardBottomButtonModel headerModel) {
            mBindingBottomButton.btnBottomList.setText(headerModel.getStringResource());
            mBindingBottomButton.btnBottomList.setOnClickListener(
                    v -> mOnNoRewardTodayClickListener.noRewardTodayClick());
        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        private CardRewardBinding mBindingItem;

        ItemHolder(CardRewardBinding binding) {
            super(binding.getRoot());
            mBindingItem = binding;
        }

        void setItemModel(RewardItemModel rewardItemModel) {
            mBindingItem.setModel(rewardItemModel);
            mBindingItem.getRoot().setTag(rewardItemModel);
            mBindingItem.getRoot().setOnClickListener(v -> mPresenter.rewardClicked(rewardItemModel));
            mBindingItem.btnRedeemNow.setOnClickListener(v -> mPresenter.rewardClicked(rewardItemModel));
            mBindingItem.btnAddToOrder.setOnClickListener(v -> mPresenter.rewardClicked(rewardItemModel));
            mBindingItem.executePendingBindings();
        }
    }
}
