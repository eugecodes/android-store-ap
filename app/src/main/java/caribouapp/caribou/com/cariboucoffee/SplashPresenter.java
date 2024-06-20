package caribouapp.caribou.com.cariboucoffee;

import com.newrelic.agent.android.NewRelic;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.analytics.OrderDropOffListenerImpl;
import caribouapp.caribou.com.cariboucoffee.api.AmsApi;
import caribouapp.caribou.com.cariboucoffee.api.CmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.oAuth.OauthSignInResponse;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserAccountService;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import retrofit2.Response;

/**
 * Created by jmsmuy on 11/7/17.
 */

public class SplashPresenter extends BasePresenter<SplashContract.View> implements SplashContract.Presenter {

    private static final String TAG = SplashPresenter.class.getSimpleName();


    private static boolean sAppInitialized;

    @Inject
    UserServices mUserServices;

    @Inject
    SettingsServices mSettingsServices;

    @Inject
    AppDataStorage mStorage;

    @Inject
    CmsApi mCmsApi;

    @Inject
    AmsApi mAmsApi;

    @Inject
    UserAccountService mUserAccountService;

    @Inject
    Clock mClock;

    @Inject
    OrderService mOrderService;

    @Inject
    AppDataStorage mAppDataStorage;

    @Inject
    EventLogger mEventLogger;

    private boolean mDashboardLoaded;

    public SplashPresenter(SplashContract.View view) {
        super(view);
    }

    @Override
    public void doStartupChecks() {

        if (sAppInitialized) {
            afterStartup();
            return;
        }

        String userUid = mUserServices.getUid();
        if (NewRelic.isStarted() && userUid != null) {
            // Sets userId for all new relic data
            NewRelic.setUserId(userUid);
        }

        // Set up discarded order listener
        mOrderService.setOrderDropOffListener(new OrderDropOffListenerImpl(mAppDataStorage, mEventLogger));


        checkUserLoggedIn();

    }

    private void checkUserLoggedIn() {
        // We check whether we already have a user logged in to avoid sign in screen
        if (mUserServices.isUserLoggedIn()) {
            Log.d(TAG, "User UID: " + mUserServices.getUid());
            refreshToken();
            return;
        }

        Log.d(TAG, "User is not logged in");
        afterStartup();
    }

    private void fetchAccountData() {
        mUserAccountService.getProfileDataWithCache(new BaseViewResultCallback<Void>(getView()) {

            @Override
            protected void onSuccessViewUpdates(Void data) {
                afterStartup();
            }

            @Override
            protected void onFailView(int errorCode, String errorMessage) {
                onSuccessViewUpdates(null);
            }
        });
    }

    private void refreshToken() {
        mAmsApi.refreshToken(mUserServices.getUid(), null).enqueue(
                new BaseViewRetrofitCallback<OauthSignInResponse>(getView()) {
                    @Override
                    protected void onSuccessBeforeViewUpdates(Response<OauthSignInResponse> response) {
                        mUserServices.setAuthToken(response.body().getToken());

                    }

                    @Override
                    protected void onSuccessViewUpdates(Response<OauthSignInResponse> response) {
                        fetchAccountData();
                    }

                    @Override
                    protected void onFail(Response<OauthSignInResponse> response) {
                        super.onFail(response);
                        mUserServices.signOut();
                        if (getView() == null) {
                            return;
                        }
                        afterStartup();
                    }
                });
    }

    private void afterStartup() {
        sAppInitialized = true;
        if (!mDashboardLoaded) {
            mDashboardLoaded = true;
            getView().startupFinished();
        }
    }
}
