package caribouapp.caribou.com.cariboucoffee.push;

import android.os.Build;

import com.google.firebase.installations.FirebaseInstallations;
import com.urbanairship.UAirship;

import java.util.Locale;

import javax.security.auth.login.LoginException;

import caribouapp.caribou.com.cariboucoffee.api.AmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsAssociateUA;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.account.PushNotificationSubcribeRequest;
import caribouapp.caribou.com.cariboucoffee.api.model.account.PushNotificationSubscribe;
import caribouapp.caribou.com.cariboucoffee.common.BrandEnum;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapper;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.push.UAirshipServices;
import caribouapp.caribou.com.cariboucoffee.util.AppUtils;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import retrofit2.Response;

/**
 * Created by andressegurola on 12/29/17.
 */

public class PushManager {

    private static final String TAG = PushManager.class.getSimpleName();

    private static final String DEVICE_TYPE_ANDROID = "Android";

    private final AmsApi mAmsApi;

    private final UserServices mUserServices;

    private final UAirshipServices mUAirshipServices;

    private final ErrorMessageMapper mErrorMessageMapper;

    public PushManager(AmsApi amsApi, UserServices userServices, UAirshipServices airshipServices, ErrorMessageMapper errorMessageMapper) {
        mAmsApi = amsApi;
        mUserServices = userServices;
        mUAirshipServices = airshipServices;
        mErrorMessageMapper = errorMessageMapper;
    }

    public void registerForUrbanAirshipPush() {
        try {
            mUAirshipServices.enableNotifications();
            mAmsApi.associateWithUrbanAirship(mUserServices.getUid(), buildAssociateUARequest()).enqueue(new BaseRetrofitCallback<AmsResponse>() {
                @Override
                protected void onSuccess(Response<AmsResponse> response) {
                    UAirship.shared().getContact().identify(mUserServices.getUid());
                    Log.i(TAG, "Urban Airship user association finished.");
                }
            });

        } catch (RuntimeException e) {
            Log.e(TAG, new LogErrorException("Error associating user for Urban Airship"));
        }

    }

    public void registerForPaytronixPush() {
        if (AppUtils.getBrand() != BrandEnum.CBOU_BRAND || mUserServices.getCaribouCardNumber() == null) {
            return;
        }

        FirebaseInstallations.getInstance().getId().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        registerForPaytronixPushInternal(task.getResult());
                    } else {
                        Log.e(TAG, new LogErrorException("Firebase id task failed", task.getException()));
                    }
                }
        );
    }

    private void registerForPaytronixPushInternal(String deviceToken) {
        try {
            PushNotificationSubcribeRequest request = new PushNotificationSubcribeRequest();
            PushNotificationSubscribe subscribe = new PushNotificationSubscribe();
            subscribe.setPrintedCardNumber(mUserServices.getCaribouCardNumber());
            subscribe.setDeviceToken(deviceToken);
            subscribe.setDeviceSystemVersion(android.os.Build.VERSION.RELEASE);
            subscribe.setDeviceSystemName(DEVICE_TYPE_ANDROID);
            subscribe.setDeviceModel(getDeviceName());
            request.setPushNotificationSubscribe(subscribe);
            mAmsApi.pushNotificationSubscribe(request).enqueue(new BaseRetrofitCallback<AmsResponse>() {

                @Override
                protected void onSuccess(Response<AmsResponse> response) {
                    if (mErrorMessageMapper.isSuccessful(response)) {
                        Log.i(TAG, "Push DeviceToken registered");
                    } else {
                        Log.e(TAG, new LogErrorException(mErrorMessageMapper.mapErrorMessage(response.body())));
                    }
                }

                @Override
                protected void onFail(Response<AmsResponse> response) {
                    Log.e(TAG, new LogErrorException("Fail to subscribe for push notification: " + response.message()));
                }

                @Override
                protected void onError(Throwable throwable) {
                    Log.e(TAG, new LogErrorException("Fail to subscribe for push notification", throwable));
                }
            });
        } catch (RuntimeException e) {
            Log.e(TAG, new LogErrorException("Error registering for Paytronix push notifications", e));
        }
    }

    private String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return StringUtils.format("%s %s", manufacturer, model);
        }
    }

    public void unregisterForPush() {
        try {
            mAmsApi.disassociateWithUrbanAirship(mUserServices.getUid(), buildAssociateUARequest()).enqueue(new BaseRetrofitCallback<AmsResponse>() {
                @Override
                protected void onSuccess(Response<AmsResponse> response) {
                    Log.i(TAG, "Urban Airship user dissociation finished.");
                }

                @Override
                protected void onFail(Response<AmsResponse> response) {
                    Log.e(TAG, new LogErrorException("Urban Airship user dissociation failed."));
                }

                @Override
                protected void onError(Throwable throwable) {
                    Log.e(TAG, new LogErrorException("Urban Airship user dissociation onError."));
                }
            });
            UAirship.shared().getContact().reset();
        } catch (Exception e) {
            Log.e(TAG, new LoginException("Error disassociating user for Urban Airship"));
        }
    }

    private AmsAssociateUA buildAssociateUARequest() {
        AmsAssociateUA amsDisassociateUA = new AmsAssociateUA();
        amsDisassociateUA.setDeviceType(DEVICE_TYPE_ANDROID.toLowerCase(Locale.US));
        amsDisassociateUA.setChannelId(UAirship.shared().getChannel().getId());
        return amsDisassociateUA;
    }
}
