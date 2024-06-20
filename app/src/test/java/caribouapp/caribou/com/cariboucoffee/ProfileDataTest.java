package caribouapp.caribou.com.cariboucoffee;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import caribouapp.caribou.com.cariboucoffee.api.AmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponse;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResult;
import caribouapp.caribou.com.cariboucoffee.common.BaseResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserAccountService;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserAccountServiceImpl;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gonzalogelos on 8/30/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProfileDataTest {

    @Mock
    private AmsApi mAmsApi;
    private Call<AmsResponse> mGetProfileCall;
    private ArgumentCaptor<Callback> mCallbackCaptor;
    private UserAccountService mUserAccountService;
    @Mock
    private UserServices mUserServices;

    @Before
    public void setup() {
        mGetProfileCall = mock(Call.class);
        mCallbackCaptor = ArgumentCaptor.forClass(Callback.class);
        when(mAmsApi.getProfileData(
                any())).thenReturn(mGetProfileCall);
        mUserAccountService = new UserAccountServiceImpl(mAmsApi, mUserServices);
    }

    @Test
    public void testInformationMissingWithCache() {
        mUserAccountService.getProfileData(new BaseResultCallback<AmsResponse>() {

            @Override
            public void onSuccess(AmsResponse data) {
                super.onSuccess(data);
                Assert.assertNotNull(data);
                AmsResult amsResult = data.getResult();
                Assert.assertNotNull(amsResult.getAmsBillingInformation().getPaymentCardList());
                Assert.assertNotNull(amsResult.getAmsBillingInformation().getPaymentCardList());
                Assert.assertNotNull(amsResult.getPersonalInfo());
                Assert.assertFalse(amsResult.getPersonalInfo().getFirstName().isEmpty());
                Assert.assertNotNull(amsResult.getPreferences());
                verify(mUserServices, times(1)).setUser(any());
                verify(mAmsApi, times(1)).getProfileData(any());
            }
        });
        verify(mGetProfileCall).enqueue(mCallbackCaptor.capture());

        mCallbackCaptor.getValue().onResponse(null, Response.success(GsonUtil.readObjectFromClasspath("/test_profile_data.json", AmsResponse.class)));
    }

    @Test
    public void testInformationCompleteWithCache() {
        when(mUserServices.isMissingUserData()).thenReturn(false);
        mUserAccountService.getProfileDataWithCache(new BaseResultCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                super.onSuccess(data);
                verify(mAmsApi, times(0)).getProfileData(any());
                verifyNoInteractions(mAmsApi);
                Assert.assertNull(data);
            }
        });
    }

    @Test
    public void testMissingInformationCompleteWithNoCache() {
        when(mUserServices.isMissingUserData()).thenReturn(true);
        mUserAccountService.getProfileDataWithCache(new BaseResultCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                super.onSuccess(data);
                Assert.assertNull(data);
                verify(mUserServices, times(1)).setUser(any());
                verify(mAmsApi, times(1)).getProfileData(any());
            }
        });
        verify(mGetProfileCall).enqueue(mCallbackCaptor.capture());
        mCallbackCaptor.getValue().onResponse(null, Response.success(GsonUtil.readObjectFromClasspath("/test_profile_data.json", AmsResponse.class)));
    }
}
