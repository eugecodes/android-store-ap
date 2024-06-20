package caribouapp.caribou.com.cariboucoffee.fiserv.api;

import java.io.IOException;

import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.FiservAnonRequest;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.FiservAnonResponse;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.PGErrorResponse;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.SaleRequest;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.SaleResponse;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;
import retrofit2.Response;

/**
 * Created by Swapnil on 01/10/22.
 */

public class PayGateServiceImpl implements PayGateService {

    private static final String TAG = PayGateService.class.getSimpleName();
    private final PayGateAPI mPayGateAPI;
    private final UserServices mUserServices;
    SettingsServices mSettingsServices;

    public PayGateServiceImpl(PayGateAPI payGateAPI, UserServices userServices,
                              SettingsServices settingsServices) {
        mPayGateAPI = payGateAPI;
        mUserServices = userServices;
        mSettingsServices = settingsServices;
    }

    @Override
    public void acquireToken(ResultCallback<FiservAnonResponse> callback, String uniqueId) {
        mPayGateAPI.getPayGateAnonymousToken(new FiservAnonRequest(uniqueId))
                .enqueue(new BaseRetrofitCallback<FiservAnonResponse>() {

                    @Override
                    protected void onSuccess(Response<FiservAnonResponse> response) {
                        if (response.body() != null && (!response.body().getTokenId().isEmpty())) {
                            mUserServices.setFiservToken(response.body().getTokenId());
                        }
                        callback.onSuccess(response.body());
                    }

                    @Override
                    protected void onFail(Response<FiservAnonResponse> response) {
                        super.onFail(response);
                        callback.onFail(response.code(), mSettingsServices.getPGCommonErrorMsg());
                    }

                });
    }

    @Override
    public void startAuthorizeSale(ResultCallback<SaleResponse> callback, SaleRequest saleRequest) {
        mPayGateAPI.makeAuthorizeSale(saleRequest)
                .enqueue(new BaseRetrofitCallback<SaleResponse>() {

                    @Override
                    protected void onSuccess(Response<SaleResponse> response) {
                        callback.onSuccess(response.body());
                    }

                    @Override
                    protected void onFail(Response<SaleResponse> response) {
                        super.onFail(response);
                        try {
                            String errorBodyContent = response.errorBody().string();
                            PGErrorResponse errorResponse = GsonUtil.defaultGson()
                                    .fromJson(errorBodyContent, PGErrorResponse.class);
                            if ((response.code() >= PGErrorResponse.PG_400_ERROR)
                                    && (response.code() <= PGErrorResponse.PG_500_ERROR)) {
                                switch (errorResponse.getmErrorCode()) {
                                    case PGErrorResponse.PAYMENT_DECLINED:
                                        callback.onFail(response.code(), mSettingsServices.getCardDeclinedMsg());
                                        break;
                                    case PGErrorResponse.PAYMENT_REJECTED:
                                        callback.onFail(response.code(), mSettingsServices.getCardValidationFailureMsg());
                                        break;
                                    case PGErrorResponse.PRICE_OVER_LIMIT:
                                        callback.onFail(response.code(), mSettingsServices.getPriceOverLimitMsg());
                                        break;
                                    default:
                                        callback.onFail(response.code(), mSettingsServices.getPGCommonErrorMsg());
                                }
                            } else {
                                callback.onFail(response.code(), mSettingsServices.getPGCommonErrorMsg());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            callback.onFail(response.code(), mSettingsServices.getPGCommonErrorMsg());
                        }
                    }

                    @Override
                    protected void onNetworkFail(IOException throwable) {
                        super.onNetworkFail(throwable);
                        callback.onError(throwable);
                    }
                });
    }
}
