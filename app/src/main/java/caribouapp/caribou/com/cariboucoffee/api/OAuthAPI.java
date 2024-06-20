package caribouapp.caribou.com.cariboucoffee.api;

import caribouapp.caribou.com.cariboucoffee.api.model.oAuth.OauthSignInRequest;
import caribouapp.caribou.com.cariboucoffee.api.model.oAuth.OauthSignInResponse;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.TokenSignInRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by andressegurola on 12/12/17.
 */

public interface OAuthAPI {

    @POST("authenticate")
    Call<OauthSignInResponse> authenticate(@Body OauthSignInRequest oAuthSignInRequest);

    @POST("authenticate")
    @Headers({"Content-Type:application/json-patch+json"})
    Call<OauthSignInResponse> authenticateViaGoogle(@Body OauthSignInRequest oAuthSignInRequest);

    /**
     * This API is used for
     * getting anonymous token for guest user
     * */
    @POST("authenticate")
    Call<OauthSignInResponse> authenticate(@Body TokenSignInRequest guestTokenRequest);

}
