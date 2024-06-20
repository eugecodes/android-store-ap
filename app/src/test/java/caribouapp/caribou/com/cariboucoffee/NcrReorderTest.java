package caribouapp.caribou.com.cariboucoffee;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static caribouapp.caribou.com.cariboucoffee.util.MockitoUtil.mockEnqueue;
import static caribouapp.caribou.com.cariboucoffee.util.MockitoUtil.mockEnqueueArray;

import com.google.gson.reflect.TypeToken;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import caribouapp.caribou.com.cariboucoffee.analytics.TracerFactory;
import caribouapp.caribou.com.cariboucoffee.api.CmsApi;
import caribouapp.caribou.com.cariboucoffee.api.CmsOrderApi;
import caribouapp.caribou.com.cariboucoffee.api.NcrWrapperApi;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsMenu;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsStoreReward;
import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrSaleItem;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderWrappedData;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrReorderErrors;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemModifier;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ModifierGroup;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ncr.NcrOrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.NcrMenuDataServiceImpl;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model.ncr.NcrRecentOrderModel;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.order.ncr.NcrOrderServiceImpl;
import caribouapp.caribou.com.cariboucoffee.util.DefaultStoreHelper;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;
import caribouapp.caribou.com.cariboucoffee.util.MockitoUtil;
import retrofit2.Call;

@RunWith(MockitoJUnitRunner.class)
public class NcrReorderTest {


    OrderService mOrderService;
    NcrMenuDataServiceImpl mMenuDataService;
    Semaphore mWaitForReorderTest;

    @Mock
    private Clock mClock;

    @Mock
    private CmsApi mCmsApi;

    @Mock
    private NcrWrapperApi mNcrWrapperApi;

    @Mock
    private Call<NcrOrderWrappedData> mReOrderCall;

    @Mock
    private CmsOrderApi mCmsOrderApi;

    @Mock
    private Call<CmsMenu> mFilterableMenuCall;

    @Mock
    private Call<List<CmsStoreReward>> mCmsStoreRewardsCalls;

    @Mock
    private SettingsServices mSettingsServices;

    @Mock
    private TracerFactory mTracerFactory;

    @Mock
    private UserServices userServices;

    @Before
    public void setup() {
        mMenuDataService = new NcrMenuDataServiceImpl(mCmsApi, mCmsOrderApi, mSettingsServices);
        mOrderService = new NcrOrderServiceImpl(mClock, userServices, mMenuDataService, mNcrWrapperApi, mSettingsServices, mTracerFactory);
        when(mCmsOrderApi.getStoreMenu(anyString())).thenReturn(mFilterableMenuCall);
        mockEnqueue(mFilterableMenuCall, "/test_ncr_store_menu.json", CmsMenu.class);
        when(mNcrWrapperApi.reorder(anyString())).thenReturn(mReOrderCall);
        when(mCmsOrderApi.getLocationRewards(anyString())).thenReturn(mCmsStoreRewardsCalls);
        mockEnqueueArray(mCmsStoreRewardsCalls, "/test_ncr_get_store_rewards.json", new TypeToken<ArrayList<CmsStoreReward>>() {
        }.getType());
    }

