package caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter;

import android.text.TextUtils;

import com.newrelic.agent.android.NewRelic;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderWrappedData;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.ResultCallbackErrorMapper;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.MoveLoyaltyRequest;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapper;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserAccountService;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.SaveSignInDataContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.view.OrderCheckoutActivity;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.push.PushManager;

public abstract class SaveSignInDataPresenter<T extends SaveSignInDataContract.View> extends BasePresenter<T>
        implements SaveSignInDataContract.Presenter {

    @Inject
    UserAccountService mUserAccountService;

    @Inject
    UserServices mUserServices;

    @Inject
    PushManager mPushManager;

    @Inject
    ErrorMessageMapper mErrorMessageMapper;

    @Inject
    OrderService mOrderService;

    @Inject
    SettingsServices mSettingsServices;

    public SaveSignInDataPresenter(T view) {
        super(view);
    }

    public UserServices getUserServices() {
        return mUserServices;
    }

    public void setUserServices(UserServices userServices) {
        mUserServices = userServices;
    }

    public void setUserAccountService(UserAccountService userAccountService) {
        mUserAccountService = userAccountService;
    }

    public void setPushManager(PushManager pushManager) {
        mPushManager = pushManager;
    }

    public void setErrorMessageMapper(ErrorMessageMapper errorMessageMapper) {
        mErrorMessageMapper = errorMessageMapper;
    }

    public OrderService getOrderService() {
        return mOrderService;
    }

    public void setOrderService(OrderService orderService) {
        this.mOrderService = orderService;
    }

    public void saveSignInData(String token, String uid) {

        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(token)) {
            throw new IllegalStateException("Empty token or uid");
        }

        mUserServices.setAuthToken(token);
        mUserServices.setUid(uid);
        mPushManager.registerForUrbanAirshipPush();

        if (OrderCheckoutActivity.isCallGuestToLoyaltyAPI()) {
            OrderCheckoutActivity.setCallGuestToLoyaltyAPI(false);
            OrderCheckoutActivity.setDisplayThankYouPopUp(true);
            mOrderService.moveOrderToLoyaltyAccount(OrderCheckoutActivity.getNcrOrderId(),
                    new MoveLoyaltyRequest(mSettingsServices.getDeviceId()),
                    new ResultCallback<NcrOrderWrappedData>() {
                        @Override
                        public void onSuccess(NcrOrderWrappedData data) {
                            loadUserProfile();
                        }

                        @Override
                        public void onFail(int errorCode, String errorMessage) {
                            getView().showWarning(R.string.unknown_error);
                        }

                        @Override
                        public void onError(Throwable error) {
                            getView().showWarning(R.string.unknown_error);
                        }
                    });
        } else {
            loadUserProfile();
        }

    }

    public void loadUserProfile() {
        mUserAccountService.getProfileData(new ResultCallbackErrorMapper<AmsResponse>(getView()) {

            @Override
            protected void onSuccessViewUpdates(AmsResponse data) {
                String userUid = mUserServices.getUid();
                if (NewRelic.isStarted() && userUid != null) {
                    // Sets userId for all new relic data
                    NewRelic.setUserId(userUid);
                }

                getView().goToDashboard();
            }

            @Override
            protected ErrorMessageMapper buildErrorMessageMapper() {
                return mErrorMessageMapper;
            }
        });
    }
}
