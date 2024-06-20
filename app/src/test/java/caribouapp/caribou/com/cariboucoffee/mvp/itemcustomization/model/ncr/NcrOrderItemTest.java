package caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ncr;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrCustomField;
import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrOmsData;
import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrSaleItem;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;
import caribouapp.caribou.com.cariboucoffee.order.ncr.NcrOrder;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NcrOrderItemTest {

    private static NcrOmsData FREE_COFFEE_LIMIT;

    @BeforeClass
    public static void beforeClass() {
        FREE_COFFEE_LIMIT = GsonUtil.readObjectFromClasspath("/test_beverage_ncr_free_limit_coffee.json", NcrOmsData.class);
    }

    @AfterClass
    public static void afterClass() {
        FREE_COFFEE_LIMIT = null;
    }

    @Mock
    private SettingsServices mSettingsServices;

    private MenuCardItemModel mMenuCardItemModel;

    private NcrOrderItem subject;

    @Before
    public void before() {
        when(mSettingsServices.getBulkPrepTimeInMins()).thenReturn(20);
        mMenuCardItemModel = new MenuCardItemModel(mSettingsServices);
        mMenuCardItemModel.setOmsProdIdForCurrentLocation("218371298");
        mMenuCardItemModel.setName("Free Medium Roast Coffee");
        subject = new NcrOrderItem(mMenuCardItemModel);
    }

    @After
    public void tearDown() {
        subject = null;
        mMenuCardItemModel = null;
    }

    @Test
    public void givenQuantityLessThanLimit_whenSetQuantity_thenReturnTrueAndChangeQuantity() {
        assertTrue(subject.setQuantity(10));
        assertEquals(10, subject.getQuantity());
    }

    @Test
    public void givenQuantityMoreThanLimit_whenSetQuantity_thenReturnFalseAndNotChangeQuantity() {
        assertFalse(subject.setQuantity(51));
        assertEquals(1, subject.getQuantity());
    }

    @Test
    public void givenMaxQuantityNotSet_whenGetMaxQuantity_thenReturnORDER_AHEAD_MAX_ITEM_QUANTITY() {
        assertEquals(AppConstants.ORDER_AHEAD_MAX_ITEM_QUANTITY, subject.getMaxQuantity());
    }

    @Test
    public void givenMaxQuantitySet_whenGetMaxQuantity_thenReturnNewValue() {
        setQuantityLimit(subject, 23);
        assertEquals(23, subject.getMaxQuantity());
    }

    @Test
    public void givenQuantityEqualThanMax_whenGetQuantityLessThanMax_thenReturnNewValue() {
        setQuantityLimit(subject, 21);
        subject.setQuantity(21);
        assertEquals(21, subject.getQuantityLessThanMax());
    }

    @Test
    public void givenQuantityLessThanMax_whenGetQuantityLessThanMax_thenReturnNewValue() {
        setQuantityLimit(subject, 23);
        subject.setQuantity(10);
        assertEquals(10, subject.getQuantityLessThanMax());
    }

    @Test
    public void givenQuantityGreaterThanMax_whenGetQuantityLessThanMax_thenReturnNewValue() {
        subject.setQuantity(49);
        setQuantityLimit(subject, 23);
        assertEquals(23, subject.getQuantityLessThanMax());
    }

    private void setQuantityLimit(NcrOrderItem orderItem, int maxQuantity) {
        HashMap<SizeEnum, NcrSaleItem> perSizeNcrSalesItems = new HashMap<>();
        NcrSaleItem ncrSaleItem = new NcrSaleItem();
        List<NcrCustomField> customFields = new ArrayList<>();
        customFields.add(new NcrCustomField("quantityLimit", String.valueOf(maxQuantity)));
        ncrSaleItem.setCustomFields(customFields);
        perSizeNcrSalesItems.put(SizeEnum.ONE_SIZE, ncrSaleItem);
        orderItem.setPerSizeNcrSalesItems(perSizeNcrSalesItems);
        orderItem.setSize(SizeEnum.ONE_SIZE);
    }

    @Test
    public void givenSMALLandQuantityOne_whenGetMaxQuantity_thenReturn2() {
        subject = buildNcrOrderItem("id1", 1, SizeEnum.SMALL);
        assertEquals(2, subject.getMaxQuantity());
    }

    @Test
    public void givenMEDIUMandQuantityOne_whenGetMaxQuantity_thenReturn1() {
        subject = buildNcrOrderItem("id1", 2, SizeEnum.MEDIUM);
        assertEquals(1, subject.getMaxQuantity());
    }

    @Test
    public void givenLARGEandQuantityOne_whenGetMaxQuantity_thenReturn1() {
        subject = buildNcrOrderItem("id1", 2, SizeEnum.LARGE);
        assertEquals(1, subject.getMaxQuantity());
    }

    @Test
    public void givenSMALLandQuantityOne_whenGetQuantityLessThanMax2_thenReturn2() {
        subject = buildNcrOrderItem("id1", 3, SizeEnum.SMALL);
        assertEquals(2, subject.getMaxQuantity());
    }

    @Test
    public void givenMEDIUMandQuantityOne_whenGetQuantityLessThanMax2_thenReturn1() {
        subject = buildNcrOrderItem("id1", 3, SizeEnum.MEDIUM);
        assertEquals(1, subject.getMaxQuantity());
    }

    @Test
    public void givenLARGEandQuantityOne_whenGetQuantityLessThanMax2_thenReturn1() {
        subject = buildNcrOrderItem("id1", 3, SizeEnum.LARGE);
        assertEquals(1, subject.getMaxQuantity());
    }

    private NcrOrderItem buildNcrOrderItem(String id, int quantity, SizeEnum size) {
        NcrOrderItem orderItem = new NcrOrderItem(mMenuCardItemModel);
        orderItem.setId(id);
        orderItem.setQuantity(quantity);
        orderItem.loadModifiers(FREE_COFFEE_LIMIT);
        orderItem.setSize(size);
        return orderItem;
    }
}