    @Test
    public void testFarmHouseNoErrorReorder() throws InterruptedException {
        mockEnqueue(mReOrderCall, "/test_ncr_reorder_bagel.json", NcrOrderWrappedData.class);
        NcrRecentOrderModel recentOrderModel = new NcrRecentOrderModel();
        recentOrderModel.setCheckNumber("9999");
        mWaitForReorderTest = new Semaphore(0);
        mOrderService.reorder(recentOrderModel, DefaultStoreHelper.defaultStore(), new ResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                assertFalse(data.isErrorReplicatingItems());
                assertEquals(1, data.getItems().size());
                NcrOrderItem ncrOrderItem = (NcrOrderItem) data.getItems().get(0);
                NcrSaleItem ncrSaleItem = ncrOrderItem.getMenuData().getNcrOmsData().getSalesItems().get(0);
                assertEquals("Farmhouse", ncrSaleItem.getName());
                assertEquals("10062", ncrSaleItem.getProductId());
                assertEqualsItemCustomization("8647", "mod:8015", ncrOrderItem);
                assertEqualsItemCustomization("8692", "mod:7014", ncrOrderItem);
                assertEqualsItemCustomization("8690", "mod:6418", ncrOrderItem);
                mWaitForReorderTest.release();

            }

            @Override
            public void onFail(int errorCode, String errorMessage) {
                Assert.fail();
            }

            @Override
            public void onError(Throwable error) {
                Assert.fail();
            }
        });
        assertTrue(mWaitForReorderTest.tryAcquire(10, TimeUnit.SECONDS));
    }

    @Test
    public void testFarmHouseErrorReorder() throws InterruptedException {
        NcrOrderWrappedData ncrOrderWrappedData = GsonUtil.readObjectFromClasspath("/test_ncr_reorder_bagel.json", NcrOrderWrappedData.class);
        List<NcrReorderErrors> ncrReorderErrors = new ArrayList<>();
        ncrReorderErrors.add(new NcrReorderErrors());
        ncrOrderWrappedData.setReorderErrorsList(ncrReorderErrors);
        MockitoUtil.mockEnqueueWithObject(mReOrderCall, ncrOrderWrappedData);
        NcrRecentOrderModel recentOrderModel = new NcrRecentOrderModel();
        recentOrderModel.setCheckNumber("9999");
        mWaitForReorderTest = new Semaphore(0);
        mOrderService.reorder(recentOrderModel, DefaultStoreHelper.defaultStore(), new ResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                assertEquals(1, data.getItems().size());
                Assert.assertTrue(data.isErrorReplicatingItems());
                NcrSaleItem ncrSaleItem = ((NcrOrderItem) data.getItems().get(0)).getMenuData().getNcrOmsData().getSalesItems().get(0);
                assertEquals("Farmhouse", ncrSaleItem.getName());
                assertEquals("10062", ncrSaleItem.getProductId());
                mWaitForReorderTest.release();
            }

            @Override
            public void onFail(int errorCode, String errorMessage) {
                Assert.fail();
            }

            @Override
            public void onError(Throwable error) {
                Assert.fail();
            }
        });
        assertTrue(mWaitForReorderTest.tryAcquire(10, TimeUnit.SECONDS));
    }

    @Test
    public void testDualLargeDrinkCustomizationReorder() throws InterruptedException {
        mockEnqueue(mReOrderCall, "/test_ncr_reorder_drink.json", NcrOrderWrappedData.class);
        NcrRecentOrderModel recentOrderModel = new NcrRecentOrderModel();
        recentOrderModel.setCheckNumber("9999");
        mWaitForReorderTest = new Semaphore(0);
        mOrderService.reorder(recentOrderModel, DefaultStoreHelper.defaultStore(), new ResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                assertEquals(2, data.getItems().size());
                assertFalse(data.isErrorReplicatingItems());
                NcrOrderItem ncrOrderItem = (NcrOrderItem) data.getItems().get(0);
                assertEqualsItemCustomization("8730", "mod:10426", ncrOrderItem);
                assertEqualsItemCustomization("8640", "mod:8010", ncrOrderItem);
                assertEquals(SizeEnum.LARGE, ncrOrderItem.getSize());
                mWaitForReorderTest.release();
                assertEquals("Hot Chocolate", ncrOrderItem.getMenuData().getName());

            }

            @Override
            public void onFail(int errorCode, String errorMessage) {
                Assert.fail();
            }

            @Override
            public void onError(Throwable error) {
                Assert.fail();
            }
        });
        assertTrue(mWaitForReorderTest.tryAcquire(10, TimeUnit.SECONDS));
    }

    @Test
    public void testFreeLimitCoffee_given5LargeLimit2And1SmallLimit1_whenReorder_then2LargeAreAdded() throws InterruptedException {
        mockEnqueue(mFilterableMenuCall, "/test_ncr_store_menu_quantitylimit_one_small_two_large.json", CmsMenu.class);

        mockEnqueue(mReOrderCall, "/test_ncr_reorder_drink_five_large_and_three_small.json", NcrOrderWrappedData.class);
        NcrRecentOrderModel recentOrderModel = new NcrRecentOrderModel();
        recentOrderModel.setCheckNumber("9999");
        mWaitForReorderTest = new Semaphore(0);
        mOrderService.reorder(recentOrderModel, DefaultStoreHelper.defaultStore(), new ResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                assertEquals(1, data.getItems().size());
                assertFalse(data.isErrorReplicatingItems());
                assertTrue(data.isErrorMaxQuantityHasChanged());
                NcrOrderItem item1 = (NcrOrderItem) data.getItems().get(0);
                assertEqualsItemCustomization("8730", "mod:10426", item1);
                assertEqualsItemCustomization("8640", "mod:8010", item1);
                assertEquals(2, item1.getQuantity());
                assertEquals("Hot Chocolate", item1.getMenuData().getName());
                assertEquals(SizeEnum.LARGE, item1.getSize());
                mWaitForReorderTest.release();
            }

            @Override
            public void onFail(int errorCode, String errorMessage) {
                Assert.fail();
            }

            @Override
            public void onError(Throwable error) {
                Assert.fail();
            }
        });
        assertTrue(mWaitForReorderTest.tryAcquire(10, TimeUnit.SECONDS));
    }

    @Test
    public void testFreeLimitCoffee_given3SmallLimit1And3SmallLimit1_whenReorder_then1SmallAdded() throws InterruptedException {
        mockEnqueue(mFilterableMenuCall, "/test_ncr_store_menu_quantitylimit_one_small_two_large.json", CmsMenu.class);

        mockEnqueue(mReOrderCall, "/test_ncr_reorder_drink_three_small_and_three_small.json", NcrOrderWrappedData.class);
        NcrRecentOrderModel recentOrderModel = new NcrRecentOrderModel();
        recentOrderModel.setCheckNumber("9999");
        mWaitForReorderTest = new Semaphore(0);
        mOrderService.reorder(recentOrderModel, DefaultStoreHelper.defaultStore(), new ResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                assertEquals(1, data.getItems().size());
                assertTrue(data.isErrorMaxQuantityHasChanged());
                assertFalse(data.isErrorReplicatingItems());
                NcrOrderItem item1 = (NcrOrderItem) data.getItems().get(0);
                assertEqualsItemCustomization("8728", "mod:10426", item1);
                assertEqualsItemCustomization("8640", "mod:8010", item1);
                assertEquals(1, item1.getQuantity());
                assertEquals(SizeEnum.SMALL, item1.getSize());
                assertEquals("Hot Chocolate", item1.getMenuData().getName());

                mWaitForReorderTest.release();
            }

            @Override
            public void onFail(int errorCode, String errorMessage) {
                Assert.fail();
            }

            @Override
            public void onError(Throwable error) {
                Assert.fail();
            }
        });
        assertTrue(mWaitForReorderTest.tryAcquire(10, TimeUnit.SECONDS));
    }

    @Test
    public void testFreeLimitCoffee_given2LargeLimit2_whenReorder_then2LargeAdded() throws InterruptedException {
        mockEnqueue(mFilterableMenuCall, "/test_ncr_store_menu_quantitylimit_one_small_two_large.json", CmsMenu.class);

        mockEnqueue(mReOrderCall, "/test_ncr_reorder_drink_two_large.json", NcrOrderWrappedData.class);
        NcrRecentOrderModel recentOrderModel = new NcrRecentOrderModel();
        recentOrderModel.setCheckNumber("9999");
        mWaitForReorderTest = new Semaphore(0);
        mOrderService.reorder(recentOrderModel, DefaultStoreHelper.defaultStore(), new ResultCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                assertEquals(1, data.getItems().size());
                assertTrue(data.isErrorMaxQuantityHasChanged());
                assertFalse(data.isErrorReplicatingItems());
                NcrOrderItem item1 = (NcrOrderItem) data.getItems().get(0);
                assertEqualsItemCustomization("8730", "mod:10426", item1);
                assertEqualsItemCustomization("8640", "mod:8010", item1);
                assertEquals(2, item1.getQuantity());
                assertEquals(SizeEnum.LARGE, item1.getSize());
                assertEquals("Hot Chocolate", item1.getMenuData().getName());

                mWaitForReorderTest.release();
            }

            @Override
            public void onFail(int errorCode, String errorMessage) {
                Assert.fail();
            }

            @Override
            public void onError(Throwable error) {
                Assert.fail();
            }
        });
        assertTrue(mWaitForReorderTest.tryAcquire(10, TimeUnit.SECONDS));
    }

    private void assertEqualsItemCustomization(String modifierGroupId, String itemOptionId, NcrOrderItem ncrOrderItem) {
        ModifierGroup modifierGroup = ncrOrderItem.getModifierGroupById(modifierGroupId);
        Map<ItemModifier, ItemOption> itemModifierItemModifierMap = ncrOrderItem.getCustomizationForGroup(modifierGroup);
        List<ItemOption> itemOptionList = new ArrayList<>(itemModifierItemModifierMap.values());
        //ItemOption doesn't have productId so we have compare against id we search with the productId from the reorder drink json
        assertEquals(itemOptionId, itemOptionList.get(0).getId());
    }
}
