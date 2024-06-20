package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OmsProductWithRewards extends OmsEntityRef {

    @SerializedName("applicable_rewards")
    private List<String> mRewardsApplicableToProduct;

    @SerializedName("available")
    private Boolean mAvailable;

    public OmsProductWithRewards(Long id, String uuid, String name) {
        super(id, uuid, name);
    }

    public List<String> getRewardsApplicableToProduct() {
        return mRewardsApplicableToProduct;
    }

    public void setRewardsApplicableToProduct(List<String> rewardsApplicableToProduct) {
        mRewardsApplicableToProduct = rewardsApplicableToProduct;
    }

    public Boolean isAvailable() {
        return mAvailable;
    }

    public void setAvailable(Boolean available) {
        mAvailable = available;
    }
}
