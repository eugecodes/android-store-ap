package caribouapp.caribou.com.cariboucoffee.mvp.menu.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.common.RewardsData;
import caribouapp.caribou.com.cariboucoffee.databinding.FragmentRewardsMenuBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.BaseFragment;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardModel;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.view.CheckInActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.view.RewardsCardAdapter;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.MenuContract;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuRewardsModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.presenter.MenuRewardsFragmentPresenter;
import icepick.Icepick;
import icepick.State;

/**
 * Created by jmsmuy on 2/14/18.
 */
public class MenuRewardsFragment extends BaseFragment<FragmentRewardsMenuBinding> implements MenuContract.Fragments.Rewards.View {

    @State
    MenuRewardsModel mModel;

    private MenuContract.Fragments.Rewards.Presenter mPresenter;
    private RewardsCardAdapter mCardAdapter;
    private RewardsData mRewardsData;
    private OnNoRewardTodayClickListener mOnNoRewardTodayClickListener;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rewards_menu;
    }

    private boolean mFinishedLoadingFragment = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof OnNoRewardTodayClickListener) {
            mOnNoRewardTodayClickListener = (OnNoRewardTodayClickListener) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        if (mModel == null) {
            mModel = new MenuRewardsModel();
        }
        MenuRewardsFragmentPresenter presenter = new MenuRewardsFragmentPresenter(this, mModel);
        SourceApplication.get(requireContext()).getComponent().inject(presenter);
        mPresenter = presenter;
        mCardAdapter = new RewardsCardAdapter(mPresenter);
        getBinding().rvMenuCards.setLayoutManager(new LinearLayoutManager(getContext()));
        getBinding().rvMenuCards.setAdapter(mCardAdapter);

        if (mRewardsData != null) {
            mPresenter.setModel(mRewardsData);
        }
        mFinishedLoadingFragment = true;
        getBinding().btnCheckPointsAndRewards.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CheckInActivity.class));
        });
        mCardAdapter.setNoRewardTodayListener(mOnNoRewardTodayClickListener);

        mPresenter.loadOrder();

        return view;
    }

    public void setData(RewardsData rewardsOrderedList) {
        mRewardsData = rewardsOrderedList;
        // This is a simple way to check if the fragment finished loading
        if (!mFinishedLoadingFragment) {
            return;
        }
        // We update the data if needed
        mPresenter.setModel(rewardsOrderedList);
    }

    @Override
    public void showCards(List<RewardModel> rewardsList) {
        mCardAdapter.setList(rewardsList);
        boolean hasRewards = rewardsList.size() > 0;
        getBinding().btnCheckPointsAndRewards.setVisibility(hasRewards ? View.GONE : View.VISIBLE);
        getBinding().rlNoRewardsAvailable.setVisibility(hasRewards ? View.GONE : View.VISIBLE);
        getBinding().rvMenuCards.setVisibility(hasRewards ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showNewPreselectedRewardSet() {
        LocalBroadcastManager
                .getInstance(getContext())
                .sendBroadcast(
                        new Intent(AppConstants.BROADCAST_INTENT_ACTION_NEW_PRESELECTED_REWARD_SET));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public void goToDashboard() {
        // NO-OP
    }

    public interface OnNoRewardTodayClickListener {
        void noRewardTodayClick();
    }

}
