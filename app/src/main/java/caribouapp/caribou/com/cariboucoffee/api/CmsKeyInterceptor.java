package caribouapp.caribou.com.cariboucoffee.api;

import androidx.annotation.NonNull;

import java.io.IOException;

import caribouapp.caribou.com.cariboucoffee.BuildConfig;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by andressegurola on 9/27/17.
 */

public class CmsKeyInterceptor implements Interceptor {

    private static final String TAG = CmsKeyInterceptor.class.getSimpleName();

    private static final String SUBSCRIPTION_KEY_HEADER_NAME = "Ocp-Apim-Subscription-Key";

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        Log.d(TAG, StringUtils.format("%s: %s", SUBSCRIPTION_KEY_HEADER_NAME, BuildConfig.CMS_AUTH_KEY));

        request = request.newBuilder()
                .header(SUBSCRIPTION_KEY_HEADER_NAME,
                        BuildConfig.CMS_AUTH_KEY
                )
                .build();

        return chain.proceed(request);
    }
}
