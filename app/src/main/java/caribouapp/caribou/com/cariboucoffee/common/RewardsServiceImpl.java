package caribouapp.caribou.com.cariboucoffee.common;

import java.util.Iterator;
import java.util.List;

import javax.inject.Singleton;

import caribouapp.caribou.com.cariboucoffee.api.CmsApi;
import caribouapp.caribou.com.cariboucoffee.api.SVmsAPI;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsReward;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsMobileEligibleReward;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.ResponseAvailableRewards;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.ResponseAvailableRewardsData;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.ResponseClaimedRewards;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.ResponseClaimedRewardsData;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.RetrofitCallbackWrapper;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardItemModel;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import retrofit2.Response;

@Singleton
public class RewardsServiceImpl implements RewardsService {

    // "LTO_" but the backend is throwing a "LTO1" result, hence, I'm assuming that it's actually "LTO"
    public static final String COMMA = ",";
    private static final String TAG = RewardsServiceImpl.class.getSimpleName();
    private static final String LTO_PREFIX = "LTO"; // NOTE: This might be wrong, the jira issue says

    private final CmsApi mCmsApi;
    private final SVmsAPI mSVmsAPI;
    private final UserServices mUserServices;
    private final SettingsServices mSettingsServices;
    private final OrderService mOrderService;
    private final Clock mClock;

    private boolean mLoadedStoreValueRewards;
    private boolean mLoadedStoreValueClaimedRewards;
    private boolean mLoadedCMSRewards;
    private boolean mLoadedOMSMobileEligibleRewardsData;
    private boolean mFilterByOmsAvailable;

    private RewardsData mData;

    public RewardsServiceImpl(CmsApi cmsApi, SVmsAPI sVmsAPI, UserServices userServices,
                              SettingsServices settingsServices, OrderService orderService, Clock clock) {
        mCmsApi = cmsApi;
        mSVmsAPI = sVmsAPI;
        mUserServices = userServices;
        mSettingsServices = settingsServices;
        mOrderService = orderService;
        mClock = clock;
    }

    @Override
    public synchronized void loadRewards(boolean loadRedeemable, boolean loadRedeemed,
                                         boolean filterByOmsAvailable, ResultCallback<RewardsData> callback) {
        mData = new RewardsData();
        mLoadedCMSRewards = false;
        mLoadedStoreValueRewards = !loadRedeemable;
        mLoadedStoreValueClaimedRewards = !loadRedeemed;
        mLoadedOMSMobileEligibleRewardsData = false;
        mFilterByOmsAvailable = filterByOmsAvailable;

        mCmsApi.getRewards().enqueue(new RetrofitCallbackWrapper<List<CmsReward>, RewardsData>(callback) {
            @Override
            protected void onSuccess(Response<List<CmsReward>> response) {
                addRewardContent(response.body());
            }

            @Override
            protected void onCallFinished() {
                super.onCallFinished();
                mLoadedCMSRewards = true;
                checkRewardsLoaded(callback);
                loadOrderAheadEligible(callback);
            }
        });

        if (loadRedeemable && (!mUserServices.isGuestUserFlowStarted())) {
            mSVmsAPI.getAvailableRewards(mUserServices.getUid())
                    .enqueue(new RetrofitCallbackWrapper<ResponseAvailableRewards, RewardsData>(callback) {
                        @Override
                        protected void onSuccess(Response<ResponseAvailableRewards> response) {
                            addAvailableRewards(response.body().getData());
                        }

                        @Override
                        protected void onCallFinished() {
                            super.onCallFinished();
                            mLoadedStoreValueRewards = true;
                            checkRewardsLoaded(callback);
                        }
                    });
        } else if (!mUserServices.isUserLoggedIn()) {
            mLoadedStoreValueRewards = true;
            checkRewardsLoaded(callback);
        }

        if (loadRedeemed && (!mUserServices.isGuestUserFlowStarted())) {
            mSVmsAPI.getClaimedRewards(mUserServices.getUid())
                    .enqueue(new RetrofitCallbackWrapper<ResponseClaimedRewards, RewardsData>(callback) {
                        @Override
                        protected void onSuccess(Response<ResponseClaimedRewards> response) {
                            if (response.body().getData() == null) {
                                return;
                            }
                            addClaimedRewards(response.body().getData());
                        }

                        @Override
                        protected void onCallFinished() {
                            super.onCallFinished();
                            mLoadedStoreValueClaimedRewards = true;
                            checkRewardsLoaded(callback);
                        }
                    });
        } else if (!mUserServices.isUserLoggedIn()) {
            mLoadedStoreValueClaimedRewards = true;
            checkRewardsLoaded(callback);
        }
    }

