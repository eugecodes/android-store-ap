package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

/**
 * Created by andressegurola on 12/29/17.
 */

public class PushNotificationSubcribeRequest extends AmsRequest {

    @SerializedName("pushNotificationSubscribe")
    private PushNotificationSubscribe mPushNotificationSubscribe;

    public PushNotificationSubscribe getPushNotificationSubscribe() {
        return mPushNotificationSubscribe;
    }

    public void setPushNotificationSubscribe(PushNotificationSubscribe pushNotificationSubscribe) {
        mPushNotificationSubscribe = pushNotificationSubscribe;
    }
}
