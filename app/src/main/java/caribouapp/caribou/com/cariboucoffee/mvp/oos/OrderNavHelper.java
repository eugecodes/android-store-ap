package caribouapp.caribou.com.cariboucoffee.mvp.oos;

import android.content.Context;
import android.content.Intent;

import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.view.MenuActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.cart.view.CartActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.view.PickupTypeActivity;

public class OrderNavHelper {

    SettingsServices mSettingsServices;

    public OrderNavHelper(SettingsServices settingsServices) {
        mSettingsServices = settingsServices;
    }

    public Intent createIntentStartOrder(
            Context context,
            StoreLocation storeLocation,
            MenuActivity.MenuOrigin origin,
            RewardItemModel rewardItemModel) {

        Intent nextScreenIntent = rewardItemModel == null
                ? MenuActivity.createIntent(context, true, origin)
                : MenuActivity.createIntent(context, true, origin, rewardItemModel);

        if (!requiresPickupData(storeLocation)) {
            return nextScreenIntent;
        }

        return PickupTypeActivity.createIntentNextScreenMenu(context, nextScreenIntent, null, false);
    }

    public Intent createIntentReOrder(Context context, StoreLocation storeLocation) {

        Intent nextScreenIntent = CartActivity.createIntent(context);

        if (!requiresPickupData(storeLocation)) {
            return nextScreenIntent;
        }

        return PickupTypeActivity.createIntentNextScreenMenu(context, nextScreenIntent, null, false);
    }

    public Intent createIntentContinueOrder(Context context, StoreLocation storeLocation) {
        Intent nextScreenIntent = CartActivity.createIntent(context);

        if (!requiresPickupData(storeLocation)) {
            return nextScreenIntent;
        }

        return PickupTypeActivity.createIntentNextScreenMenu(context, nextScreenIntent, null, false);
    }

    public boolean requiresPickupData(StoreLocation storeLocation) {
        return mSettingsServices.isPickupTypeSelectionEnabled()
                && !(storeLocation.getPickupTypes().isEmpty()
                || storeLocation.getPickupTypes().size() == 1
                && storeLocation.getPickupTypes().iterator().next().equals(YextPickupType.WalkIn));
    }

}
