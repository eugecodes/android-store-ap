package caribouapp.caribou.com.cariboucoffee.api;

import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.AddFundsRequest;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.AddFundsResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.ResponseAvailableRewards;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.ResponseClaimedRewards;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.SVmsRequest;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.SVmsRequestClaimReward;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.SVmsResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by jmsmuy on 11/8/17.
 */

public interface SVmsAPI {

    @POST("getBalance")
    @Headers("Cache-Control: no-cache")
    Call<SVmsResponse> getBalanceNoCache(@Body SVmsRequest request);

    @POST("getBalance")
    Call<SVmsResponse> getBalance(@Body SVmsRequest request);

    @POST("addFunds")
    @Headers("TIMEOUT_GROUP: AddFunds")
    Call<AddFundsResponse> addFunds(@Body AddFundsRequest request);

    @GET("{uid}/claimedRewards")
    Call<ResponseClaimedRewards> getClaimedRewards(@Path("uid") String uid);

    @POST("{uid}/claimedRewards")
    @Headers("Accept-Encoding: identity")
        // Fix for empty gzip malformed response https://github.com/square/retrofit/issues/804
    Call<ResponseBody> claimReward(@Path("uid") String uid, @Body SVmsRequestClaimReward body);

    @GET("{uid}/rewards")
    Call<ResponseAvailableRewards> getAvailableRewards(@Path("uid") String uid);
}
