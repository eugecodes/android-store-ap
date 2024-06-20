package caribouapp.caribou.com.cariboucoffee;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import com.google.gson.reflect.TypeToken;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import caribouapp.caribou.com.cariboucoffee.api.CmsApi;
import caribouapp.caribou.com.cariboucoffee.api.CmsOrderApi;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsMenu;
import caribouapp.caribou.com.cariboucoffee.api.model.content.CmsStoreReward;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCategory;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.MenuData;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.MenuDataServiceImpl;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.service.NcrMenuDataServiceImpl;
import caribouapp.caribou.com.cariboucoffee.util.AppUtils;
import caribouapp.caribou.com.cariboucoffee.util.MockitoUtil;
import retrofit2.Call;

@RunWith(MockitoJUnitRunner.class)
public class NcrMenuServiceTest {

    MenuDataServiceImpl mMenuDataService;
    @Mock
    private CmsApi mCmsApi;
    @Mock
    private CmsOrderApi mCmsOrderApi;
    @Mock
    private Call<CmsMenu> mCallFilterableMenu;
    @Mock
    private SettingsServices mSettingsServices;

    @Mock
    private Call<List<CmsStoreReward>> mCmsStoreRewardsCalls;

    @Before
    public void setup() {
        mMenuDataService = new NcrMenuDataServiceImpl(mCmsApi, mCmsOrderApi, mSettingsServices);
        when(mCmsOrderApi.getStoreMenu("9920")).thenReturn(mCallFilterableMenu);
        MockitoUtil.mockEnqueue(mCallFilterableMenu, "/test_ncr_store_menu.json", CmsMenu.class);
        when(mCmsOrderApi.getLocationRewards(anyString())).thenReturn(mCmsStoreRewardsCalls);
        MockitoUtil.mockEnqueueArray(mCmsStoreRewardsCalls, "/test_ncr_get_store_rewards.json", new TypeToken<ArrayList<CmsStoreReward>>() {
        }.getType());
    }

    @Test
    public void testBuildMenuWithReward() throws InterruptedException {
        mMenuDataService.clearCache();
        Semaphore semaphore = new Semaphore(0);
        mMenuDataService.getOrderAheadMenuDataFiltered("9920", AppUtils.isProductionBuild() ? "55" : "555", new ResultCallback<MenuData>() {
            @Override
            public void onSuccess(MenuData menuData) {
                Assert.assertEquals(7, menuData.getMenuProductDataByOmsProdId().size());
                Assert.assertNotNull(menuData.getMenuProductDataByOmsProdId().get("2480"));
                Assert.assertNotNull(menuData.getMenuProductDataByOmsProdId().get("2481"));
                Assert.assertNotNull(menuData.getMenuProductDataByOmsProdId().get("2479"));
                Assert.assertNotNull(menuData.getMenuProductDataByOmsProdId().get("2483"));
                Assert.assertNotNull(menuData.getMenuProductDataByOmsProdId().get("2484"));
                Assert.assertNotNull(menuData.getMenuProductDataByOmsProdId().get("2485"));
                Assert.assertNotNull(menuData.getMenuProductDataByOmsProdId().get("2504"));
                semaphore.release();
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
        assertTrue(semaphore.tryAcquire(10, TimeUnit.SECONDS));
    }

    @Test
    public void testBuildMenuWithEmptyReward() throws InterruptedException {
        mMenuDataService.clearCache();
        Semaphore semaphore = new Semaphore(0);
        mMenuDataService.getOrderAheadMenuDataFiltered("9920", "1", new ResultCallback<MenuData>() {
            @Override
            public void onSuccess(MenuData menuData) {
                Assert.assertTrue(menuData.getMenuProductDataByOmsProdId().isEmpty());
                semaphore.release();
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
        assertTrue(semaphore.tryAcquire(10, TimeUnit.SECONDS));
    }

    @Test
    public void testBuildMenuEmptyCategories() {
        mMenuDataService.clearCache();
        mMenuDataService.getOrderAheadMenuDataFiltered("9920", null, new ResultCallback<MenuData>() {
            @Override
            public void onSuccess(MenuData menuData) {
                for (MenuCategory menuCategory : menuData.getCategoryByName("Food").getSubCategories()) {
                    Assert.assertNotEquals("Bagels and Shmear", menuCategory.getName());
                }
            }

            @Override
            public void onFail(int errorCode, String errorMessage) {

            }

            @Override
            public void onError(Throwable error) {

            }
        });
    }
}
