package caribouapp.caribou.com.cariboucoffee;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.util.List;

import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.analytics.JoinLoginType;
import caribouapp.caribou.com.cariboucoffee.api.AmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.ResponseWithHeader;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponseCreateUser;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapper;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserAccountService;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.PersonalInformationContract;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.SetPasswordContract;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.model.PersonalInformationModel;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter.PersonalInformationPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter.SetPasswordPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.GeolocationServices;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.LocationInfo;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.push.PushManager;
import caribouapp.caribou.com.cariboucoffee.util.MockitoUtil;
import retrofit2.Call;
import retrofit2.Response;

public class PersonalInformationTest {

    private static final String ZIPCODE = "55415";
    private static final String CITY = "Minneapolis";
    private static final String STATE = "MN";
    private static final String USER_UID = "120";
    private static final String TOKEN = "53240923849032845415";
    private static final String FIRST_NAME = "Pepe";
    private static final String LAST_NAME = "Lepu";
    private static final String EMAIL = "test@gmail.com";
    private static final String PHONE_NUMBER = "38974383456";
    private static final LocalDate BIRTHDAY = new LocalDate(1983, 4, 12);


    private AmsApi mAmsApi;
    private GeolocationServices mGeolocationServices;
    private EventLogger mEventLogger;
    private ErrorMessageMapper mErrorMessageMapper;
    private Call<AmsResponseCreateUser> mCreateUserCall;
    private PersonalInformationPresenter mPersonalInformationPresenter;
    private PersonalInformationContract.View mView;
    private PersonalInformationModel mModel;
    private SetPasswordPresenter mSetPasswordPresenter;
    private SetPasswordContract.View mSetPasswordView;
    private UserAccountService mUserAccountService;
    private UserServices mUserServices;
    private PushManager mPushManager;
    private OrderService mOrderService;

    @Before
    public void setup() {
        mAmsApi = mock(AmsApi.class);
        mEventLogger = mock(EventLogger.class);
        mModel = new PersonalInformationModel();

        mView = mock(PersonalInformationContract.View.class);
        when(mView.isActive()).thenReturn(true);
        doAnswer(invocation -> {
            invocation.getArgument(0, Runnable.class).run();
            return null;
        }).when(mView).runOnUiThread(any());

        mErrorMessageMapper = mock(ErrorMessageMapper.class);
        when(mErrorMessageMapper.isSuccessful(any(Response.class))).thenReturn(true);
        when(mErrorMessageMapper.isSuccessful(any(ResponseWithHeader.class))).thenReturn(true);

        mCreateUserCall = mock(Call.class);
        when(mAmsApi.createUser(any())).thenReturn(mCreateUserCall);

        mGeolocationServices = mock(GeolocationServices.class);

        mSetPasswordPresenter = mock(SetPasswordPresenter.class);
        when(mSetPasswordPresenter.isPasswordActive()).thenReturn(true);
        mSetPasswordView = mock(SetPasswordContract.View.class);
        when(mSetPasswordView.passwordErrorEnable(anyList()))
                .thenAnswer((Answer<Boolean>) invocation -> !invocation.getArgument(0, List.class).isEmpty());
        when(mSetPasswordPresenter.getView()).thenReturn(mSetPasswordView);

        doAnswer((Answer<Void>) invocation -> {
            String zipcode = invocation.getArgument(0, String.class);
            Assert.assertEquals(ZIPCODE, zipcode);
            ResultCallback resultCallback = invocation.getArgument(1, ResultCallback.class);
            LocationInfo locationInfo = new LocationInfo();
            locationInfo.setCity(CITY);
            locationInfo.setState(STATE);
            resultCallback.onSuccess(locationInfo);
            return null;
        }).when(mGeolocationServices).getCityAndStateFromZipcode(anyString(), any());

        mUserServices = mock(UserServices.class);
        mPushManager = mock(PushManager.class);
        mOrderService = mock(OrderService.class);
        mUserAccountService = mock(UserAccountService.class);

        doAnswer((Answer<Void>) invocation -> {
            ResultCallback resultCallback = invocation.getArgument(0, ResultCallback.class);
            resultCallback.onSuccess(createAmsResponse());
            return null;
        }).when(mUserAccountService).getProfileData(any());

        mPersonalInformationPresenter = new PersonalInformationPresenter(mView, mModel, null, mSetPasswordPresenter);
        mPersonalInformationPresenter.setAmsApi(mAmsApi);
        mPersonalInformationPresenter.setUserAccountService(mUserAccountService);
        mPersonalInformationPresenter.setUserServices(mUserServices);
        mPersonalInformationPresenter.setPushManager(mPushManager);
        mPersonalInformationPresenter.setEventLogger(mEventLogger);
        mPersonalInformationPresenter.setErrorMessageMapper(mErrorMessageMapper);
        mPersonalInformationPresenter.setErrorMessageMapper(mErrorMessageMapper);
        mPersonalInformationPresenter.setOrderService(mOrderService);
        mPersonalInformationPresenter.setGeolocationServices(mGeolocationServices);
        mPersonalInformationPresenter.setClock(new CustomClock(new DateTime()));
    }

