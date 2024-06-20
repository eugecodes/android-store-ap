package caribouapp.caribou.com.cariboucoffee.order.ncr;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrOmsData;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ncr.NcrOrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NcrOrderTest {

    private static NcrOmsData FREE_COFFEE_LIMIT;
    private MenuCardItemModel mMenuCardItemModel;

    @Mock
    private SettingsServices mSettingsServices;
    @Mock
    private StoreLocation mStoreLocation;

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
        mMenuCardItemModel = new MenuCardItemModel(mSettingsServices);
        mMenuCardItemModel.setOmsProdIdForCurrentLocation("218371298");
        mMenuCardItemModel.setName("Free Medium Roast Coffee");
    }

    @After
    public void tearDown() {
        mMenuCardItemModel = null;
    }


    @Test
    public void givenValidOrderWithOneItemAndAddingValidItem_whenOrderItemQuantityWillNotPassMax_thenReturnTrue() {
        assertFalse(buildOrderWithItem("id1", 1, SizeEnum.SMALL)
                .orderItemPassesMaxQuantityCheck(
                        buildNcrOrderItem("id2", 1, SizeEnum.SMALL), 1));
    }

    @Test
    public void givenValidOrderWithOneItemAndAddingNotValidItem_whenOrderItemQuantityWillNotPassMax_thenReturnFalse() {
        assertTrue(
                buildOrderWithItem("id1", 1, SizeEnum.SMALL)
                        .orderItemPassesMaxQuantityCheck(
                                buildNcrOrderItem("id2", 2, SizeEnum.SMALL), 2));
    }

    @Test
    public void givenValidOrderWithOneItemAndAddingDifferentValidItem_whenOrderItemQuantityWillNotPassMax_thenReturnFalse() {
        assertTrue(buildOrderWithItem("id1", 1, SizeEnum.MEDIUM)
                .orderItemPassesMaxQuantityCheck(
                        buildNcrOrderItem("id2", 1, SizeEnum.SMALL), 1));
    }

    @Test
    public void givenValidOrderWithTwoAndAddingValidItem_whenOrderItemQuantityWillNotPassMax_thenReturnFalse() {
        assertTrue(buildOrderWithTwoItems("id1", 1, SizeEnum.SMALL, "id2", 1, SizeEnum.SMALL)
                .orderItemPassesMaxQuantityCheck(
                        buildNcrOrderItem("id3", 2, SizeEnum.SMALL), 1));
    }

    @Test
    public void givenValidOrderWithTwoAndAddingNotValidItem_whenOrderItemQuantityWillNotPassMax_thenReturnFalse() {
        assertTrue(buildOrderWithTwoItems("id1", 1, SizeEnum.SMALL, "id2", 1, SizeEnum.SMALL)
                .orderItemPassesMaxQuantityCheck(
                        buildNcrOrderItem("id3", 2, SizeEnum.SMALL), 2));
    }


    @Test
    public void givenValidOrderWithOneItem_whenValidateOrderItemQuantity_thenReturnTrue() {
        assertTrue(buildOrderWithItem("id1", 1, SizeEnum.MEDIUM).validateOrderItemQuantity());
    }

    @Test
    public void givenAnotherValidOrderWithOneItem_whenValidateOrderItemQuantity_thenReturnTrue() {
        assertTrue(buildOrderWithItem("id1", 2, SizeEnum.SMALL).validateOrderItemQuantity());
    }

    @Test
    public void givenNotValidOrderWithOneItem_whenValidateOrderItemQuantity_thenReturnFalse() {
        assertFalse(buildOrderWithItem("id1", 3, SizeEnum.SMALL).validateOrderItemQuantity());
    }

    @Test
    public void givenAnotherNotValidOrderWithOneItem_whenValidateOrderItemQuantity_thenReturnFalse() {
        assertFalse(buildOrderWithItem("id1", 2, SizeEnum.LARGE).validateOrderItemQuantity());
    }

    @Test
    public void givenValidOrderWithTwoItems_whenValidateOrderItemQuantity_thenReturnTrue() {
        assertTrue(buildOrderWithTwoItems("id1", 1, SizeEnum.SMALL, "id2", 1, SizeEnum.SMALL)
                .validateOrderItemQuantity());
    }

    @Test
    public void givenNotValidOrderWithTwoItems_whenValidateOrderItemQuantity_thenReturnFalse() {
        assertFalse(buildOrderWithTwoItems("id1", 1, SizeEnum.SMALL, "id2", 1, SizeEnum.MEDIUM)
                .validateOrderItemQuantity());
    }

    @Test
    public void givenAnotherNotValidOrderWithTwoItems_whenValidateOrderItemQuantity_thenReturnFalse() {
        assertFalse(buildOrderWithTwoItems("id1", 1, SizeEnum.LARGE, "id2", 1, SizeEnum.SMALL)
                .validateOrderItemQuantity());
    }


    private NcrOrderItem buildNcrOrderItem(String id, int quantity, SizeEnum size) {
        NcrOrderItem orderItem = new NcrOrderItem(mMenuCardItemModel);
        orderItem.setId(id);
        orderItem.setQuantity(quantity);
        orderItem.loadModifiers(FREE_COFFEE_LIMIT);
        orderItem.setSize(size);
        return orderItem;
    }

    private NcrOrder buildOrderWithItem(String id, int quantity, SizeEnum size) {
        NcrOrder order = new NcrOrder(mStoreLocation);
        order.addItem(buildNcrOrderItem(id, quantity, size));
        return order;
    }

    private NcrOrder buildOrderWithTwoItems(
            String id1, int quantity1, SizeEnum size1,
            String id2, int quantity2, SizeEnum size2) {
        NcrOrder order = new NcrOrder(mStoreLocation);
        order.addItem(buildNcrOrderItem(id1, quantity1, size1));
        order.addItem(buildNcrOrderItem(id2, quantity2, size2));
        return order;
    }
}
