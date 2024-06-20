package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.presenter;

import static junit.framework.Assert.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrOmsData;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.ItemContract;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ItemOption;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ncr.NcrOrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.presenter.ncr.NcrItemCustomizationPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.order.ncr.NcrOrder;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;
import caribouapp.caribou.com.cariboucoffee.util.ItemCustomizationHelper;

@RunWith(MockitoJUnitRunner.class)
public class ItemCustomizationPresenterUnitTest {

    private static NcrOmsData FREE_COFFEE_LIMIT;

    private MenuCardItemModel mMenuCardItemModel;
    private OrderItem mOrderItem;
    private AtomicInteger mCounter;
    @Mock
    private ItemContract.View mView;
    @Mock
    private SettingsServices mSettingsServices;
    @Mock
    private StoreLocation mStoreLocation;
    @Mock
    private OrderService mOrderService;
    @Mock
    private EventLogger mEventLogger;

    @BeforeClass
    public static void beforeClass(){
        FREE_COFFEE_LIMIT = GsonUtil.readObjectFromClasspath("/test_beverage_ncr_free_limit_coffee.json", NcrOmsData.class);
    }

    @AfterClass
    public static void afterClass(){
        FREE_COFFEE_LIMIT = null;
    }

    @Before
    public void before() {
        mMenuCardItemModel = new MenuCardItemModel(mSettingsServices);
        mCounter = new AtomicInteger();

        when(mView.isActive()).thenReturn(true);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0, Runnable.class);
            runnable.run();
            return null;
        }).when(mView).runOnUiThread(any());
    }

    @Test
    public void testNutritionAllergensAvailabilityIsChecked() {
        mOrderItem = new NcrOrderItem(mMenuCardItemModel);
        ItemCustomizationPresenter presenter = new NcrItemCustomizationPresenter(mView);
        presenter.setSettingsServices(mSettingsServices);

        doAnswer(invocation -> mCounter.incrementAndGet()).when(mView).onNoNutritionAllergensAvailable();

        presenter.setModel(mOrderItem, false);

        assertThat(mCounter).hasValue(1);
    }

    @Test
    public void testItemCustomizationChangingSize() {
        MenuCardItemModel menuCardItemModel = new MenuCardItemModel(mSettingsServices);
        menuCardItemModel.getOmsProdIdSet().add("4");
        menuCardItemModel.setName("Prod1");
        OrderItem orderItem = new NcrOrderItem(menuCardItemModel);
        orderItem.loadModifiers(GsonUtil.readObjectFromClasspath("/test_beverage_ncr_sizes_latte_3_sizes.json", NcrOmsData.class));
        ItemCustomizationHelper.addCustomization(orderItem, "8614", "mod:4326", "mod:4326");
        ItemCustomizationHelper.addCustomization(orderItem, "8615", "mod:4671", "mod:4671");
        ItemCustomizationHelper.addCustomization(orderItem, "8186", "mod:4675", "mod:4675");
        orderItem.setSize(SizeEnum.LARGE);
        Map<String, Map<String, ItemOption>> customization = orderItem.getCustomizations();
        assertTrue(customization.containsKey("8614"));
        assertTrue(customization.get("8614").containsKey("mod:4326"));
        assertEquals(customization.get("8614").get("mod:4326").getId(), ("mod:4326"));

        assertTrue(customization.containsKey("8615"));
        assertTrue(customization.get("8615").containsKey("mod:4671"));
        assertEquals(customization.get("8615").get("mod:4671").getId(), ("mod:4671"));

        assertTrue(customization.containsKey("8186"));
        assertTrue(customization.get("8186").containsKey("mod:4675"));
        assertEquals(customization.get("8186").get("mod:4675").getId(), ("mod:4675"));

    }

    @Test
    public void testThreeSizesBeverage() {
        mMenuCardItemModel.setOmsProdIdForCurrentLocation("218371298");
        mOrderItem = new NcrOrderItem(mMenuCardItemModel);

        doAnswer(invocation -> {
            ResultCallback resultCallback = invocation.getArgument(1, ResultCallback.class);
            resultCallback.onSuccess(GsonUtil.readObjectFromClasspath("/test_beverage_ncr_sizes_latte_3_sizes.json", NcrOmsData.class));
            return null;
        }).when(mOrderService).getProductCustomizations(anyString(), any());


        ItemCustomizationPresenter presenter = new NcrItemCustomizationPresenter(mView);
        presenter.setOrderService(mOrderService);
        presenter.setSettingsServices(mSettingsServices);
        presenter.setModel(mOrderItem, true);

        Set<SizeEnum> availableSizes = new HashSet<>();
        availableSizes.add(SizeEnum.SMALL);
        availableSizes.add(SizeEnum.MEDIUM);
        availableSizes.add(SizeEnum.LARGE);

        verify(mView, times(1)).updateSize(SizeEnum.SMALL, availableSizes);
    }

    @Test
    public void testTwoSizesBeverage() {
        mMenuCardItemModel.setOmsProdIdForCurrentLocation("218371298");
        mOrderItem = new NcrOrderItem(mMenuCardItemModel);

        doAnswer(invocation -> {
            ResultCallback resultCallback = invocation.getArgument(1, ResultCallback.class);
            resultCallback.onSuccess(GsonUtil.readObjectFromClasspath("/test_beverage_ncr_sizes_latte_2_sizes.json", NcrOmsData.class));
            return null;
        }).when(mOrderService).getProductCustomizations(anyString(), any());


        ItemCustomizationPresenter presenter = new NcrItemCustomizationPresenter(mView);
        presenter.setOrderService(mOrderService);
        presenter.setSettingsServices(mSettingsServices);
        presenter.setModel(mOrderItem, true);

        Set<SizeEnum> availableSizes = new HashSet<>();
        availableSizes.add(SizeEnum.MEDIUM);
        availableSizes.add(SizeEnum.LARGE);

        verify(mView, times(1)).updateSize(SizeEnum.MEDIUM, availableSizes);
    }

    @Test
    public void testOneSizesBeverage() {
        mMenuCardItemModel.setOmsProdIdForCurrentLocation("218371298");
        mOrderItem = new NcrOrderItem(mMenuCardItemModel);

        doAnswer(invocation -> {
            ResultCallback resultCallback = invocation.getArgument(1, ResultCallback.class);
            resultCallback.onSuccess(GsonUtil.readObjectFromClasspath("/test_beverage_ncr_sizes_latte_1_size.json", NcrOmsData.class));
            return null;
        }).when(mOrderService).getProductCustomizations(anyString(), any());

        ItemCustomizationPresenter presenter = new NcrItemCustomizationPresenter(mView);
        presenter.setOrderService(mOrderService);
        presenter.setSettingsServices(mSettingsServices);
        presenter.setModel(mOrderItem, true);

        Set<SizeEnum> availableSizes = new HashSet<>();
        availableSizes.add(SizeEnum.ONE_SIZE);

        verify(mView, times(1)).updateSize(SizeEnum.ONE_SIZE, availableSizes);
    }

    @Test
    public void givenQuantitySetGreaterThanLimit_whenUpdateQuantities_thenLimitIsSet() {
        mMenuCardItemModel.setOmsProdIdForCurrentLocation("218371298");
        mOrderItem = new NcrOrderItem(mMenuCardItemModel);

        doAnswer(invocation -> {
            ResultCallback resultCallback = invocation.getArgument(1, ResultCallback.class);
            resultCallback.onSuccess(FREE_COFFEE_LIMIT);
            return null;
        }).when(mOrderService).getProductCustomizations(anyString(), any());

        ItemCustomizationPresenter presenter = new NcrItemCustomizationPresenter(mView);
        presenter.setOrderService(mOrderService);
        presenter.setSettingsServices(mSettingsServices);
        presenter.setModel(mOrderItem, true);

        mOrderItem.setSize(SizeEnum.MEDIUM);
        mOrderItem.setQuantity(2);

        presenter.updateQuantities();

        assertEquals(1, mOrderItem.getQuantity());

        verify(mView, times(2)).updateQuantity(mOrderItem);
    }

    @Test
    public void givenQuantityIsLessThanLimit_whenUpdateQuantities_thenQuantityIsSet() {
        mMenuCardItemModel.setOmsProdIdForCurrentLocation("218371298");
        mOrderItem = new NcrOrderItem(mMenuCardItemModel);

        doAnswer(invocation -> {
            ResultCallback resultCallback = invocation.getArgument(1, ResultCallback.class);
            resultCallback.onSuccess(FREE_COFFEE_LIMIT);
            return null;
        }).when(mOrderService).getProductCustomizations(anyString(), any());

        ItemCustomizationPresenter presenter = new NcrItemCustomizationPresenter(mView);
        presenter.setOrderService(mOrderService);
        presenter.setSettingsServices(mSettingsServices);
        presenter.setModel(mOrderItem, true);

        mOrderItem.setSize(SizeEnum.SMALL);
        mOrderItem.setQuantity(1);

        presenter.updateQuantities();

        assertEquals(1, mOrderItem.getQuantity());

        verify(mView, times(2)).updateQuantity(mOrderItem);
    }

    @Test
    public void givenQuantityIsEqualThanLimit_whenUpdateQuantities_thenQuantityIsSet() {
        mMenuCardItemModel.setOmsProdIdForCurrentLocation("218371298");
        mOrderItem = new NcrOrderItem(mMenuCardItemModel);

        doAnswer(invocation -> {
            ResultCallback resultCallback = invocation.getArgument(1, ResultCallback.class);
            resultCallback.onSuccess(FREE_COFFEE_LIMIT);
            return null;
        }).when(mOrderService).getProductCustomizations(anyString(), any());

        ItemCustomizationPresenter presenter = new NcrItemCustomizationPresenter(mView);
        presenter.setOrderService(mOrderService);
        presenter.setSettingsServices(mSettingsServices);
        presenter.setModel(mOrderItem, true);

        mOrderItem.setSize(SizeEnum.SMALL);
        mOrderItem.setQuantity(2);

        presenter.updateQuantities();

        assertEquals(2, mOrderItem.getQuantity());

        verify(mView, times(2)).updateQuantity(mOrderItem);
    }

    @Test
    public void givenOtherItemAddedWithQuantityLimit_whenAddItemToCart_thenItemIsNotSaved() {
        mMenuCardItemModel.setOmsProdIdForCurrentLocation("218371298");
        mMenuCardItemModel.setName("Free Medium Roast Coffee");
        mOrderItem = new NcrOrderItem(mMenuCardItemModel);
        mOrderItem.setId("id1");

        doAnswer(invocation -> {
            ResultCallback resultCallback = invocation.getArgument(1, ResultCallback.class);
            resultCallback.onSuccess(FREE_COFFEE_LIMIT);
            return null;
        }).when(mOrderService).getProductCustomizations(anyString(), any());

        ItemCustomizationPresenter presenter = new NcrItemCustomizationPresenter(mView);
        presenter.setOrderService(mOrderService);
        presenter.setSettingsServices(mSettingsServices);
        presenter.setModel(mOrderItem, true);

        NcrOrderItem orderItem1 = new NcrOrderItem(mMenuCardItemModel);
        orderItem1.setId("id2");
        orderItem1.loadModifiers(FREE_COFFEE_LIMIT);
        NcrOrder mOrder = new NcrOrder(mStoreLocation);
        mOrder.addItem(orderItem1);
        presenter.setOrder(mOrder);

        mOrderItem.setSize(SizeEnum.MEDIUM);
        mOrderItem.setQuantity(1);

        presenter.addItemToCart();

        verify(mOrderService, times(0)).addItem(eq(mOrderItem), any());
        verify(mOrderService, times(0)).updateItem(eq(mOrderItem), any());
        verify(mView, times(1)).showQuantityLimitDialog();
        verify(mEventLogger, times(0)).logAddToCart();
    }

    @Test
    public void givenQuantityIsLessOrEqualThanLimit_whenAddItemToCart_thenItemIsNotSaved() {
        mMenuCardItemModel.setOmsProdIdForCurrentLocation("218371298");
        mOrderItem = new NcrOrderItem(mMenuCardItemModel);

        doAnswer(invocation -> {
            ResultCallback resultCallback = invocation.getArgument(1, ResultCallback.class);
            resultCallback.onSuccess(FREE_COFFEE_LIMIT);
            return null;
        }).when(mOrderService).getProductCustomizations(anyString(), any());

        doAnswer(invocation -> {
            ResultCallback callback = invocation.getArgument(1, ResultCallback.class);
            callback.onSuccess(null);
            return null;
        }).when(mOrderService).addItem(eq(mOrderItem), any());

        ItemCustomizationPresenter presenter = new NcrItemCustomizationPresenter(mView);
        presenter.setOrderService(mOrderService);
        presenter.setSettingsServices(mSettingsServices);
        presenter.setEventLogger(mEventLogger);
        presenter.setModel(mOrderItem, true);

        presenter.setOrder(new NcrOrder(mStoreLocation));

        mOrderItem.setSize(SizeEnum.MEDIUM);
        mOrderItem.setQuantity(1);

        presenter.addItemToCart();

        verify(mOrderService, times(1)).addItem(eq(mOrderItem), any());
        verify(mOrderService, times(0)).updateItem(eq(mOrderItem), any());
        verify(mView, times(0)).showQuantityLimitDialog();
        verify(mEventLogger, times(1)).logAddToCart();
    }
}
