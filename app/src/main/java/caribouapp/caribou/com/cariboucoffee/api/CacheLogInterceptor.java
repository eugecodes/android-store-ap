package caribouapp.caribou.com.cariboucoffee.api;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.HttpURLConnection;

import caribouapp.caribou.com.cariboucoffee.util.Log;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by andressegurola on 9/27/17.
 */

public class CacheLogInterceptor implements Interceptor {

    private static final String TAG = CacheLogInterceptor.class.getSimpleName();

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (response.cacheResponse() != null
                && (response.networkResponse() == null || response.networkResponse().code() == HttpURLConnection.HTTP_NOT_MODIFIED)) {
            Log.i(TAG, "Cached response for: " + request.toString());
        }
        return response;
    }
}
