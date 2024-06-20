package caribouapp.caribou.com.cariboucoffee.mvp.webflow.presenter;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.api.AmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.oAuth.OauthSignInResponse;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.webflow.SourceWebContract;
import retrofit2.Response;

/**
 * Created by andressegurola on 11/28/17.
 */

public class SourceWebPresenter extends BasePresenter<SourceWebContract.View> implements SourceWebContract.Presenter {

    private static final int TOKEN_VALID_PERIOD_IN_MINUTES = 20;

    @Inject
    UserServices mUserServices;

    @Inject
    AmsApi mAmsApi;

    public SourceWebPresenter(SourceWebContract.View view) {
        super(view);
    }

    @Override
    public void loadUrl(boolean sendAuthToken, String webUrl, String redirectUrl) {
        if (!sendAuthToken) {
            getView().updateUrl(null, webUrl, redirectUrl);
            return;
        }

        mAmsApi.refreshToken(mUserServices.getUid(), TOKEN_VALID_PERIOD_IN_MINUTES)
                .enqueue(new BaseViewRetrofitCallback<OauthSignInResponse>(getView()) {
                    @Override
                    protected void onSuccessViewUpdates(Response<OauthSignInResponse> response) {
                        getView().updateUrl(response.body().getToken(), webUrl, redirectUrl);
                    }
                });
    }
}
