package caribouapp.caribou.com.cariboucoffee;

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.MenuCardItemModel;

@RunWith(MockitoJUnitRunner.class)
public class MenuCardItemModelTest {

    @Mock
    private SettingsServices mSettingsServices;

    @Test
    public void testBulkNoPrepTime() {
        when(mSettingsServices.getBulkPrepTimeInMins()).thenReturn(20);
        MenuCardItemModel menuCardItemModel = new MenuCardItemModel(mSettingsServices);
        menuCardItemModel.setPrepTimeInMins(null);
        Assert.assertFalse(menuCardItemModel.isBulk());
    }

    @Test
    public void testBulkWithExactPrepTime() {
        when(mSettingsServices.getBulkPrepTimeInMins()).thenReturn(20);
        MenuCardItemModel menuCardItemModel = new MenuCardItemModel(mSettingsServices);
        menuCardItemModel.setPrepTimeInMins(20);
        Assert.assertTrue(menuCardItemModel.isBulk());
    }

    @Test
    public void testBulkWithLargerPrepTime() {
        when(mSettingsServices.getBulkPrepTimeInMins()).thenReturn(20);
        MenuCardItemModel menuCardItemModel = new MenuCardItemModel(mSettingsServices);
        menuCardItemModel.setPrepTimeInMins(30);
        Assert.assertTrue(menuCardItemModel.isBulk());
    }

    @Test
    public void testNotBulkWithPrepTime() {
        when(mSettingsServices.getBulkPrepTimeInMins()).thenReturn(20);
        MenuCardItemModel menuCardItemModel = new MenuCardItemModel(mSettingsServices);
        menuCardItemModel.setPrepTimeInMins(10);
        Assert.assertFalse(menuCardItemModel.isBulk());
    }
}
