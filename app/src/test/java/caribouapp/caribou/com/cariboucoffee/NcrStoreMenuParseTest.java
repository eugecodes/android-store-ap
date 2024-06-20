package caribouapp.caribou.com.cariboucoffee;

import static caribouapp.caribou.com.cariboucoffee.util.StringUtils.fromClasspathResource;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;

import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsMenu;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsMenuProduct;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;

public class NcrStoreMenuParseTest {
    @Test
    public void testStoreMenuStore2192Farmhouse() {
        CmsMenu cmsMenu = GsonUtil.readObjectFromClasspath("/test_ncr_store_menu.json", CmsMenu.class);

        try {
            CmsMenuProduct cmsMenuProductAvocado = cmsMenu.getCategories().get(0).getGroups().get(0).getProducts().get(0);

            JSONAssert.assertEquals(fromClasspathResource("/test_ncr_store_menu_omsdata_avocado.json"),
                    GsonUtil.defaultGson().toJson(cmsMenuProductAvocado.getOmsData()), false);


        } catch (IOException | JSONException e) {
            Assert.fail(e.getMessage());
        }
    }
}
