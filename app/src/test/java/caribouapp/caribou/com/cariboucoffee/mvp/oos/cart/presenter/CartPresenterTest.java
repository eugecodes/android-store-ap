package caribouapp.caribou.com.cariboucoffee.mvp.oos.cart.presenter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrOmsData;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ncr.NcrOrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.cart.CartContract;
import caribouapp.caribou.com.cariboucoffee.order.OrderService;
import caribouapp.caribou.com.cariboucoffee.order.ncr.NcrOrder;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;

@RunWith(MockitoJUnitRunner.class)
public class CartPresenterTest {

    private static NcrOmsData FREE_COFFEE_LIMIT;

    private MenuCardItemModel mMenuCardItemModel;
    @Mock
    private CartContract.View mView;
    @Mock
    private OrderService mOrderService;
    @Mock
    private AppDataStorage mAppDataStorage;
    @Mock
    private EventLogger mEventLogger;
    @Mock
    private SettingsServices mSettingsServices;
    @Mock
    private Clock mClock;
    @Mock
    private StoreLocation mStoreLocation;

    private CartPresenter subject;

    @BeforeClass
    public static void beforeClass() {
        FREE_COFFEE_LIMIT = GsonUtil.readObjectFromClasspath("/test_beverage_ncr_free_limit_coffee.json", NcrOmsData.class);
    }

    @AfterClass
    public static void afterClass() {
        FREE_COFFEE_LIMIT = null;
    }

