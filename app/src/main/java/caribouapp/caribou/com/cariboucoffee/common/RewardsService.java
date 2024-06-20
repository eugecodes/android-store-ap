package caribouapp.caribou.com.cariboucoffee.common;

public interface RewardsService {

    void loadRewards(boolean loadRedeemable, boolean loadRedeemed,
                     boolean filterByOmsAvailable, ResultCallback<RewardsData> callback);
}
