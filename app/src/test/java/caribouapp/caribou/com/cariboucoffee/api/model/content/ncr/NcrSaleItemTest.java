package caribouapp.caribou.com.cariboucoffee.api.model.content.ncr;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;

import static caribouapp.caribou.com.cariboucoffee.AppConstants.ORDER_AHEAD_MAX_ITEM_QUANTITY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NcrSaleItemTest {

    private NcrSaleItem subject;

    @Before
    public void setUp() {
        subject = new NcrSaleItem();
    }

    @After
    public void tearDown() {
        subject = null;
    }

    @Test
    public void test_givenNoLimit_whenGetQuantityLimit_thenReturnORDER_AHEAD_MAX_ITEM_QUANTITY() {
        assertEquals(ORDER_AHEAD_MAX_ITEM_QUANTITY, subject.getQuantityLimit().intValue());
    }

    @Test
    public void test_givenLimitSet_whenGetQuantityLimit_thenReturnNewLimit() {
        subject = GsonUtil.readObjectFromClasspath("/ncrsaleitem_free_coffee_with_purchase.json", NcrSaleItem.class);
        assertEquals(1, subject.getQuantityLimit().intValue());
    }

    @Test
    public void test_givenNullProductId_whenFindGroupAndItemByProductId_thenReturnNull() {
        assertNull(subject.findGroupAndItemByProductId(null));
    }

    @Test
    public void test_givenNullProductId_whenFindGroupAndItemByProductId_thenXXYY() {
        subject.findGroupAndItemByProductId(null);
    }
}
