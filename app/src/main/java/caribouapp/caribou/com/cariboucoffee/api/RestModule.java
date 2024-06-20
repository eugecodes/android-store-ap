package caribouapp.caribou.com.cariboucoffee.api;

import static caribouapp.caribou.com.cariboucoffee.AppConstants.CHECK_CAPTIVE_PORTAL_URL;

import android.app.Application;

import com.google.gson.Gson;

import net.cogindo.ssl.TLSSocketFactory;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.BuildConfig;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.cybersource.Base64Encoder;
import caribouapp.caribou.com.cariboucoffee.cybersource.CybersourceService;
import caribouapp.caribou.com.cariboucoffee.cybersource.UUIDGenerator;
import caribouapp.caribou.com.cariboucoffee.fiserv.api.FiservAPI;
import caribouapp.caribou.com.cariboucoffee.fiserv.api.FiservKeyInterceptor;
import caribouapp.caribou.com.cariboucoffee.fiserv.api.PayGateAPI;
import caribouapp.caribou.com.cariboucoffee.fiserv.api.PayGateKeyInterceptor;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;
import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class RestModule {

    public static final String OK_HTTP_CMS_CLIENT_BUILDER = "cmsClient";
    public static final String OK_HTTP_OAUTH_CLIENT_BUILDER = "oauthClient";
    public static final String OK_HTTP_USER_CLIENT_BUILDER = "userClient";
    public static final String OK_HTTP_ORDER_CLIENT_BUILDER = "orderClient";
    public static final String OK_HTTP_PAY_GATE_CLIENT_BUILDER = "payGateClient";
    public static final String OK_HTTP_FISERV_CLIENT_BUILDER = "fiservClient";
    private static final String MOCK_BASE_URL = "https://localhost/";

    @Provides
    @Singleton
    public YextApi provideYextApi(OkHttpClient.Builder okHttpBuilder) {

        okHttpBuilder.interceptors().add(new CmsKeyInterceptor());
        Retrofit retrofit = new Retrofit.Builder().
                client(okHttpBuilder.build()).
                addConverterFactory(GsonConverterFactory.create(GsonUtil.defaultGson())).
                baseUrl(AppConstants.YEXT_API_PATH).build();

        return retrofit.create(YextApi.class);
    }

    @Provides
    @Singleton
    public CmsApi provideCmsApi(@Named(OK_HTTP_CMS_CLIENT_BUILDER) OkHttpClient.Builder okHttpBuilder) {

        Retrofit retrofit = new Retrofit.Builder().
                client(okHttpBuilder.build()).
                addConverterFactory(GsonConverterFactory.create(GsonUtil.defaultGson())).
                baseUrl(AppConstants.CMS_API_PATH).build();

        return retrofit.create(CmsApi.class);
    }

    @Provides
    @Singleton
    public CmsOrderApi provideCmsOrderApi(@Named(OK_HTTP_CMS_CLIENT_BUILDER) OkHttpClient.Builder okHttpBuilder) {

        Retrofit retrofit = new Retrofit.Builder().
                client(okHttpBuilder.build()).
                addConverterFactory(GsonConverterFactory.create(GsonUtil.defaultGson())).
                baseUrl(AppConstants.CMS_ORDER_API_PATH).build();

        return retrofit.create(CmsOrderApi.class);
    }

    @Provides
    public OkHttpClient.Builder provideOkHttpClientBuilder(Application application,
                                                           X509TrustManager trustManager,
                                                           SettingsServices settingsServices) {
        TimeoutInterceptor timeoutInterceptor = new TimeoutInterceptor(settingsServices);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BASIC);


        int cacheSize = 10 * 1024 * 1024; // 10 MB
        Cache cache = new Cache(application.getCacheDir(), cacheSize);

        // TODO: custom timeouts to support the slow response times of the staging server
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .cache(cache)
                    .sslSocketFactory(new TLSSocketFactory(), trustManager)
                    .connectTimeout(16, TimeUnit.SECONDS)
                    .readTimeout(16, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(new AppInfoHeaderInterceptor())
                    .addInterceptor(timeoutInterceptor);

            if (BuildConfig.DEBUG) {
                builder.addInterceptor(new CacheLogInterceptor());
            }

            return builder;
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Provides
    @Named(OK_HTTP_CMS_CLIENT_BUILDER)
    public OkHttpClient.Builder provideOkHttpClientBuilderCmsApi(OkHttpClient.Builder builder) {
        builder.interceptors().add(0, new CmsKeyInterceptor());
        return builder;
    }

    @Provides
    @Named(OK_HTTP_OAUTH_CLIENT_BUILDER)
    public OkHttpClient.Builder provideOkHttpClientBuilderOauthApi(OkHttpClient.Builder builder) {
        builder.interceptors().add(0, new CmsKeyInterceptor());
        return builder;
    }

    @Provides
    @Named(OK_HTTP_USER_CLIENT_BUILDER)
    public OkHttpClient.Builder provideOkHttpClientBuilderUserApi(OkHttpClient.Builder builder, TokenAuthenticator tokenAuthenticator) {
        builder.interceptors().add(0, new CmsKeyInterceptor());
        builder.interceptors().add(0, tokenAuthenticator);
        return builder;
    }

    @Provides
    @Named(OK_HTTP_ORDER_CLIENT_BUILDER)
    public OkHttpClient.Builder provideOkHttpClientBuilderOrderApi(OkHttpClient.Builder builder, TokenAuthenticator tokenAuthenticator) {
        builder.interceptors().add(0, tokenAuthenticator);
        return builder;
    }

    @Provides
    @Named(OK_HTTP_PAY_GATE_CLIENT_BUILDER)
    public OkHttpClient.Builder provideOkHttpClientBuilderPayGateApi(OkHttpClient.Builder builder) {
        builder.interceptors().add(0, new PayGateKeyInterceptor());
        return builder;
    }

    @Provides
    @Named(OK_HTTP_FISERV_CLIENT_BUILDER)
    public OkHttpClient.Builder provideOkHttpClientBuilderFiservApi(OkHttpClient.Builder builder, FiservKeyInterceptor fiservKeyInterceptor) {
        builder.interceptors().add(0, fiservKeyInterceptor);
        return builder;
    }

    @Provides
    @Singleton
    public X509TrustManager providesX509TrustManager() {
        try {
            /**
             * This code was taking straight from the example out of the javadoc
             * for {@link  OkHttpClient.Builder#sslSocketFactory(SSLSocketFactory, X509TrustManager)}
             */
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            return trustManager;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Provides
    @Singleton
    public CybersourceService providesCybersourceService(Base64Encoder base64Encoder, Clock clock,
                                                         UUIDGenerator uuidGenerator, X509TrustManager trustManager) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(httpLoggingInterceptor);
        }

        try {
            clientBuilder.connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS).sslSocketFactory(new TLSSocketFactory(), trustManager);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return new CybersourceService(clientBuilder.build(), base64Encoder, BuildConfig.CYBERSOURCE_SECRET_KEY, clock, uuidGenerator);
    }

    @Provides
    @Singleton
    public GeocodingApi provideGeocodingApi(@Named(OK_HTTP_CMS_CLIENT_BUILDER) OkHttpClient.Builder okHttpBuilder) {

        Retrofit retrofit = new Retrofit.Builder().
                client(okHttpBuilder.build()).
                addConverterFactory(GsonConverterFactory.create(GsonUtil.defaultGson())).
                baseUrl(GeocodingApi.GOOGLE_GEOCODING_URL).build();

        return retrofit.create(GeocodingApi.class);
    }

    @Provides
    @Singleton
    public CheckCaptivePortalApi provideCheckCaptivePortalApi(OkHttpClient.Builder okHttpBuilder) {
        return new Retrofit.Builder()
                .baseUrl(CHECK_CAPTIVE_PORTAL_URL)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(okHttpBuilder.build())
                .build().create(CheckCaptivePortalApi.class);
    }

    @Provides
    @Singleton
    public TriviaApi provideTriviaApi(@Named(OK_HTTP_USER_CLIENT_BUILDER) OkHttpClient.Builder okHttpBuilder) {
        return new Retrofit.Builder()
                .baseUrl(AppConstants.TRIVIA_V1_API_PATH)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(okHttpBuilder.build())
                .build().create(TriviaApi.class);
    }

    @Provides
    @Singleton
    public NcrWrapperApi provideNcrWrapperApi(@Named(OK_HTTP_ORDER_CLIENT_BUILDER) OkHttpClient.Builder okHttpBuilder) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.NCR_WRAPPER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonUtil.defaultGson()))
                .client(okHttpBuilder.build())
                .build().create(NcrWrapperApi.class);
    }

    @Provides
    @Singleton
    public PayGateAPI providePayGateApi(@Named(OK_HTTP_PAY_GATE_CLIENT_BUILDER) OkHttpClient.Builder okHttpBuilder) {
        return new Retrofit.Builder()
                .baseUrl(AppConstants.PAY_GATE_V1_API_PATH)
                .addConverterFactory(GsonConverterFactory.create(GsonUtil.defaultGson()))
                .client(okHttpBuilder.build())
                .build().create(PayGateAPI.class);
    }

    @Provides
    @Singleton
    public FiservAPI provideFiservApi(@Named(OK_HTTP_FISERV_CLIENT_BUILDER) OkHttpClient.Builder okHttpBuilder) {
        return new Retrofit.Builder()
                .baseUrl(AppConstants.FISERV_V1_API_PATH)
                .addConverterFactory(GsonConverterFactory.create(GsonUtil.defaultGson()))
                .client(okHttpBuilder.build())
                .build().create(FiservAPI.class);
    }
}
