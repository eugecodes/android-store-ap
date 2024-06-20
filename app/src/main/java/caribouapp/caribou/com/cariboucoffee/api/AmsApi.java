package caribouapp.caribou.com.cariboucoffee.api;

import caribouapp.caribou.com.cariboucoffee.api.model.ResponseWithHeader;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsAssociateUA;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsBillingResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsRequestCreateUser;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsRequestDeleteAccount;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsRequestFeedback;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsRequestPreEnrollment;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsRequestResetPwd;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsRequestUpdatePassword;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsRequestUpdateUser;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsRequestUserProfileData;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponseCreateUser;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponsePreEnrollment;
import caribouapp.caribou.com.cariboucoffee.api.model.account.PushNotificationSubcribeRequest;
import caribouapp.caribou.com.cariboucoffee.api.model.account.UpdateBillingRequest;
import caribouapp.caribou.com.cariboucoffee.api.model.account.UpdateBillingResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.oAuth.OauthSignInResponse;
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.TransactionHistory;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by jmsmuy on 11/2/17.
 */

public interface AmsApi {

    @POST("get")
    Call<AmsResponse> getProfileData(@Body AmsRequestUserProfileData request);

    @POST("password/reset")
    Call<AmsResponse> sendResetPasswordMail(@Body AmsRequestResetPwd request);

    @POST("password/resetUpdate")
    Call<AmsResponse> resetPassword(@Body AmsRequestResetPwd request);

    @DELETE("{uid}/billing")
    Call<AmsBillingResponse> removeCard(@Path("uid") String uid);

    // TODO: Remove mSourceApp param when service stops using it
    @GET("billing/get")
    Call<AmsResponse> getBillingData(@Query("sourceApp") String sourceApp, @Query("uid") String uid);

    @POST("billing/update")
    Call<UpdateBillingResponse> updateBilling(@Body UpdateBillingRequest updateBillingRequest);

    @GET("refreshToken")
    @Headers("Cache-Control: no-cache")
    Call<OauthSignInResponse> refreshToken(@Query("uid") String uid, @Query("min") Integer minutesToExpire);

    @POST("preEnrollment")
    Call<AmsResponsePreEnrollment> preEnrollment(@Body AmsRequestPreEnrollment request);

    @POST("pushNotificationSubscribe")
    Call<AmsResponse> pushNotificationSubscribe(@Body PushNotificationSubcribeRequest request);

    @POST("{uid}/associate")
    Call<AmsResponse> associateWithUrbanAirship(@Path("uid") String uid, @Body AmsAssociateUA amsAssociateUA);

    @POST("{uid}/disassociate")
    Call<AmsResponse> disassociateWithUrbanAirship(@Path("uid") String uid, @Body AmsAssociateUA amsDisassociateUA);

    @POST("create")
    Call<AmsResponseCreateUser> createUser(@Body AmsRequestCreateUser request);

    @POST("updatepersonalinfo")
    Call<ResponseWithHeader> updateUser(@Body AmsRequestUpdateUser request);

    @POST("password/update")
    Call<ResponseWithHeader> updatePassword(@Body AmsRequestUpdatePassword request);

    @POST("{uid}/feedback")
    Call<ResponseBody> sendFeedback(@Path("uid") String uid, @Body AmsRequestFeedback request);

    @GET("{uid}/history")
    Call<TransactionHistory> getTransactionHistory(@Path("uid") String uid);

    @POST("requestDeletion")
    Call<ResponseWithHeader> requestDeletion(@Body AmsRequestDeleteAccount request);

}
