package caribouapp.caribou.com.cariboucoffee.api;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsMenu;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsStoreReward;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CmsOrderApi {

    @GET("v1/store-menu/{storeId}")
    Call<CmsMenu> getStoreMenu(@Path("storeId") String storeId);

    @GET("v1/store-rewards/{storeId}")
    Call<List<CmsStoreReward>> getLocationRewards(@Path("storeId") String locationId);

}