    private void setModelData() {
        mModel.setEmail(EMAIL);
        mModel.setPhoneNumber(PHONE_NUMBER);

        mModel.setFirstName(FIRST_NAME);
        mModel.setLastName(LAST_NAME);
        mModel.setBirthday(BIRTHDAY);
        mModel.setZipCode(ZIPCODE);
    }


    private AmsResponseCreateUser createAmsCreateUserResponse() {
        AmsResponseCreateUser amsResponseCreateUser = new AmsResponseCreateUser();
        amsResponseCreateUser.setUid(USER_UID);
        amsResponseCreateUser.setToken(TOKEN);
        return amsResponseCreateUser;
    }

    private AmsResponse createAmsResponse() {
        AmsResponse amsResponse = new AmsResponse();
        return amsResponse;
    }

    @Test
    public void testCreateUserWithEmail() {

        setModelData();

        MockitoUtil.mockEnqueueWithObject(mCreateUserCall, createAmsCreateUserResponse());

        when(mView.getPassword()).thenReturn("Caribou1");

        mPersonalInformationPresenter.createNewUser();

        verify(mGeolocationServices, times(1))
                .getCityAndStateFromZipcode(eq(ZIPCODE), any());

        verify(mAmsApi,
                times(1))
                .createUser(any());

        verify(mSetPasswordPresenter, times(2)).isPasswordActive();
        verify(mSetPasswordView, times(1)).passwordErrorEnable(anyList());
        verify(mEventLogger, times(1)).logEnrollStepCompleted(JoinLoginType.EMAIL, AppScreen.PERSONAL_INFO);
        verify(mEventLogger, times(1)).logEnrollCompleted(JoinLoginType.EMAIL);
        verify(mEventLogger, times(1)).logCompletedRegistration();
        verify(mUserServices, times(1)).setAuthToken(TOKEN);
        verify(mUserServices, times(1)).setUid(USER_UID);
        verify(mPushManager, times(1)).registerForUrbanAirshipPush();
        verify(mUserAccountService, times(1)).getProfileData(any());
        verify(mView, times(1)).goToDashboard();
    }

    @Test
    public void testCreateUserWithGoogle() {

        setModelData();
        mModel.setEnrolledViaGoogle(true);

        MockitoUtil.mockEnqueueWithObject(mCreateUserCall, createAmsCreateUserResponse());

        when(mSetPasswordPresenter.isPasswordActive()).thenReturn(false);
        when(mView.getPassword()).thenReturn(null);

        mPersonalInformationPresenter.createNewUser();

        verify(mGeolocationServices, times(1))
                .getCityAndStateFromZipcode(eq(ZIPCODE), any());

        verify(mAmsApi,
                times(1))
                .createUser(any());

        verify(mSetPasswordPresenter, times(2)).isPasswordActive();
        verify(mEventLogger, times(1)).logEnrollStepCompleted(JoinLoginType.GOOGLE, AppScreen.PERSONAL_INFO);
        verify(mEventLogger, times(1)).logEnrollCompleted(JoinLoginType.GOOGLE);
        verify(mEventLogger, times(1)).logCompletedRegistration();
        verify(mUserServices, times(1)).setAuthToken(TOKEN);
        verify(mUserServices, times(1)).setUid(USER_UID);
        verify(mPushManager, times(1)).registerForUrbanAirshipPush();
        verify(mUserAccountService, times(1)).getProfileData(any());
        verify(mView, times(1)).goToDashboard();
    }

}
