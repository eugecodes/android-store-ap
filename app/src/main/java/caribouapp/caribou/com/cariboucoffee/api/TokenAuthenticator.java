package caribouapp.caribou.com.cariboucoffee.api;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.urbanairship.UAirship;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.api.model.oAuth.OauthSignInResponse;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.TokenSignInRequest;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.util.AppUtils;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenAuthenticator implements Interceptor {

    public static final String TAG = TokenAuthenticator.class.getSimpleName();

    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private final UserServices mUserServices;
    private final Context mApplicationContext;
    private final SettingsServices mSettingsServices;
    @Inject
    OAuthAPI mOAuthAPI;

    public TokenAuthenticator(Context context, UserServices userServices,
                              SettingsServices mSettingsServices,
                              OAuthAPI oAuthAPI) {
        mApplicationContext = context;
        mUserServices = userServices;
        this.mSettingsServices = mSettingsServices;
        this.mOAuthAPI = oAuthAPI;
    }

    private void clearCredentialsAndResetToLoginScreen() {
        Log.i(TAG, "Forced logout");
        mUserServices.signOut();
        AppUtils.restartAppAtActivity(mApplicationContext);
    }

    private Request buildAuthorizedRequest(Request request, String credentials) {
        Log.d(TAG, "AuthToken: " + credentials);
        return request.newBuilder()
                .header(
                        AUTHORIZATION_HEADER_KEY,
                        credentials
                )
                .build();
    }

    private String formatCredentials(String token) {
        return StringUtils.format("Bearer %s", token);
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        String token;
        /*
          As we are going to use Anonymous token for
          guest user, hence we have to handle the token passing
          into header as per user type
          */
        boolean isThisGuestFlow = (!mUserServices.isUserLoggedIn() && mUserServices.isGuestUserFlowStarted());
        if (isThisGuestFlow) {
            token = mUserServices.getAnonAuthToken();
        } else {
            token = mUserServices.getAuthToken();
        }

        if (token != null) {
            request = buildAuthorizedRequest(chain.request(), formatCredentials(token));
        }

        Response response = chain.proceed(request);
        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED && (!isThisGuestFlow)) {
            askForLoginAfterAuthFailed();
        } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED && isThisGuestFlow) {
            retrofit2.Response<OauthSignInResponse> anonResponse = requestAnonToken();
            OauthSignInResponse oAuthSignInResponse = anonResponse.body();
            if (anonResponse.isSuccessful() && oAuthSignInResponse != null) {
                saveGuestSignInData(oAuthSignInResponse.getToken(),
                        oAuthSignInResponse.getAud());
                request = buildAuthorizedRequest(request,
                        formatCredentials(oAuthSignInResponse.getToken()));
                response = chain.proceed(request);
            }
        }

        return response;
    }

    /*
     * In this method we added logic for calling anonymous token again
     * if we got 401 on any API in guest flow
     * @params Request, Chain is taken from interceptor
     * Once the anon token refresh we are calling the previous API again
     * and if ANON_TOKEN_NUMBER_OF_ATTEMPTS < 2 we are calling anon token API again
     * */
    private retrofit2.Response<OauthSignInResponse> requestAnonToken() throws IOException {
        retrofit2.Response<OauthSignInResponse> anonResponse = mOAuthAPI.authenticate(
                new TokenSignInRequest(UAirship.shared().getChannel().getId(),
                        true)).execute();
        return anonResponse;
//         else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
//                            if (ANON_TOKEN_NUMBER_OF_ATTEMPTS <= 2) {
//                                ANON_TOKEN_NUMBER_OF_ATTEMPTS++;
//                                requestAnonToken(request, chain);
//                            }
//                        }
    }

    private void askForLoginAfterAuthFailed() {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(SourceApplication.get(mApplicationContext).getCurrentActivity())
                            .setTitle(mSettingsServices.getTokenExpiredMessageTitle())
                            .setCancelable(false)
                            .setMessage(mSettingsServices.getTokenExpiredMessage())
                            .setPositiveButton(R.string.log_in, (dialog, which) -> goToSignIn())
                            .show();
                }
            });
        } catch (Exception e) {
            Log.i(TAG, "Problems while opening dialog" + e.getMessage());
        }
    }

    private void goToSignIn() {
        Log.i(TAG, "Forced logout");
        mUserServices.signOut();
        AppUtils.restartAppSignInActivity(mApplicationContext);
    }

    public void saveGuestSignInData(String token, String uid) {
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(token)) {
            return;
        }
        mUserServices.setAnonAuthToken(token);
        mUserServices.setGuestUid(uid);
    }
}
