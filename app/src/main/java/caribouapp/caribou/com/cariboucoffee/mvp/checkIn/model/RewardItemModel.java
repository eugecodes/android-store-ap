package caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model;

import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by jmsmuy on 12/28/17.
 */

public class RewardItemModel extends RewardModel implements Serializable {

    private Integer mRewardId;
    private BigDecimal mPoints;
    private String mImageUrl;
    private String mName;
    private DateTime mEndingDate;
    private boolean mRedeemed;
    private boolean mBuyable;
    private String mCode;
    private boolean mLimitedTimeReward;
    private boolean mSelectionEnabled = true;
    private boolean mSelectedReward;
    private boolean mOmsMobileEligible;
    private boolean mAutoApply;


    public RewardItemModel() {
    }

    public RewardItemModel(Integer id, DateTime expirationDate, boolean redeemed) {
        mRewardId = id;
        mEndingDate = expirationDate;
        mRedeemed = redeemed;
    }

    public RewardItemModel(boolean limitedTimeOffer, Integer rewardId, String code, BigDecimal points) {
        mLimitedTimeReward = limitedTimeOffer;
        mRewardId = rewardId;
        mCode = code;
        mPoints = points;
    }

    @Bindable
    public BigDecimal getPoints() {
        return mPoints;
    }

    public void setPoints(BigDecimal points) {
        mPoints = points;
        notifyPropertyChanged(BR.points);
    }

    @Bindable
    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
        notifyPropertyChanged(BR.imageUrl);
    }

    @Bindable
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public boolean isBuyable() {
        return mBuyable;
    }

    public void calculateBuyable(BigDecimal userPoints) {
        mBuyable = mPoints.compareTo(userPoints) <= 0;
        notifyPropertyChanged(BR.buyable);
    }

    @Bindable
    public DateTime getEndingDate() {
        return mEndingDate;
    }

    public void setEndingDate(DateTime endingDate) {
        mEndingDate = endingDate;
        notifyPropertyChanged(BR.endingDate);
    }

    @Bindable
    public Integer getRewardId() {
        return mRewardId;
    }

    public void setRewardId(Integer rewardId) {
        mRewardId = rewardId;
    }

    @Bindable
    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    @Bindable
    public boolean isRedeemed() {
        return mRedeemed;
    }

    public void setRedeemed(boolean redeemed) {
        mRedeemed = redeemed;
    }

    @Bindable
    public boolean isLimitedTimeReward() {
        return mLimitedTimeReward;
    }

    public void setLimitedTimeReward(boolean limitedTimeReward) {
        mLimitedTimeReward = limitedTimeReward;
    }

    public boolean isSelectionEnabled() {
        return mSelectionEnabled;
    }

    public void setSelectionEnabled(boolean mobileOrderEligible) {
        mSelectionEnabled = mobileOrderEligible;
    }

    @Bindable
    public boolean isSelectedReward() {
        return mSelectedReward;
    }

    public void setSelectedReward(boolean selectedReward) {
        mSelectedReward = selectedReward;
        notifyPropertyChanged(BR.selectedReward);
    }

    @Bindable
    public boolean isOmsMobileEligible() {
        return mOmsMobileEligible;
    }

    public void setOmsMobileEligible(boolean omsMobileEligible) {
        mOmsMobileEligible = omsMobileEligible;
        notifyPropertyChanged(BR.omsMobileEligible);
    }

    public boolean isAutoApply() {
        return mAutoApply;
    }

    public void setAutoApply(boolean autoApply) {
        mAutoApply = autoApply;
    }
}