    @Before
    public void setUp() {
        when(mSettingsServices.getBulkPrepTimeInMins()).thenReturn(20);
        when(mSettingsServices.getZeroTotalOrderErrorMessage()).thenReturn("Zero Total Order error");
        when(mView.isActive()).thenReturn(true);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0, Runnable.class);
            runnable.run();
            return null;
        }).when(mView).runOnUiThread(any());

        subject = new CartPresenter(mView);
        subject.setOrderService(mOrderService);
        subject.setAppDataStorage(mAppDataStorage);
        subject.setEventLogger(mEventLogger);
        subject.setSettingsServices(mSettingsServices);
        subject.setClock(mClock);

        mMenuCardItemModel = new MenuCardItemModel(mSettingsServices);
    }

    @After
    public void tearDown() {
        subject = null;
    }

    @Test
    public void givenErrorMaxQuantityHasChangedTrue_thenShowMaxQuantityHasChangedDialog() {

        NcrOrder ncrOrder = new NcrOrder(null);
        ncrOrder.setErrorMaxQuantityHasChanged(true);

        assertTrue(ncrOrder.isMaxQuantityHasChangedDialogEnable());
        assertTrue(ncrOrder.shouldShowMaxQuantityHasChangedDialog());

        subject.setOrder(ncrOrder);

        assertFalse(ncrOrder.isMaxQuantityHasChangedDialogEnable());
        assertFalse(ncrOrder.shouldShowMaxQuantityHasChangedDialog());

        verify(mView).showMaxQuantityHasChangedDialog();
    }

    @Test
    public void givenErrorMaxQuantityHasChangedFalse_thenNotShowMaxQuantityHasChangedDialog() {

        NcrOrder ncrOrder = new NcrOrder(null);

        assertTrue(ncrOrder.isMaxQuantityHasChangedDialogEnable());
        assertFalse(ncrOrder.shouldShowMaxQuantityHasChangedDialog());

        subject.setOrder(ncrOrder);

        assertFalse(ncrOrder.isMaxQuantityHasChangedDialogEnable());
        assertFalse(ncrOrder.shouldShowMaxQuantityHasChangedDialog());
        verify(mView, never()).showMaxQuantityHasChangedDialog();
    }

    @Test
    public void givenOrderWithOneItemIsCreated_whenCheckout_thenOrderWillCheckOut() {

        doAnswer(invocation -> {
            ResultCallback resultCallback = invocation.getArgument(0, ResultCallback.class);
            resultCallback.onSuccess(buildOrderWithPrizedItem("id", 1, SizeEnum.MEDIUM, BigDecimal.TEN));
            return null;
        }).when(mOrderService).loadCurrentOrder(any());

        subject.checkout();

        verify(mView, never()).showMaxQuantityHasChangedDialog();
        verify(mOrderService).checkout(any());
    }


    @Test
    public void givenOrderWithOneFreeItemIsCreated_whenCheckout_thenOrderWillNotCheckOut() {

        doAnswer(invocation -> {
            ResultCallback resultCallback = invocation.getArgument(0, ResultCallback.class);
            resultCallback.onSuccess(buildOrderWithFreeItem("id", 1, SizeEnum.MEDIUM));
            return null;
        }).when(mOrderService).loadCurrentOrder(any());

        subject.checkout();

        verify(mView).showFreeItemsOnlyNotAllowedDialog(anyString());
        verify(mOrderService, never()).checkout(any());
    }

    @Test
    public void givenOrderWithOnePrizedItemIsCreated_whenCheckout_thenOrderWillCheckOut() {

        doAnswer(invocation -> {
            ResultCallback resultCallback = invocation.getArgument(0, ResultCallback.class);
            resultCallback.onSuccess(buildOrderWithPrizedItem("id", 1, SizeEnum.MEDIUM, BigDecimal.TEN));
            return null;
        }).when(mOrderService).loadCurrentOrder(any());

        subject.checkout();

        verify(mView, never()).showFreeItemsOnlyNotAllowedDialog(anyString());
        verify(mOrderService).checkout(any());
    }

    @Test
    public void givenOrderWithPrizedAndFreeItemsIsCreated_whenCheckout_thenOrderWillNotCheckOut() {

        doAnswer(invocation -> {
            ResultCallback resultCallback = invocation.getArgument(0, ResultCallback.class);
            resultCallback.onSuccess(buildOrderWithTwoItems("id1", 1, SizeEnum.SMALL, BigDecimal.ONE, "id2", 1, SizeEnum.SMALL, BigDecimal.ONE));
            return null;
        }).when(mOrderService).loadCurrentOrder(any());

        subject.checkout();

        verify(mView, never()).showQuantityLimitDialog();
        verify(mOrderService).checkout(any());
    }

    @Test
    public void givenOrderWithOnlyFreeItemsIsCreated_whenCheckout_thenOrderWillNotCheckOut() {

        doAnswer(invocation -> {
            ResultCallback resultCallback = invocation.getArgument(0, ResultCallback.class);
            resultCallback.onSuccess(buildOrderWithTwoItems("id1", 1, SizeEnum.SMALL, BigDecimal.ZERO, "id2", 1, SizeEnum.SMALL, BigDecimal.ZERO));
            return null;
        }).when(mOrderService).loadCurrentOrder(any());

        subject.checkout();

        verify(mView).showFreeItemsOnlyNotAllowedDialog(anyString());
        verify(mOrderService, never()).checkout(any());
    }

    private NcrOrder buildOrderWithPrizedItem(String id, int quantity, SizeEnum size, BigDecimal price) {
        NcrOrder order = new NcrOrder(mStoreLocation);
        order.addItem(buildNcrOrderItem(id, quantity, size, price));
        return order;
    }

    private NcrOrder buildOrderWithFreeItem(String id, int quantity, SizeEnum size) {
        NcrOrder order = new NcrOrder(mStoreLocation);
        order.addItem(buildNcrOrderItem(id, quantity, size, BigDecimal.ZERO));
        return order;
    }

    private NcrOrderItem buildNcrOrderItem(String id, int quantity, SizeEnum size, BigDecimal price) {
        NcrOrderItem orderItem = new NcrOrderItem(mMenuCardItemModel);
        orderItem.setId(id);
        orderItem.setQuantity(quantity);
        orderItem.loadModifiers(FREE_COFFEE_LIMIT);
        orderItem.setSize(size);
        orderItem.setPrice(price);
        return orderItem;
    }

    private NcrOrder buildOrderWithTwoItems(
            String id1, int quantity1, SizeEnum size1, BigDecimal price1,
            String id2, int quantity2, SizeEnum size2, BigDecimal price) {
        NcrOrder order = new NcrOrder(mStoreLocation);
        order.addItem(buildNcrOrderItem(id1, quantity1, size1, price1));
        order.addItem(buildNcrOrderItem(id2, quantity2, size2, price));
        return order;
    }
}
