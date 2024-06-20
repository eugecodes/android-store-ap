package caribouapp.caribou.com.cariboucoffee;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import caribouapp.caribou.com.cariboucoffee.analytics.Tracer;
import caribouapp.caribou.com.cariboucoffee.analytics.TracerFactory;
import caribouapp.caribou.com.cariboucoffee.api.CmsApi;
import caribouapp.caribou.com.cariboucoffee.api.CmsOrderApi;
import caribouapp.caribou.com.cariboucoffee.api.NcrWrapperApi;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsMenu;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsReward;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsStoreReward;
import caribouapp.caribou.com.cariboucoffee.api.model.order.NcrCurbsideIamHere;
import caribouapp.caribou.com.cariboucoffee.api.model.order.OmsMobileEligibleReward;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderStatus;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderWrappedData;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.common.RewardsData;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.CurbsideOrderData;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.CurbsidePickupData;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ncr.NcrOrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.MenuData;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.NcrMenuDataServiceImpl;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.RewardBannerModel;
import caribouapp.caribou.com.cariboucoffee.order.DiscountLine;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.order.PreSelectedReward;
import caribouapp.caribou.com.cariboucoffee.order.ncr.NcrOrderServiceImpl;
import caribouapp.caribou.com.cariboucoffee.util.ItemCustomizationHelper;
import caribouapp.caribou.com.cariboucoffee.util.MockitoUtil;
import caribouapp.caribou.com.cariboucoffee.util.TestResultCallback;
import okhttp3.ResponseBody;
import retrofit2.Call;

@RunWith(MockitoJUnitRunner.class)
public class NcrOrderServiceTest {

    public static final String DEFAULT_STORE_ID = "2101";

    public static final String PROD_ID_LATTE = "2464";
    public static final String PROD_ID_MOCHA = "2432";
    public static final String PROD_ID_FARMHOUSE = "2438";


    private OrderService<NcrOrderItem> mOrderService;

    private NcrMenuDataServiceImpl mMenuDataService;

    @Mock
    private CmsApi mCmsApi;

    @Mock
    private CmsOrderApi mCmsOrderApi;

    @Mock
    private Call<CmsMenu> mCmsMenuCall;

    @Mock
    private NcrWrapperApi mNcrWrapperApi;

    @Mock
    private Call<NcrOrderWrappedData> mPostOrderCall;

    @Mock
    private Call<NcrOrderWrappedData> mPutOrderCall;

    @Mock
    private Call<NcrOrderWrappedData> mCheckOrderStatusCall;

    @Mock
    private Call<List<CmsReward>> mApplicableRewardsCall;

    @Mock
    private Call<ResponseBody> mSendCurbsideIamHereCall;

    @Mock
    private UserServices mUserServices;

    @Mock
    private Clock mClock;

    @Mock
    private SettingsServices mSettingsServices;

    @Mock
    private TracerFactory mTracerFactory;

    @Mock
    private Tracer mTracer;

    private Order<NcrOrderItem> mCurrentOrder;

    @Mock
    private Call<List<CmsStoreReward>> mCmsStoreRewardsCalls;


    @Before
    public void setup() {
        mMenuDataService = new NcrMenuDataServiceImpl(mCmsApi, mCmsOrderApi, mSettingsServices);

        when(mSettingsServices.getOrderAheadCheckStatusMaxAttempts()).thenReturn(8);
        when(mSettingsServices.getCurbsideImHereMinutesToStart()).thenReturn(5);
        when(mSettingsServices.getCurbsideImHereMinutesToEnd()).thenReturn(10);

        mOrderService = new NcrOrderServiceImpl(mClock, mUserServices, mMenuDataService, mNcrWrapperApi, mSettingsServices, mTracerFactory);

        MockitoUtil.mockEnqueue(mCmsMenuCall, "/test_cms_menu_ncr_order_ahead.json", CmsMenu.class);
        when(mCmsOrderApi.getStoreMenu(anyString())).thenReturn(mCmsMenuCall);

        setupNcrWrapperMocks();
        setupTracerFactoryMocks();
        setupClockMocks();
    }

    private void setupClockMocks() {
        when(mClock.getCurrentDateTime()).thenReturn(new DateTime(2019, 10, 25, 11, 20, 0, 0));
    }

