package caribouapp.caribou.com.cariboucoffee.api;

import android.app.Application;

import javax.inject.Named;
import javax.inject.Singleton;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.fiserv.api.FiservKeyInterceptor;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class RestOAuthV2Module {

    @Provides
    @Singleton
    public AmsApi provideAmsApi(@Named(RestModule.OK_HTTP_USER_CLIENT_BUILDER) OkHttpClient.Builder okHttpBuilder) {

        Retrofit retrofit = new Retrofit.Builder().
                client(okHttpBuilder.build()).
                addConverterFactory(GsonConverterFactory.create(GsonUtil.defaultGson())).
                baseUrl(AppConstants.AMS_V2_API_PATH).build();

        return retrofit.create(AmsApi.class);
    }

    @Provides
    @Singleton
    public SVmsAPI provideSVmsAPI(@Named(RestModule.OK_HTTP_USER_CLIENT_BUILDER) OkHttpClient.Builder okHttpBuilder) {

        Retrofit retrofit = new Retrofit.Builder().
                client(okHttpBuilder.build()).
                addConverterFactory(GsonConverterFactory.create(GsonUtil.defaultGson())).
                baseUrl(AppConstants.SVMS_V2_API_PATH).build();

        return retrofit.create(SVmsAPI.class);
    }

    @Provides
    @Singleton
    public OAuthAPI provideOauthAPI(@Named(RestModule.OK_HTTP_OAUTH_CLIENT_BUILDER) OkHttpClient.Builder okHttpBuilder) {

        Retrofit retrofit = new Retrofit.Builder().
                client(okHttpBuilder.build()).
                addConverterFactory(GsonConverterFactory.create(GsonUtil.defaultGson())).
                baseUrl(AppConstants.AMS_V2_API_PATH).build();

        return retrofit.create(OAuthAPI.class);
    }

    @Provides
    public TokenAuthenticator providesTokenAuthenticator(Application application,
                                                         UserServices userServices,
                                                         SettingsServices mSettingsServices,
                                                         OAuthAPI mOAuthAPI) {
        return new TokenAuthenticator(application, userServices, mSettingsServices, mOAuthAPI);
    }

    @Provides
    public FiservKeyInterceptor providesFiservAuthenticator(UserServices userServices) {
        return new FiservKeyInterceptor(userServices);
    }
}
