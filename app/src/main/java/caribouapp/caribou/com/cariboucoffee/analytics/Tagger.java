package caribouapp.caribou.com.cariboucoffee.analytics;

import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;

public interface Tagger {

    void tagOrderAheadUser();

    void tagOrderAheadPickup();

    void tagOrderAheadWithReward();

    void tagLocationSearched();

    void tagRewardRedeemed();

    void tagAddFunds();

    void tagUserLoggedIn();

    void tagPickUpOrder(YextPickupType yextPickupType);

    void tagBulkOrder();
}