    private void setupNcrWrapperMocks() {
        when(mNcrWrapperApi.postOrder(anyString(), any())).thenReturn(mPostOrderCall);
        when(mNcrWrapperApi.putOrder(anyString(), anyString(), any())).thenReturn(mPutOrderCall);
        when(mNcrWrapperApi.getOrder(anyString())).thenReturn(mCheckOrderStatusCall);
        when(mNcrWrapperApi.loadApplicableRewards(any())).thenReturn(mApplicableRewardsCall);
        when(mNcrWrapperApi.sendCurbsideIamHere(anyString(), any())).thenReturn(mSendCurbsideIamHereCall);
    }

    private void setupTracerFactoryMocks() {
        when(mTracerFactory.createTracer(anyString())).thenReturn(mTracer);
        when(mTracer.start()).thenReturn(mTracer);
        when(mCmsOrderApi.getLocationRewards(anyString())).thenReturn(mCmsStoreRewardsCalls);
        MockitoUtil.mockEnqueueArray(mCmsStoreRewardsCalls, "/test_ncr_get_store_rewards.json", new TypeToken<ArrayList<CmsStoreReward>>() {
        }.getType());
    }

    @Test
    public void testOrderCreation() {
        StoreLocation storeLocation = new StoreLocation();
        storeLocation.setId(DEFAULT_STORE_ID);

        mOrderService.createOrderWithLocation(storeLocation, new TestResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                super.onSuccess(data);
                mCurrentOrder = data;
                assertNotNull(data);
                assertEquals(storeLocation.getId(), data.getStoreLocation().getId());
                assertEquals(0, data.getTotalItemsInCart());
            }
        });

        mOrderService.loadCurrentOrder(new TestResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                assertNotNull(data);
                assertEquals(mCurrentOrder, data);
                assertEquals(storeLocation.getId(), data.getStoreLocation().getId());
                assertEquals(0, data.getTotalItemsInCart());
            }
        });

    }

    @Test
    public void testAddItem1() {
        testOrderCreation();

        mMenuDataService.getOrderAheadMenuDataFiltered(DEFAULT_STORE_ID, null,
                new ResultCallback<MenuData>() {
                    @Override
                    public void onSuccess(MenuData data) {
                        // NO-OP
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

        MenuCardItemModel latteMenucMenuCardItemModel = mMenuDataService.getProductData(PROD_ID_LATTE);
        NcrOrderItem orderItem = new NcrOrderItem(latteMenucMenuCardItemModel);
        orderItem.loadModifiers(latteMenucMenuCardItemModel.getNcrOmsData());
        orderItem.setQuantity(2);
        orderItem.setSize(SizeEnum.MEDIUM);

        mOrderService.addItem(orderItem, new TestResultCallback<NcrOrderItem>() {
            @Override
            public void onSuccess(NcrOrderItem data) {
                assertEqualDecimal("3.75", data.getPrice());
                assertEqualDecimal("0.0", data.calculateAdditionalCharges());
                assertEqualDecimal("7.50", data.getSubtotal());
            }
        });

        mOrderService.loadCurrentOrder(new TestResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                assertEqualDecimal("7.50", data.getSubtotal());
            }
        });

    }

    @Test
    public void testAddItem2() {
        testAddItem1();

        MenuCardItemModel mochaMenuCardItemModel = mMenuDataService.getProductData(PROD_ID_MOCHA);
        NcrOrderItem orderItem = new NcrOrderItem(mochaMenuCardItemModel);
        orderItem.setQuantity(1);
        orderItem.loadModifiers(mochaMenuCardItemModel.getNcrOmsData());
        orderItem.setSize(SizeEnum.SMALL);

        mOrderService.addItem(orderItem, new TestResultCallback<NcrOrderItem>() {
            @Override
            public void onSuccess(NcrOrderItem data) {
                assertEqualDecimal("3.25", data.getPrice());
                assertEqualDecimal("0.00", data.calculateAdditionalCharges());
                assertEqualDecimal("3.25", data.getSubtotal());
            }
        });

        mOrderService.loadCurrentOrder(new TestResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                assertEqualDecimal("10.75", data.getSubtotal());
            }
        });
    }

    @Test
    public void testAddItem3() {
        testAddItem2();

        MenuCardItemModel farmhouseMenuCardItemModel = mMenuDataService.getProductData(PROD_ID_FARMHOUSE);
        NcrOrderItem orderItem = new NcrOrderItem(farmhouseMenuCardItemModel);
        orderItem.setQuantity(1);
        orderItem.loadModifiers(farmhouseMenuCardItemModel.getNcrOmsData());

        mOrderService.addItem(orderItem, new TestResultCallback<NcrOrderItem>() {
            @Override
            public void onSuccess(NcrOrderItem data) {
                assertEqualDecimal("5.99", data.getPrice());
                assertEqualDecimal("1.35", data.calculateAdditionalCharges());
                assertEqualDecimal("7.34", data.getSubtotal());
            }
        });

        mOrderService.loadCurrentOrder(new TestResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                assertEqualDecimal("18.09", data.getSubtotal());
            }
        });
    }

    @Test
    public void testRemoveItem1() {
        testAddItem3();

        NcrOrderItem mochaNcrOrderItem = mCurrentOrder.getItems().get(1);

        mOrderService.removeItem(mochaNcrOrderItem, new TestResultCallback<>());
        mOrderService.loadCurrentOrder(new TestResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                assertEquals(3, data.getTotalItemsInCart());
                assertEquals(2, data.getItems().size());
                assertEqualDecimal("14.84", data.getSubtotal());
            }
        });
    }


    @Test
    public void testUpdateQuantity() {
        testRemoveItem1();
        mOrderService.loadCurrentOrder(new TestResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {

                mOrderService.updateQuantity((NcrOrderItem) data.getItems().get(0),
                        1,
                        new TestResultCallback<NcrOrderItem>() {
                            @Override
                            public void onSuccess(NcrOrderItem data) {
                                assertEqualDecimal("3.75", data.getPrice());
                                assertEqualDecimal("0", data.calculateAdditionalCharges());
                                assertEqualDecimal("3.75", data.getSubtotal());

                                mOrderService.loadCurrentOrder(new TestResultCallback<Order>() {
                                    @Override
                                    public void onSuccess(Order data) {
                                        assertEqualDecimal("11.09", data.getSubtotal());
                                    }
                                });
                            }
                        });
            }
        });
    }

    @Test
    public void testCheckout() throws InterruptedException {
        testUpdateQuantity();

        mockPostOrderCall("/test_ncr_order_ahead_order_post.json");
        mockCheckOrderStatusCall("/test_ncr_order_ahead_check_status.json");

        Semaphore waitForCheckout = new Semaphore(0);
        mOrderService.checkout(new TestResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                data.setUseServerSubtotal(true);
                assertEqualDecimal("11.09", data.getSubtotal());
                assertEqualDecimal("1.06", data.getTaxes());
                assertEqualDecimal("12.15", data.getTotalWithTip());

                verify(mTracerFactory).createTracer(AppConstants.CUSTOM_METRIC_ID_NCR_ORDER_BOUNCE);
                verify(mTracer).start();
                verify(mTracer).end();
                waitForCheckout.release();
            }
        });
        assertTrue(waitForCheckout.tryAcquire(10, TimeUnit.SECONDS));
    }

    @Test
    public void testCheckoutWithPreSelectedReward() throws InterruptedException {
        testUpdateQuantity();

        PreSelectedReward preSelectedReward = new PreSelectedReward();
        preSelectedReward.setRewardId(17);
        preSelectedReward.setRewardName("1$ off");
        mOrderService.setPreSelectedReward(preSelectedReward);

        mockPostOrderCall("/test_ncr_order_ahead_order_post.json");
        mockCheckOrderStatusCall("/test_ncr_order_ahead_check_status_applied_reward.json");

        Semaphore waitForCheckout = new Semaphore(0);
        mOrderService.checkout(new TestResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                data.setUseServerSubtotal(true);
                assertEqualDecimal("11.09", data.getSubtotal());
                assertEqualDecimal("1.06", data.getTaxes());
                assertEqualDecimal("12.15", data.getTotalWithTip());

                verify(mTracerFactory).createTracer(AppConstants.CUSTOM_METRIC_ID_NCR_ORDER_BOUNCE);
                verify(mTracer).start();
                verify(mTracer).end();

                assertEquals(1, data.getDiscountLines().size());
                DiscountLine discountLine = (DiscountLine) data.getDiscountLines().get(0);
                assertEquals(17, discountLine.getRewardId());
                assertEquals("1$ off the total", discountLine.getDiscountLine());

                waitForCheckout.release();
            }
        });
        assertTrue(waitForCheckout.tryAcquire(10, TimeUnit.SECONDS));

        ArgumentCaptor<NcrOrderWrappedData> orderArgumentCaptor = ArgumentCaptor.forClass(NcrOrderWrappedData.class);
        verify(mNcrWrapperApi).postOrder(anyString(), orderArgumentCaptor.capture());
        NcrOrderWrappedData ncrOrderWrappedData = orderArgumentCaptor.getValue();
        assertEquals(1, ncrOrderWrappedData.getRewardsToApply().size());
        assertEquals(17, (long) ncrOrderWrappedData.getRewardsToApply().get(0));
    }

    @Test
    public void testCheckoutWithPreSelectedRewardReturnsError() throws InterruptedException {
        testUpdateQuantity();

        PreSelectedReward preSelectedReward = new PreSelectedReward();
        preSelectedReward.setRewardId(20);
        preSelectedReward.setRewardName("Free bagel with purchase");
        mOrderService.setPreSelectedReward(preSelectedReward);

        mockPostOrderCall("/test_ncr_order_ahead_order_post_rewards_error.json");
        mockCheckOrderStatusCall("/test_ncr_order_ahead_check_status.json");

        Semaphore waitForCheckout = new Semaphore(0);
        mOrderService.checkout(new TestResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                data.setUseServerSubtotal(true);
                assertEqualDecimal("11.09", data.getSubtotal());
                assertEqualDecimal("1.06", data.getTaxes());
                assertEqualDecimal("12.15", data.getTotalWithTip());

                verify(mTracerFactory).createTracer(AppConstants.CUSTOM_METRIC_ID_NCR_ORDER_BOUNCE);
                verify(mTracer).start();
                verify(mTracer).end();

                assertTrue(data.getDiscountLines().isEmpty());
                assertEquals("Needs an extra product to be applied.", data.getRewardErrorMessage());

                waitForCheckout.release();
            }
        });
        assertTrue(waitForCheckout.tryAcquire(10, TimeUnit.SECONDS));

        ArgumentCaptor<NcrOrderWrappedData> orderArgumentCaptor = ArgumentCaptor.forClass(NcrOrderWrappedData.class);
        verify(mNcrWrapperApi).postOrder(anyString(), orderArgumentCaptor.capture());
        NcrOrderWrappedData ncrOrderWrappedData = orderArgumentCaptor.getValue();
        assertEquals(1, ncrOrderWrappedData.getRewardsToApply().size());
        assertEquals(20, (long) ncrOrderWrappedData.getRewardsToApply().get(0));
    }

    @Test
    public void testCheckoutWithNoPreSelectedWithAppliedRewards() throws InterruptedException {
        testCheckoutWithPreSelectedReward();
        reset(mNcrWrapperApi);
        setupNcrWrapperMocks();

        reset(mTracerFactory);
        reset(mTracer);
        setupTracerFactoryMocks();

        when(mNcrWrapperApi.putOrder(anyString(), anyString(), any())).thenReturn(mPutOrderCall);
        when(mNcrWrapperApi.getOrder(anyString())).thenReturn(mCheckOrderStatusCall);

        mockPutOrderCall("/test_ncr_order_ahead_order_post.json");
        mockCheckOrderStatusCall("/test_ncr_order_ahead_check_status.json");
        mOrderService.setPreSelectedReward(null);

        Semaphore waitForCheckout = new Semaphore(0);
        mOrderService.checkout(new TestResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                data.setUseServerSubtotal(true);
                assertEqualDecimal("11.09", data.getSubtotal());
                assertEqualDecimal("1.06", data.getTaxes());
                assertEqualDecimal("12.15", data.getTotalWithTip());

                verify(mTracerFactory).createTracer(AppConstants.CUSTOM_METRIC_ID_NCR_ORDER_BOUNCE);
                verify(mTracer).start();
                verify(mTracer).end();

                assertTrue(data.getDiscountLines().isEmpty());
                waitForCheckout.release();
            }
        });
        assertTrue(waitForCheckout.tryAcquire(10, TimeUnit.SECONDS));

        ArgumentCaptor<NcrOrderWrappedData> orderArgumentCaptor = ArgumentCaptor.forClass(NcrOrderWrappedData.class);
        verify(mNcrWrapperApi).putOrder(any(), anyString(), orderArgumentCaptor.capture());
        NcrOrderWrappedData ncrOrderWrappedData = orderArgumentCaptor.getValue();
        assertTrue(ncrOrderWrappedData.getRewardsToApply().isEmpty());
    }

    @Test
    public void testApplyRewardAfterCheckout() throws InterruptedException {
        testCheckout();

        reset(mNcrWrapperApi);
        setupNcrWrapperMocks();

        reset(mTracerFactory);
        reset(mTracer);
        setupTracerFactoryMocks();

        mockPutOrderCall("/test_ncr_order_ahead_check_status_applied_reward.json");
        mockCheckOrderStatusCall("/test_ncr_order_ahead_check_status_applied_reward.json");

        Semaphore semaphore = new Semaphore(0);
        mOrderService.applyReward(17, new TestResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                assertEquals(1, data.getDiscountLines().size());
                DiscountLine discountLine = (DiscountLine) data.getDiscountLines().get(0);
                assertEquals(17, discountLine.getRewardId());
                assertEquals("1$ off the total", discountLine.getDiscountLine());
                semaphore.release();
            }
        });

        assertTrue(semaphore.tryAcquire(10, TimeUnit.SECONDS));

        ArgumentCaptor<NcrOrderWrappedData> orderArgumentCaptor = ArgumentCaptor.forClass(NcrOrderWrappedData.class);
        verify(mNcrWrapperApi).putOrder(any(), anyString(), orderArgumentCaptor.capture());
        NcrOrderWrappedData ncrOrderWrappedData = orderArgumentCaptor.getValue();
        assertEquals(1, ncrOrderWrappedData.getRewardsToApply().size());
    }

    @Test
    public void testClearRewardAtCheckout() throws InterruptedException {
        testCheckoutWithPreSelectedReward();

        reset(mNcrWrapperApi);
        setupNcrWrapperMocks();

        reset(mTracerFactory);
        reset(mTracer);
        setupTracerFactoryMocks();

        mockPutOrderCall("/test_ncr_order_ahead_check_status.json");
        mockCheckOrderStatusCall("/test_ncr_order_ahead_check_status.json");

        Semaphore semaphore = new Semaphore(0);
        mOrderService.clearReward(new TestResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                assertTrue(data.getDiscountLines().isEmpty());
                assertNull(data.getPreSelectedReward());
                semaphore.release();
            }
        });

        assertTrue(semaphore.tryAcquire(10, TimeUnit.SECONDS));

        ArgumentCaptor<NcrOrderWrappedData> orderArgumentCaptor = ArgumentCaptor.forClass(NcrOrderWrappedData.class);
        verify(mNcrWrapperApi).putOrder(any(), anyString(), orderArgumentCaptor.capture());
        NcrOrderWrappedData ncrOrderWrappedData = orderArgumentCaptor.getValue();
        assertTrue(ncrOrderWrappedData.getRewardsToApply().isEmpty());
    }

    @Test
    public void testUpdateCustomization() throws InterruptedException {
        testCheckout();

        mOrderService.loadCurrentOrder(new TestResultCallback<Order>() {
            @Override
            public void onSuccess(Order order) {
                NcrOrderItem farmhouseItem = (NcrOrderItem) order.getItems().get(1);
                farmhouseItem.getCustomizations().clear();

                // Bagel - Spinach Florentine Bagel
                ItemCustomizationHelper.addCustomization(farmhouseItem, "8636", "mod:4833", "mod:4833");

                // Eggs - One Egg
                ItemCustomizationHelper.addCustomization(farmhouseItem, "8363", "mod:4771", "mod:4771");

                // Sides - Chips
                ItemCustomizationHelper.addCustomization(farmhouseItem, "8560", "mod:4779", "mod:4779");

                mOrderService.updateItem(farmhouseItem, new TestResultCallback<NcrOrderItem>() {
                    @Override
                    public void onSuccess(NcrOrderItem updatedItem) {
                        assertEqualDecimal("5.99", updatedItem.getPrice());
                        assertEqualDecimal("1.65", updatedItem.calculateAdditionalCharges());
                        assertEqualDecimal("7.64", updatedItem.getSubtotal());
                    }
                });
            }
        });
    }

    @Test
    public void testCheckoutAfterCustomizationUpdates() throws InterruptedException {
        testUpdateCustomization();

        mockPutOrderCall("/test_ncr_order_ahead_order_put.json");
        mockCheckOrderStatusCall("/test_ncr_order_ahead_check_status_2.json");

        Semaphore waitForCheckout = new Semaphore(0);
        mOrderService.checkout(new TestResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                data.setUseServerSubtotal(true);
                assertEqualDecimal("11.39", data.getSubtotal());
                assertEqualDecimal("1.09", data.getTaxes());
                assertEqualDecimal("12.48", data.getTotalWithTip());
                waitForCheckout.release();
            }
        });
        assertTrue(waitForCheckout.tryAcquire(10, TimeUnit.SECONDS));
    }

    @Test
    public void testOrderAheadEligibleRewards() throws InterruptedException {
        RewardsData rewardsData = new RewardsData();
        Map<Integer, CmsReward> rewardMap = rewardsData.getRewardsContent();
        addCmsRewardData(rewardMap, 1, 10);
        addCmsRewardData(rewardMap, 2, null);
        addCmsRewardData(rewardMap, 3, 11);

        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        mOrderService.loadEligibleRewards(rewardsData, new ResultCallback<List<OmsMobileEligibleReward>>() {
            @Override
            public void onSuccess(List<OmsMobileEligibleReward> data) {
                assertEquals(2, data.size());

                OmsMobileEligibleReward reward1 = null;
                OmsMobileEligibleReward reward3 = null;
                for (OmsMobileEligibleReward omsMobileEligibleReward : data) {
                    if (omsMobileEligibleReward.getWalletId().equals("1")) {
                        reward1 = omsMobileEligibleReward;
                    } else if (omsMobileEligibleReward.getWalletId().equals("3")) {
                        reward3 = omsMobileEligibleReward;
                    }
                }
                assertNotNull(reward1);
                assertNotNull(reward3);
                semaphore.release();
            }

            @Override
            public void onFail(int errorCode, String errorMessage) {
                Assert.fail();
                semaphore.release();
            }

            @Override
            public void onError(Throwable error) {
                Assert.fail();
                semaphore.release();
            }
        });
        assertTrue(semaphore.tryAcquire(10, TimeUnit.SECONDS));
    }

    private void addCmsRewardData(Map<Integer, CmsReward> rewardMap, int rewardId, Integer ruleId) {
        CmsReward cmsReward = new CmsReward();
        cmsReward.setRewardId(rewardId);
        cmsReward.setRuleId(ruleId);
        rewardMap.put(cmsReward.getRewardId(), cmsReward);
    }

    @Test
    public void testLoadApplicableRewardBanner() throws InterruptedException, IOException {
        testCheckout();
        List<CmsReward> applicableRewards = new ArrayList<>();
        {
            CmsReward cmsReward = new CmsReward();
            cmsReward.setRewardId(1);
            cmsReward.setHeading("This reward gets you a free bagel");
            applicableRewards.add(cmsReward);
        }

        {
            CmsReward cmsReward = new CmsReward();
            cmsReward.setRewardId(2);
            cmsReward.setHeading("Free coffee with purchase");
            applicableRewards.add(cmsReward);
        }

        MockitoUtil.mockEnqueueWithObject(mApplicableRewardsCall, applicableRewards);
        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        mOrderService.loadOrderBanner(new ResultCallback<RewardBannerModel>() {
            @Override
            public void onSuccess(RewardBannerModel data) {
                assertEquals(1, data.getRewardId());
                assertEquals("This reward gets you a free bagel", data.getDescription());
                semaphore.release();
            }

            @Override
            public void onFail(int errorCode, String errorMessage) {
                Assert.fail();
                semaphore.release();
            }

            @Override
            public void onError(Throwable error) {
                Assert.fail();
                semaphore.release();
            }
        });

        assertTrue(semaphore.tryAcquire(10, TimeUnit.SECONDS));
    }

    private CurbsidePickupData buildCulCurbsidePickupData() {
        return new CurbsidePickupData("Ford", "white", "sedan");
    }


    @Test
    public void testIamHereCurbsideNotYetInPickupTimeWindow() {
        DateTime pickupTime = new DateTime(2020, 11, 15, 11, 30);
        CurbsideOrderData curbsideOrderData = new CurbsideOrderData("21301l", pickupTime, "1902", buildCulCurbsidePickupData(), false, NcrOrderStatus.Finished);
        when(mUserServices.getCurbsideOrderData()).thenReturn(curbsideOrderData);
        when(mClock.getCurrentDateTime()).thenReturn(new DateTime(2020, 11, 15, 11, 00));

        assertFalse(mOrderService.shouldDisplayCurbsideIamHere());
    }

    @Test
    public void testIamHereCurbsideInPickupTimeWindowBeforePickupTime() {
        DateTime pickupTime = new DateTime(2020, 11, 15, 11, 30);
        CurbsideOrderData curbsideOrderData = new CurbsideOrderData("21301l", pickupTime, "1902", buildCulCurbsidePickupData(), false, NcrOrderStatus.Finished);
        when(mUserServices.getCurbsideOrderData()).thenReturn(curbsideOrderData);
        when(mClock.getCurrentDateTime()).thenReturn(new DateTime(2020, 11, 15, 11, 28));

        mOrderService = new NcrOrderServiceImpl(mClock, mUserServices, mMenuDataService, mNcrWrapperApi, mSettingsServices, mTracerFactory);
        assertTrue(mOrderService.shouldDisplayCurbsideIamHere());
    }

    @Test
    public void testIamHereCurbsideInPickupTimeWindowAfterPickupTime() {
        DateTime pickupTime = new DateTime(2020, 11, 15, 11, 30, 0, 0);
        CurbsideOrderData curbsideOrderData = new CurbsideOrderData("21301l", pickupTime, "1902", buildCulCurbsidePickupData(), false, NcrOrderStatus.Finished);
        when(mUserServices.getCurbsideOrderData()).thenReturn(curbsideOrderData);
        when(mClock.getCurrentDateTime()).thenReturn(new DateTime(2020, 11, 15, 11, 34));

        mOrderService = new NcrOrderServiceImpl(mClock, mUserServices, mMenuDataService, mNcrWrapperApi, mSettingsServices, mTracerFactory);
        assertTrue(mOrderService.shouldDisplayCurbsideIamHere());
    }

    @Test
    public void testIamHereCurbsideNotInPickupTimeWindowAfterPickupTime() {
        DateTime pickupTime = new DateTime(2020, 11, 15, 11, 30, 0, 0);
        CurbsideOrderData curbsideOrderData = new CurbsideOrderData("21301l", pickupTime, "1902", buildCulCurbsidePickupData(), false, NcrOrderStatus.Finished);
        when(mUserServices.getCurbsideOrderData()).thenReturn(curbsideOrderData);
        when(mClock.getCurrentDateTime()).thenReturn(new DateTime(2020, 11, 15, 11, 44));

        assertFalse(mOrderService.shouldDisplayCurbsideIamHere());
    }

    @Test
    public void testSendCurbsideIamHere() throws IOException {
        CurbsideOrderData curbsideOrderData = new CurbsideOrderData("232232l", mClock.getCurrentDateTime(), "3432", buildCulCurbsidePickupData(), false, NcrOrderStatus.Finished);
        when(mUserServices.getCurbsideOrderData()).thenReturn(curbsideOrderData);

        mOrderService.sendCurbsideIamHereSignal(new TestResultCallback<NcrCurbsideIamHere>() {
            @Override
            public void onSuccess(NcrCurbsideIamHere data) {
                verify(mSendCurbsideIamHereCall).enqueue(any());
                verify(mUserServices).saveCurbsideLocationPhone(null);
                verify(mUserServices).saveCurbsideLocationMessage(null);
                verify(mUserServices).saveCurbsideOrderData(null);
            }
        });
    }

    private void mockPostOrderCall(String jsonResponseFileName) {
        MockitoUtil.mockEnqueue(mPostOrderCall, jsonResponseFileName, NcrOrderWrappedData.class);
    }

    private void mockPutOrderCall(String jsonResponseFileName) {
        MockitoUtil.mockEnqueue(mPutOrderCall, jsonResponseFileName, NcrOrderWrappedData.class);
    }

    private void mockCheckOrderStatusCall(String jsonResponseFileName) {
        MockitoUtil.mockExecute(mCheckOrderStatusCall, jsonResponseFileName, NcrOrderWrappedData.class);
    }

    private void assertEqualDecimal(String sDecimal, BigDecimal decimal) {
        assertEquals(new BigDecimal(sDecimal).stripTrailingZeros(),
                decimal.stripTrailingZeros());
    }

}
