package caribouapp.caribou.com.cariboucoffee.api;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsBanner;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsHomeMessage;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsMenuCategory;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsReward;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsWebContentSection;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by andressegurola on 9/27/17.
 */

public interface CmsApi {

    @GET("v3/mobilegreeting")
    Call<CmsHomeMessage> getHome();

    @GET("v3/mobilelegal")
    Call<List<CmsWebContentSection>> getTermsAndPrivacy();

    @GET("v3/mobilehelp")
    Call<List<CmsWebContentSection>> getFaqs();

    @GET("v3/reward")
    Call<List<CmsReward>> getRewards();

    @GET("v3/menu")
    Call<List<CmsMenuCategory>> getFilterableMenu();

    @GET("v3/banner?random=1")
    Call<CmsBanner> getRandomBanner();
}
