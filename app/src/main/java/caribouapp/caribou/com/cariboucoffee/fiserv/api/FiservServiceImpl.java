package caribouapp.caribou.com.cariboucoffee.fiserv.api;

import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.AccountTokenRequest;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.AccountTokenResponse;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import retrofit2.Response;

/**
 * Created by Swapnil on 01/10/22.
 */

public class FiservServiceImpl implements FiservService {

    private static final String TAG = PayGateService.class.getSimpleName();
    private final FiservAPI mFiservAPI;
    private final UserServices mUserServices;
    SettingsServices mSettingsServices;

    public FiservServiceImpl(FiservAPI fiservAPI, UserServices userServices,
                             SettingsServices settingsServices) {
        mFiservAPI = fiservAPI;
        mUserServices = userServices;
        mSettingsServices = settingsServices;
    }

    @Override
    public void acquireAccountToken(ResultCallback<AccountTokenResponse> callback,
                                    AccountTokenRequest accountTokenRequest) {
        mFiservAPI.getFiservAccountToken(accountTokenRequest)
                .enqueue(new BaseRetrofitCallback<AccountTokenResponse>() {
                    @Override
                    protected void onSuccess(Response<AccountTokenResponse> response) {
                        callback.onSuccess(response.body());
                    }

                    @Override
                    protected void onFail(Response<AccountTokenResponse> response) {
                        super.onFail(response);
                        callback.onFail(response.code(), mSettingsServices.getPGCommonErrorMsg());
                    }
                });
    }

}
