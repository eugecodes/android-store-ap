package caribouapp.caribou.com.cariboucoffee.api;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsReward;
import caribouapp.caribou.com.cariboucoffee.api.model.order.NcrCurbsideIamHere;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderWrappedData;
import caribouapp.caribou.com.cariboucoffee.fiserv.model.MoveLoyaltyRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NcrWrapperApi {


    @POST("orders")
    Call<NcrOrderWrappedData> postOrder(@Header("nep-enterprise-unit") String nepEnterpriseUnit, @Body NcrOrderWrappedData ncrOrderApiData);

    @POST("orders/{orderId}/reorder")
    Call<NcrOrderWrappedData> reorder(@Path("orderId") String orderId);

    @PUT("orders/{orderId}")
    Call<NcrOrderWrappedData> putOrder(
            @Header("nep-enterprise-unit") String nepEnterpriseUnit, @Path("orderId") String orderId, @Body NcrOrderWrappedData ncrOrderApiData);

    @GET("orders")
    Call<List<NcrOrderWrappedData>> getRecentOrders(@Query("days") int days);

    @GET("orders/{orderId}/check_status")
    Call<NcrOrderWrappedData> getOrder(@Path("orderId") String orderId);

    @GET("status")
    Call<ResponseBody> checkServerStatus();

    @GET("orders/{id}/applicable_rewards")
    Call<List<CmsReward>> loadApplicableRewards(@Path("id") String orderId);

    @PUT("orders/{id}/curbside_notification")
    Call<ResponseBody> sendCurbsideIamHere(@Path("id") String orderId, @Body NcrCurbsideIamHere omsCurbsideIamHere);

    @PUT("orders/{orderId}/loyalty")
    Call<NcrOrderWrappedData> moveOrderToLoyaltyAccount(@Path("orderId") String orderId,
                                                        @Body MoveLoyaltyRequest moveLoyaltyRequest);
}
