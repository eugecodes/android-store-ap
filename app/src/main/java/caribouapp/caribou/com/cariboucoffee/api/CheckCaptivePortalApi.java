package caribouapp.caribou.com.cariboucoffee.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by gonzalo.gelos on 2/27/18.
 */

public interface CheckCaptivePortalApi {

    @GET("generate_204")
    @Headers("Cache-Control: no-cache")
    Call<ResponseBody> checkInternetConnectivity();
}
