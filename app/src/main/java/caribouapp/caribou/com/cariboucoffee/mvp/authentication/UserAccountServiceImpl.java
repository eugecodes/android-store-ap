package caribouapp.caribou.com.cariboucoffee.mvp.authentication;

import caribouapp.caribou.com.cariboucoffee.api.AmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsRequestUserProfileData;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResult;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.RetrofitCallbackWrapper;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import retrofit2.Response;

/**
 * Created by gonzalogelos on 8/27/18.
 */

public class UserAccountServiceImpl implements UserAccountService {

    private static final String TAG = UserAccountService.class.getSimpleName();
    private final AmsApi mAmsApi;
    // TODO: ask to or we make this class preferences no dependable, or we have a
    // constructor that permit pass preferences i think both are a little wrong
    private final UserServices mUserServices;

    public UserAccountServiceImpl(AmsApi amsApi, UserServices userServices) {
        mAmsApi = amsApi;
        mUserServices = userServices;
    }

    @Override
    public void getProfileDataWithCache(ResultCallback<Void> callback) {
        if (mUserServices.isMissingUserData()) {
            Log.w(TAG, "Missing account data. Fetching new data from server.");
            mAmsApi.getProfileData(new AmsRequestUserProfileData(mUserServices.getUid()))
                    .enqueue(new RetrofitCallbackWrapper<AmsResponse, Void>(callback) {
                        @Override
                        protected void onSuccess(Response<AmsResponse> response) {
                            mUserServices.setUser(response.body().getResult());
                            callback.onSuccess(null);

                        }

                        @Override
                        protected void onFail(Response<AmsResponse> response) {
                            callback.onFail(response.code(), response.message());
                        }
                    });
        } else {
            Log.i(TAG, "Account data present.");
            callback.onSuccess(null);
        }

    }

    @Override
    public void getProfileData(ResultCallback<AmsResponse> callback) {
        mAmsApi.getProfileData(new AmsRequestUserProfileData(mUserServices.getUid()))
                .enqueue(new RetrofitCallbackWrapper<AmsResponse, AmsResponse>(callback) {
                    @Override
                    protected void onSuccess(Response<AmsResponse> response) {
                        AmsResult amsResult = response.body().getResult();
                        mUserServices.setUser(amsResult);
                        callback.onSuccess(response.body());

                    }

                    @Override
                    protected void onFail(Response<AmsResponse> response) {
                        callback.onFail(response.code(), response.message());
                    }
                });
    }


}
