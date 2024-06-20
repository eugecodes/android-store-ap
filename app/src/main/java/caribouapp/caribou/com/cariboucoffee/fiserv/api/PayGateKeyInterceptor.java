package caribouapp.caribou.com.cariboucoffee.fiserv.api;

import androidx.annotation.NonNull;

import com.newrelic.agent.android.NewRelic;

import java.io.IOException;

import caribouapp.caribou.com.cariboucoffee.BuildConfig;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Swapnil on 01/10/22.
 */

public class PayGateKeyInterceptor implements Interceptor {

    private static final String TAG = PayGateKeyInterceptor.class.getSimpleName();

    private static final String SUBSCRIPTION_KEY_HEADER_NAME = "Ocp-Apim-Subscription-Key";
    private static final String X_SESSION_ID = "X-Session-Id";

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        Log.d(TAG, StringUtils.format("%s: %s", SUBSCRIPTION_KEY_HEADER_NAME, BuildConfig.CMS_AUTH_KEY));

        request = request.newBuilder()
                .header(SUBSCRIPTION_KEY_HEADER_NAME,
                        BuildConfig.CMS_AUTH_KEY
                )
                .header(X_SESSION_ID, NewRelic.currentSessionId())
                .build();

        return chain.proceed(request);
    }
}