    private void loadOrderAheadEligible(ResultCallback<RewardsData> callback) {
        mOrderService.loadEligibleRewards(mData, new ResultCallback<List<OmsMobileEligibleReward>>() {
            @Override
            public void onSuccess(List<OmsMobileEligibleReward> data) {
                mData.getOmsMobileEligibleRewards().addAll(data);
                onCallFinished();
            }

            @Override
            public void onFail(int errorCode, String errorMessage) {
                Log.e(TAG, new LogErrorException("Error loading order rewards. errorCode: " + errorCode + " message: " + errorMessage));
                onCallFinished();
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, new LogErrorException("Error loading order rewards.", error));
                onCallFinished();
            }

            private void onCallFinished() {
                mLoadedOMSMobileEligibleRewardsData = true;
                checkRewardsLoaded(callback);
            }
        });
    }

    private void checkRewardsLoaded(ResultCallback<RewardsData> callback) {
        if (!mLoadedCMSRewards || !mLoadedStoreValueRewards || !mLoadedStoreValueClaimedRewards || !mLoadedOMSMobileEligibleRewardsData) {
            return;
        }

        filterRewardsAndLoadCMSData();
        callback.onSuccess(mData);
    }

    private void filterRewardsAndLoadCMSData() {
        filterRewardsAndLoadCMSData(mData.getRedeemedRewards());
        filterRewardsAndLoadCMSData(mData.getAvailableRewards());
    }

    private void filterRewardsAndLoadCMSData(List<RewardItemModel> rewardList) {
        if (rewardList == null || rewardList.isEmpty()) {
            return;
        }

        RewardItemModel reward;
        Iterator<RewardItemModel> iterator = rewardList.iterator();

        while (iterator.hasNext()) {
            reward = iterator.next();

            if (mSettingsServices.isOrderAhead() && mSettingsServices.isOrderAheadRewardsEnabled() && mData.getOmsMobileEligibleRewards() != null) {
                // Sets if the reward is OMS eligible and appliable
                rewardIsAvailableOnOms(reward);

                // Filters rewards that are not OMS eligible if such filtering was requested
                if (mFilterByOmsAvailable && !reward.isOmsMobileEligible()) {
                    Log.d(TAG, "Skipping reward because OMS doesn't support it: " + reward.getRewardId());
                    iterator.remove();
                    continue;
                }
            }

            CmsReward cmsReward = mData.getRewardsContent().get(reward.getRewardId());
            if (cmsReward == null) {
                Log.d(TAG, "Skipping rewards from Paytronix for lack of Sitecore content for it: " + reward.getRewardId());
                iterator.remove();
                continue;
            }

            reward.setName(cmsReward.getHeading());
            reward.setImageUrl(cmsReward.getImage());
        }
    }

    public void addRewardContent(List<CmsReward> rewards) {
        for (CmsReward reward : rewards) {
            Integer rewardId = reward.getRewardId();

            if (rewardId == null) {
                Log.e(TAG, new LogErrorException("Sitecore reward with no RewardId for reward: " + reward.getHeading()));
                continue;
            }
            mData.getRewardsContent().put(rewardId, reward);
        }

    }

    private void addAvailableRewards(List<ResponseAvailableRewardsData> data) {
        for (ResponseAvailableRewardsData item : data) {
            if (item.getRewardId() == null) {
                Log.e(TAG, new LogErrorException("Error available reward id not present code:" + item.getCode()));
                continue;
            }
            mData.getAvailableRewards().add(new RewardItemModel(item.getCode().startsWith(LTO_PREFIX), item.getRewardId(),
                    item.getCode(), item.getCriteria().getPoints()));
        }
    }

    private void addClaimedRewards(List<ResponseClaimedRewardsData> data) {
        for (ResponseClaimedRewardsData claimedRewardData : data) {
            for (int i = 0; i < claimedRewardData.getBalance(); i++) {
                mData.getRedeemedRewards().add(new RewardItemModel(claimedRewardData.getId(), claimedRewardData.getExpirationDate(), true));
            }
        }
    }

    private void rewardIsAvailableOnOms(RewardItemModel reward) {
        String rewardId = String.valueOf(reward.getRewardId());
        for (OmsMobileEligibleReward omsReward : mData.getOmsMobileEligibleRewards()) {
            if (rewardId.equals(omsReward.getWalletId())) {
                reward.setOmsMobileEligible(true);
                reward.setAutoApply(omsReward.isAutoApply());
            }
        }
    }

}
