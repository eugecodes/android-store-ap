package caribouapp.caribou.com.cariboucoffee;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.Intent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.mvp.menu.view.MenuActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OrderNavHelper;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.cart.view.CartActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.pickup.view.PickupTypeActivity;


@RunWith(RobolectricTestRunner.class)
public class OrderNavHelperTest {

    @Mock
    StoreLocation mStoreLocation;

    @Mock
    SettingsServices mSettingsServices;

    @Mock
    RewardItemModel rewardItemModel;

    private Context mContext = RuntimeEnvironment.application;

    private OrderNavHelper mOrderNavHelper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mOrderNavHelper = new OrderNavHelper(mSettingsServices);
        mOrderNavHelper = spy(mOrderNavHelper);
    }

    @Test
    public void testCreateIntentStartOrderWhenRequiresPickupTypeSelection() {
        doReturn(true).when(mOrderNavHelper).requiresPickupData(any());

        Intent resultIntent = mOrderNavHelper.createIntentStartOrder(mContext, mStoreLocation, MenuActivity.MenuOrigin.CART, rewardItemModel);

        Intent expectedIntent = new Intent(mContext, PickupTypeActivity.class);
        assertEquals(expectedIntent.getComponent(), resultIntent.getComponent());
    }

    @Test
    public void testCreateIntentStartOrderWhenNoRequiresPickupTypeSelection() {
        doReturn(false).when(mOrderNavHelper).requiresPickupData(any());

        Intent resultIntent = mOrderNavHelper.createIntentStartOrder(mContext, mStoreLocation, MenuActivity.MenuOrigin.CART, rewardItemModel);

        Intent expectedIntent = new Intent(mContext, MenuActivity.class);
        assertEquals(expectedIntent.getComponent(), resultIntent.getComponent());
    }

    @Test
    public void testCreateIntentReOrderWhenRequiresPickupTypeSelection() {
        doReturn(true).when(mOrderNavHelper).requiresPickupData(any());

        Intent resultIntent = mOrderNavHelper.createIntentReOrder(mContext, mStoreLocation);

        Intent expectedIntent = new Intent(mContext, PickupTypeActivity.class);
        assertEquals(expectedIntent.getComponent(), resultIntent.getComponent());
    }

    @Test
    public void testCreateIntentReOrderWhenNoRequiresPickupTypeSelection() {
        doReturn(false).when(mOrderNavHelper).requiresPickupData(any());

        Intent resultIntent = mOrderNavHelper.createIntentReOrder(mContext, mStoreLocation);

        Intent expectedIntent = new Intent(mContext, CartActivity.class);
        assertEquals(expectedIntent.getComponent(), resultIntent.getComponent());
    }


    @Test
    public void testCreateIntentContinueOrderWhenRequiresPickupTypeSelection() {
        doReturn(true).when(mOrderNavHelper).requiresPickupData(any());

        Intent resultIntent = mOrderNavHelper.createIntentContinueOrder(mContext, mStoreLocation);

        Intent expectedIntent = new Intent(mContext, PickupTypeActivity.class);
        assertEquals(expectedIntent.getComponent(), resultIntent.getComponent());
    }

    @Test
    public void testCreateIntentContinueOrderWhenNoRequiresPickupTypeSelection() {
        doReturn(false).when(mOrderNavHelper).requiresPickupData(any());

        Intent resultIntent = mOrderNavHelper.createIntentContinueOrder(mContext, mStoreLocation);

        Intent expectedIntent = new Intent(mContext, CartActivity.class);
        assertEquals(expectedIntent.getComponent(), resultIntent.getComponent());
    }

    @Test
    public void testRequiresPickupDataIsFalseWhenPickupTypeSelectionDisabled() {
        when(mSettingsServices.isPickupTypeSelectionEnabled()).thenReturn(false);
        Boolean requiresPickupData = mOrderNavHelper.requiresPickupData(mStoreLocation);
        Assert.assertFalse(requiresPickupData);
    }

    @Test
    public void testRequiresPickupDataIsFalseWhenPickupTypeCollectionIsEmpty() {
        when(mSettingsServices.isPickupTypeSelectionEnabled()).thenReturn(true);
        when(mStoreLocation.getPickupTypes()).thenReturn(Collections.emptyList());
        Boolean requiresPickupData = mOrderNavHelper.requiresPickupData(mStoreLocation);
        Assert.assertFalse(requiresPickupData);
    }

    @Test
    public void testRequiresPickupDataIsFalseWhenPickupTypeCollectionHasOnlyWalkIn() {
        when(mSettingsServices.isPickupTypeSelectionEnabled()).thenReturn(true);
        List<YextPickupType> yextPickupTypes = new ArrayList<>();
        yextPickupTypes.add(YextPickupType.WalkIn);
        when(mStoreLocation.getPickupTypes()).thenReturn(yextPickupTypes);
        Boolean requiresPickupData = mOrderNavHelper.requiresPickupData(mStoreLocation);
        Assert.assertFalse(requiresPickupData);
    }

    @Test
    public void testRequiresPickupDataIsTrueWhenPickupTypeCollectionHasOnlyCurbside() {
        when(mSettingsServices.isPickupTypeSelectionEnabled()).thenReturn(true);
        List<YextPickupType> yextPickupTypes = new ArrayList<>();
        yextPickupTypes.add(YextPickupType.Curbside);
        when(mStoreLocation.getPickupTypes()).thenReturn(yextPickupTypes);
        Boolean requiresPickupData = mOrderNavHelper.requiresPickupData(mStoreLocation);
        Assert.assertTrue(requiresPickupData);
    }

    @Test
    public void testRequiresPickupDataIsTrueWhenPickupTypeCollectionHasCurbsideAndDriveThru() {
        when(mSettingsServices.isPickupTypeSelectionEnabled()).thenReturn(true);
        List<YextPickupType> yextPickupTypes = new ArrayList<>();
        yextPickupTypes.add(YextPickupType.Curbside);
        yextPickupTypes.add(YextPickupType.DriveThru);
        when(mStoreLocation.getPickupTypes()).thenReturn(yextPickupTypes);
        Boolean requiresPickupData = mOrderNavHelper.requiresPickupData(mStoreLocation);
        Assert.assertTrue(requiresPickupData);
    }

    @Test
    public void testRequiresPickupDataIsTrueWhenPickupTypeCollectionHasOtherTypes() {
        when(mSettingsServices.isPickupTypeSelectionEnabled()).thenReturn(true);
        List<YextPickupType> yextPickupTypes = new ArrayList<>();
        yextPickupTypes.add(YextPickupType.Curbside);
        yextPickupTypes.add(YextPickupType.DriveThru);
        yextPickupTypes.add(YextPickupType.Delivery);
        yextPickupTypes.add(YextPickupType.WalkIn);
        when(mStoreLocation.getPickupTypes()).thenReturn(yextPickupTypes);
        Boolean requiresPickupData = mOrderNavHelper.requiresPickupData(mStoreLocation);
        Assert.assertTrue(requiresPickupData);
    }
}
