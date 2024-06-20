package caribouapp.caribou.com.cariboucoffee.fiserv.api;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.UUID;

import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Swapnil on 01/10/22.
 */

public class FiservKeyInterceptor implements Interceptor {

    private static final String TAG = FiservKeyInterceptor.class.getSimpleName();

    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String TIMESTAMP_HEADER_KEY = "Timestamp";
    private static final String CLIENT_REQ_ID_HEADER_KEY = "Client-Request-Id";
    private final UserServices mUserServices;


    public FiservKeyInterceptor(UserServices userServices) {
        mUserServices = userServices;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        String token = null;

        boolean isThisGuestFlow = (!mUserServices.isUserLoggedIn() && mUserServices.isGuestUserFlowStarted());
        if (isThisGuestFlow) {
            token = mUserServices.getFiservToken();
        }

        if (token != null) {
            request = buildAuthorizedRequest(chain.request(), formatCredentials(token));
        }

        request = request.newBuilder()
                .header(TIMESTAMP_HEADER_KEY,
                        String.valueOf((System.currentTimeMillis() / 1000L))
                )
                .header(CLIENT_REQ_ID_HEADER_KEY, UUID.randomUUID().toString())
                .build();

        return chain.proceed(request);
    }

    private Request buildAuthorizedRequest(Request request, String credentials) {
        Log.d(TAG, "Fiserv Token: " + credentials);
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
}
