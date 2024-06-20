package caribouapp.caribou.com.cariboucoffee;

import android.app.Application;
import android.util.Base64;

import com.urbanairship.UAirship;
import com.urbanairship.messagecenter.Inbox;
import com.urbanairship.messagecenter.MessageCenter;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.interfaces.RSAPublicKey;
import java.util.Random;
import java.util.UUID;

import javax.inject.Named;
import javax.inject.Singleton;

import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLoggerImpl;
import caribouapp.caribou.com.cariboucoffee.analytics.MetricsLogger;
import caribouapp.caribou.com.cariboucoffee.analytics.MetricsLoggerImpl;
import caribouapp.caribou.com.cariboucoffee.analytics.Tagger;
import caribouapp.caribou.com.cariboucoffee.analytics.TaggerImpl;
import caribouapp.caribou.com.cariboucoffee.analytics.TracerFactory;
import caribouapp.caribou.com.cariboucoffee.api.AmsApi;
import caribouapp.caribou.com.cariboucoffee.api.CmsApi;
import caribouapp.caribou.com.cariboucoffee.api.CmsOrderApi;
import caribouapp.caribou.com.cariboucoffee.api.GeocodingApi;
import caribouapp.caribou.com.cariboucoffee.api.NcrWrapperApi;
import caribouapp.caribou.com.cariboucoffee.api.SVmsAPI;
import caribouapp.caribou.com.cariboucoffee.api.YextApi;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.RewardsService;
import caribouapp.caribou.com.cariboucoffee.common.RewardsServiceImpl;
import caribouapp.caribou.com.cariboucoffee.cybersource.Base64Encoder;
import caribouapp.caribou.com.cariboucoffee.cybersource.UUIDGenerator;
import caribouapp.caribou.com.cariboucoffee.fiserv.api.FiservAPI;
import caribouapp.caribou.com.cariboucoffee.fiserv.api.FiservService;
import caribouapp.caribou.com.cariboucoffee.fiserv.api.FiservServiceImpl;
import caribouapp.caribou.com.cariboucoffee.fiserv.api.PayGateAPI;
import caribouapp.caribou.com.cariboucoffee.fiserv.api.PayGateService;
import caribouapp.caribou.com.cariboucoffee.fiserv.api.PayGateServiceImpl;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapper;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.FirebaseIdProvider;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.FirebaseIdProviderImpl;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServicesImpl;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserAccountService;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserAccountServiceImpl;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServicesImpl;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.BarcodeService;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.BarcodeServiceImpl;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.DashboardDataStorage;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.DashboardDataStorageImpl;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.GeolocationImpl;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.GeolocationServices;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.MenuDataService;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.NcrMenuDataServiceImpl;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OrderNavHelper;
import caribouapp.caribou.com.cariboucoffee.mvp.push.UAirshipServices;
import caribouapp.caribou.com.cariboucoffee.mvp.push.UAirshipServicesImpl;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.order.ncr.NcrOrderServiceImpl;
import caribouapp.caribou.com.cariboucoffee.push.PushManager;
import caribouapp.caribou.com.cariboucoffee.stores.StoresService;
import caribouapp.caribou.com.cariboucoffee.stores.StoresServiceImpl;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Singleton
    @Provides
    FirebaseIdProvider providesFirebaseIdProvider() {
        return new FirebaseIdProviderImpl();
    }

    @Singleton
    @Provides
    Application providesApplication() {
        return mApplication;
    }

    @Singleton
    @Provides
    Clock providesClock() {
        return new Clock() {
            @Override
            public LocalTime getCurrentTime() {
                return new LocalTime();
            }

            @Override
            public DateTime getCurrentDateTime() {
                return new DateTime();
            }
        };
    }

    @Singleton
    @Provides
    GeolocationServices providesGeoLocationServices(Application application, GeocodingApi geocodingApi) {
        return new GeolocationImpl(application, geocodingApi);
    }

    @Singleton
    @Provides
    UserServices providesUserServices(Application application, FirebaseIdProvider firebaseIdProvider) {
        return new UserServicesImpl(application, firebaseIdProvider);
    }

    @Singleton
    @Provides
    UAirshipServices providesUAirshipServices(Application application, FirebaseIdProvider firebaseIdProvider) {
        return new UAirshipServicesImpl();
    }

    @Singleton
    @Provides
    EventLogger providesFirebaseEventLogger(Application application) {
        return new EventLoggerImpl(application);
    }

    @Singleton
    @Provides
    Tagger providesTagger() {
        return new TaggerImpl();
    }

    @Singleton
    @Provides
    DashboardDataStorage providesDashboardStorage(Application application) {
        return new DashboardDataStorageImpl(application);
    }

    @Singleton
    @Provides
    AppDataStorage providesAppDataStorage(Application application) {
        return new AppDataStorageImpl(application);
    }

    @Singleton
    @Provides
    ErrorMessageMapper providesErrorMessageMapper(Application application) {
        Reader mappingsData;
        try {
            mappingsData = new InputStreamReader(application.getAssets().open("error_message_mappings.json"));
            return new ErrorMessageMapper(mappingsData);
        } catch (IOException e) {
            throw new RuntimeException("Problems reading mappings file", e);
        }
    }

    @Singleton
    @Provides
    SettingsServices providesSettingsServices(Application application) {
        return new SettingsServicesImpl(application);
    }

    @Singleton
    @Provides
    StoresService providesStoreService(YextApi yextApi) {
        return new StoresServiceImpl(yextApi);
    }

    @Provides
    Base64Encoder providesBase64Encoder() {
        return data -> Base64.encodeToString(data, Base64.DEFAULT);
    }

    @Provides
    UUIDGenerator provideUUIDGenerator() {
        return () -> UUID.randomUUID().toString();
    }

    @Provides
    @Singleton
    PushManager providePushManager(UserServices userServices,
                                   UAirshipServices airshipServices,
                                   AmsApi amsApi,
                                   ErrorMessageMapper errorMessageMapper) {
        return new PushManager(amsApi, userServices, airshipServices, errorMessageMapper);
    }

    @Provides
    @Singleton
    BarcodeService provideBarcodeService(Application application) {
        return new BarcodeServiceImpl(application);
    }

    @Provides
    @Singleton
    OrderService provideOrderService(Clock clock, UserServices userServices,
                                     MenuDataService menuDataService,
                                     NcrWrapperApi ncrWrapperApi,
                                     SettingsServices settingsServices, TracerFactory tracerFactory) {
        return new NcrOrderServiceImpl(clock, userServices, (NcrMenuDataServiceImpl) menuDataService,
                ncrWrapperApi, settingsServices, tracerFactory);
    }

    @Provides
    @Singleton
    UserAccountService provideUserAccountService(AmsApi amsApi, UserServices userServices) {
        return new UserAccountServiceImpl(amsApi, userServices);
    }

    @Provides
    @Singleton
    MenuDataService provideMenuDataService(CmsApi cmsApi, CmsOrderApi cmsOrderApi, SettingsServices settingsServices) {
        return new NcrMenuDataServiceImpl(cmsApi, cmsOrderApi, settingsServices);
    }

    @Provides
    @Singleton
    PayGateService providePayGateService(PayGateAPI payGateAPI,
                                         UserServices userServices,
                                         SettingsServices settingsServices) {
        return new PayGateServiceImpl(payGateAPI, userServices,
                settingsServices);
    }

    @Provides
    @Singleton
    FiservService provideFiservService(FiservAPI fiservAPI,
                                       UserServices userServices,
                                       SettingsServices settingsServices) {
        return new FiservServiceImpl(fiservAPI, userServices,
                settingsServices);
    }

    @Provides
    @Singleton
    Random provideRandom() {
        return new Random();
    }

    @Provides
    UAirship provideUAirship() {
        return UAirship.shared();
    }

    @Provides
    Inbox provideUAirshipInbox() {
        return MessageCenter.shared().getInbox();
    }

    @Provides
    @Singleton
    RewardsService provideRewardService(CmsApi cmsApi, SVmsAPI svmsApi,
                                        UserServices userServices, SettingsServices settingsServices,
                                        OrderService orderService, Clock clock) {
        return new RewardsServiceImpl(cmsApi, svmsApi, userServices, settingsServices, orderService, clock);
    }

    @Provides
    @Singleton
    DeeplinkParser provideDeeplinkParser() {
        return new DeeplinkParser();
    }

    @Provides
    @Singleton
    MetricsLogger provideMetricsLogger(Application application) {
        return new MetricsLoggerImpl(application);
    }

    @Provides
    @Singleton
    TracerFactory provideTracerFactory(MetricsLogger metricsLogger) {
        return new TracerFactory(metricsLogger);
    }

    @Provides
    @Singleton
    OrderNavHelper provideOrderNavHelper(SettingsServices settingsServices) {
        return new OrderNavHelper(settingsServices);
    }

    @Provides
    @Named("PayGatePublicKey")
    @Singleton
    RSAPublicKey providePayGatePublicKey() {
        try {
            InputStream inStream = getClass().getResourceAsStream(AppConstants.PAY_GATE_PUBLIC_KEY_PATH);
            byte[] bytes = StringUtils.bytesFromInputStream(inStream);
            return StringUtils.readPublicKey(bytes);
        } catch (Exception e) {
            String message = String.format(
                    "PayGate public key '%s' not present or incorrectly configured in build!",
                    AppConstants.PAY_GATE_PUBLIC_KEY_PATH);
            throw new IllegalStateException(message, e);
        }
    }
}
