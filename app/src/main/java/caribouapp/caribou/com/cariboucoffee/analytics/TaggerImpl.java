package caribouapp.caribou.com.cariboucoffee.analytics;

import com.urbanairship.UAirship;

import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;

public class TaggerImpl implements Tagger {

    private static final String TAG = TaggerImpl.class.getSimpleName();

    private static final String TAG_GROUP = "mApp";
    private static final String TAG_ORDER_AHEAD_USER = "order_ahead_user";
    private static final String TAG_ORDER_AHEAD_PICKUP = "order_scheduled_pickup";
    private static final String TAG_ORDER_AHEAD_WITH_REWARD = "order_applied_reward";
    private static final String TAG_LOCATION_SEARCHED = "location_search";
    private static final String TAG_REWARD_REDEEMED = "reward_redeemed_in_app";
    private static final String TAG_ADD_FUNDS = "add_funds";
    private static final String TAG_USER_LOGGED_IN = "user_logged_in";
    private static final String TAG_WALKIN_ORDER = "walkin_order";
    private static final String TAG_DRIVETHRU_ORDER = "drivethru_order";
    private static final String TAG_CURBSIDE_ORDER = "curbside_order";
    private static final String TAG_DELIVERY_ORDER = "delivery_order";
    private static final String TAG_ORDER_BULK_ORDER = "bulk_order";

    private void tagUrbanAirship(String tag) {
        try {
            UAirship.shared().getContact().editTagGroups().addTag(TAG_GROUP, tag).apply();
        } catch (RuntimeException ex) {
            Log.e(TAG, new LogErrorException("Error tagging UA " + tag, ex));
        }
    }

    public void tagOrderAheadUser() {
        tagUrbanAirship(TAG_ORDER_AHEAD_USER);
    }

    @Override
    public void tagOrderAheadPickup() {
        tagUrbanAirship(TAG_ORDER_AHEAD_PICKUP);
    }

    @Override
    public void tagOrderAheadWithReward() {
        tagUrbanAirship(TAG_ORDER_AHEAD_WITH_REWARD);
    }

    @Override
    public void tagLocationSearched() {
        tagUrbanAirship(TAG_LOCATION_SEARCHED);
    }

    @Override
    public void tagRewardRedeemed() {
        tagUrbanAirship(TAG_REWARD_REDEEMED);
    }

    @Override
    public void tagAddFunds() {
        tagUrbanAirship(TAG_ADD_FUNDS);
    }

    @Override
    public void tagUserLoggedIn() {
        tagUrbanAirship(TAG_USER_LOGGED_IN);
    }

    public void tagPickUpOrder(YextPickupType pickupType) {
        switch (pickupType) {
            case WalkIn:
                tagUrbanAirship(TAG_WALKIN_ORDER);
                break;
            case DriveThru:
                tagUrbanAirship(TAG_DRIVETHRU_ORDER);
                break;
            case Curbside:
                tagUrbanAirship(TAG_CURBSIDE_ORDER);
                break;
            case Delivery:
                tagUrbanAirship(TAG_DELIVERY_ORDER);
            break;
        }
    }

    public void tagBulkOrder() {
        tagUrbanAirship(TAG_ORDER_BULK_ORDER);
    }
}
