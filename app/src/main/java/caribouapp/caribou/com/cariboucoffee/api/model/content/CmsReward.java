package caribouapp.caribou.com.cariboucoffee.api.model.content;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import caribouapp.caribou.com.cariboucoffee.util.AppUtils;

/**
 * Created by asegurola on 6/15/18.
 */

public class CmsReward implements Serializable {

    @SerializedName("title")
    private String mHeading;

    @SerializedName("image")
    private String mImage;

    @SerializedName("rewardId")
    private Integer mRewardId;

    @SerializedName("rewardIdStaging")
    private Integer mRewardIdStaging;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("descriptionRendered")
    private String mDescriptionRendered;

    @SerializedName("priority")
    private String mPriority;

    @SerializedName("imageThumb")
    private String mImageThumb;

    @SerializedName("ruleId")
    private Integer mRuleId;

    public void setRewardId(Integer rewardId) {
        mRewardId = rewardId;
        mRewardIdStaging = rewardId;
    }

    public Integer getRewardId() {
        return AppUtils.isProductionBuild() ? mRewardId : mRewardIdStaging;
    }

    public String getHeading() {
        return mHeading;
    }

    public void setHeading(String heading) {
        mHeading = heading;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescriptionRendered() {
        return mDescriptionRendered;
    }

    public void setDescriptionRendered(String descriptionRendered) {
        mDescriptionRendered = descriptionRendered;
    }

    public String getPriority() {
        return mPriority;
    }

    public void setPriority(String priority) {
        mPriority = priority;
    }

    public String getImageThumb() {
        return mImageThumb;
    }

    public void setImageThumb(String imageThumb) {
        mImageThumb = imageThumb;
    }

    public Integer getRuleId() {
        return mRuleId;
    }

    public void setRuleId(Integer ruleId) {
        mRuleId = ruleId;
    }
}
