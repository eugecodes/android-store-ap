package caribouapp.caribou.com.cariboucoffee;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.tz.UTCProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.CmsApi;
import caribouapp.caribou.com.cariboucoffee.api.SVmsAPI;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsReward;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsMobileEligibleReward;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.ResponseAvailableRewards;
import caribouapp.caribou.com.cariboucoffee.api.model.storedValue.ResponseClaimedRewards;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.RewardsData;
import caribouapp.caribou.com.cariboucoffee.common.RewardsServiceImpl;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardItemModel;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.util.MockitoUtil;
import retrofit2.Call;

public class RewardsServiceTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private CmsApi mCmsApi;

    @Mock
    private Call<List<CmsReward>> mCmsApiRewardsCall;

    @Mock
    private SVmsAPI mSVmsAPI;

    @Mock
    private Call<ResponseAvailableRewards> mResponseAvailableRewardsCall;

    @Mock
    private Call<ResponseClaimedRewards> mResponseClaimedRewardsCall;

    @Mock
    private UserServices mUserServices;

    @Mock
    private SettingsServices mSettingsServices;

    @Mock
    private OrderService mOrderService;

    private RewardsServiceImpl mRewardsService;

    private boolean mOnSuccessCalled;

    @Before
    public void setup() {
        DateTimeZone.setProvider(new UTCProvider());
        DateTime now = new DateTime(2018, 10, 15, 15, 10, 0);
        CustomClock mCustomClock = new CustomClock(now);

        mRewardsService = new RewardsServiceImpl(mCmsApi, mSVmsAPI, mUserServices, mSettingsServices, mOrderService, mCustomClock);
        when(mSettingsServices.isOrderAhead()).thenReturn(true);
        when(mSettingsServices.isOrderAheadRewardsEnabled()).thenReturn(true);
        when(mUserServices.getUid()).thenReturn("820c2846-6f6d-43c5-97b3-d09a916296b9");
    }

    @Test
    public void testLoadRewards() {
        when(mCmsApi.getRewards()).thenReturn(mCmsApiRewardsCall);
        MockitoUtil.mockEnqueueArray(mCmsApiRewardsCall, "/test_cms_api_rewards.json", new TypeToken<List<CmsReward>>() {
        }.getType());

        when(mSVmsAPI.getAvailableRewards(anyString())).thenReturn(mResponseAvailableRewardsCall);
        MockitoUtil.mockEnqueue(mResponseAvailableRewardsCall, "/test_svms_available_rewards.json", ResponseAvailableRewards.class);

        when(mSVmsAPI.getClaimedRewards(anyString())).thenReturn(mResponseClaimedRewardsCall);
        MockitoUtil.mockEnqueue(mResponseClaimedRewardsCall, "/test_svms_claimed_rewards.json", ResponseClaimedRewards.class);

        MockitoUtil.answerResultCallback("/test_orderservice_oms_eligible.json",
                new TypeToken<List<OmsMobileEligibleReward>>() {
                }.getType())
                .when(mOrderService).loadEligibleRewards(any(), any());

        mRewardsService.loadRewards(true, true, false,
                new ResultCallback<RewardsData>() {

                    @Override
                    public void onSuccess(RewardsData data) {
                        mOnSuccessCalled = true;
                        assertEquals(3, data.getRedeemedRewards().size());
                        assertEquals(6, data.getAvailableRewards().size());

                        // Check redeemed
                        assertEquals(3, data.getRedeemedRewards().size());

                        // Check redeemed
                        RewardItemModel redeemed = data.getRedeemedRewards().get(0);
                        assertEquals("free beverage size upgrade", redeemed.getName().toLowerCase());
                        assertEquals(new LocalDate(2018, 10, 15), redeemed.getEndingDate().toLocalDate());
                        assertTrue(redeemed.isOmsMobileEligible());

                        // Check available to redeem
                        Collections.sort(data.getAvailableRewards(),
                                Comparator.comparing(RewardItemModel::getPoints));

                        RewardItemModel freeSizeUpgrade = data.getAvailableRewards().get(0);
                        freeSizeUpgrade.calculateBuyable(new BigDecimal(401));
                        assertEquals("free beverage size upgrade", freeSizeUpgrade.getName().toLowerCase());
                        assertEquals(30, freeSizeUpgrade.getPoints().intValue());
                        assertTrue(freeSizeUpgrade.isBuyable());

                        RewardItemModel bakeryItem = data.getAvailableRewards().get(1);
                        bakeryItem.calculateBuyable(new BigDecimal(40));
                        assertEquals("free stuff!!! bakery item", bakeryItem.getName().toLowerCase());
                        assertEquals(50, bakeryItem.getPoints().intValue());
                        assertFalse(bakeryItem.isBuyable());

                    }

                    @Override
                    public void onFail(int errorCode, String errorMessage) {
                        fail();
                    }

                    @Override
                    public void onError(Throwable error) {
                        fail();
                    }
                });

        verify(mCmsApi, times(1)).getRewards();
        verify(mSVmsAPI, times(1)).getAvailableRewards(anyString());
        verify(mSVmsAPI, times(1)).getClaimedRewards(anyString());
        verify(mOrderService, times(1)).loadEligibleRewards(any(), any());
        assertTrue(mOnSuccessCalled);
    }

}
