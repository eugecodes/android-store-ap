package caribouapp.caribou.com.cariboucoffee.api;

import androidx.annotation.NonNull;

import java.io.IOException;

import caribouapp.caribou.com.cariboucoffee.BuildConfig;
import caribouapp.caribou.com.cariboucoffee.util.AppUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by gonzalogelos on 11/1/18.
 */

public class AppInfoHeaderInterceptor implements Interceptor {

    private static final String SOURCE_APP_VERSION_HEADER = "sourceAppVersion";

    private static final String SOURCE_APP_BRAND_HEADER = "sourceAppBrand";
    private static final String SOURCE_APP_BRAND_ALT_HEADER = "x-source-app";

    private static final String SOURCE_APP_PLATFORM_HEADER = "sourceAppPlatform";
    private static final String SOURCE_APP_PLATFORM_HEADER_VALUE = "Android";

    @Override
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder()
                .addHeader(SOURCE_APP_VERSION_HEADER, AppUtils.getAppVersionText())
                .addHeader(SOURCE_APP_BRAND_HEADER, BuildConfig.SOURCE_APP)
                .addHeader(SOURCE_APP_BRAND_ALT_HEADER, BuildConfig.SOURCE_APP)
                .addHeader(SOURCE_APP_PLATFORM_HEADER, SOURCE_APP_PLATFORM_HEADER_VALUE)
                .build();
        return chain.proceed(request);
    }
}
