package caribouapp.caribou.com.cariboucoffee;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextGetLocationResult;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextLocation;
import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextPickupType;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.util.GsonUtil;

public class YextPickUpTypeParsingTest {

    YextLocation mYextLocation;
    @Before
    public void setup() {
        YextGetLocationResult yextLocationResult = GsonUtil.readObjectFromClasspath("/test_yext_store_open_hours_get_location.json", YextGetLocationResult.class);
        mYextLocation = yextLocationResult.getResponse();
    }

    @Test
    public void testParsingAllPickUpTypes() {
        List<YextPickupType> yextPickupTypes = new ArrayList<>();
        yextPickupTypes.add(YextPickupType.Curbside);
        yextPickupTypes.add(YextPickupType.DriveThru);
        yextPickupTypes.add(YextPickupType.WalkIn);
        mYextLocation.setOrderPickUp(yextPickupTypes);
        StoreLocation storeLocation = new StoreLocation(mYextLocation, null);
        Assert.assertEquals(3, storeLocation.getPickupTypes().size());
        Assert.assertTrue(storeLocation.getPickupTypes().contains(YextPickupType.Curbside));
        Assert.assertTrue(storeLocation.getPickupTypes().contains(YextPickupType.WalkIn));
        Assert.assertTrue(storeLocation.getPickupTypes().contains(YextPickupType.DriveThru));
    }

    @Test
    public void testParsingCurbsideAndDriveThruPickupType () {
        List<YextPickupType> yextPickupTypes = new ArrayList<>();
        yextPickupTypes.add(YextPickupType.Curbside);
        yextPickupTypes.add(YextPickupType.DriveThru);
        mYextLocation.setOrderPickUp(yextPickupTypes);
        StoreLocation storeLocation = new StoreLocation(mYextLocation, null);
        Assert.assertEquals(2, storeLocation.getPickupTypes().size());
        Assert.assertTrue(storeLocation.getPickupTypes().contains(YextPickupType.Curbside));
        Assert.assertFalse(storeLocation.getPickupTypes().contains(YextPickupType.WalkIn));
        Assert.assertTrue(storeLocation.getPickupTypes().contains(YextPickupType.DriveThru));
    }

    @Test
    public void testParsingWalkInPickupType () {
        List<YextPickupType> yextPickupTypes = new ArrayList<>();
        yextPickupTypes.add(YextPickupType.WalkIn);
        mYextLocation.setOrderPickUp(yextPickupTypes);
        StoreLocation storeLocation = new StoreLocation(mYextLocation, null);
        Assert.assertEquals(1, storeLocation.getPickupTypes().size());
        Assert.assertFalse(storeLocation.getPickupTypes().contains(YextPickupType.Curbside));
        Assert.assertTrue(storeLocation.getPickupTypes().contains(YextPickupType.WalkIn));
        Assert.assertFalse(storeLocation.getPickupTypes().contains(YextPickupType.DriveThru));
    }

    @Test
    public void testParsingCurbsidePickupType () {
        List<YextPickupType> yextPickupTypes = new ArrayList<>();
        yextPickupTypes.add(YextPickupType.Curbside);
        mYextLocation.setOrderPickUp(yextPickupTypes);
        StoreLocation storeLocation = new StoreLocation(mYextLocation, null);
        Assert.assertEquals(1, storeLocation.getPickupTypes().size());
        Assert.assertTrue(storeLocation.getPickupTypes().contains(YextPickupType.Curbside));
        Assert.assertFalse(storeLocation.getPickupTypes().contains(YextPickupType.WalkIn));
        Assert.assertFalse(storeLocation.getPickupTypes().contains(YextPickupType.DriveThru));
    }

    @Test
    public void testParsingDriveThruPickupType () {
        List<YextPickupType> yextPickupTypes = new ArrayList<>();
        yextPickupTypes.add(YextPickupType.DriveThru);
        mYextLocation.setOrderPickUp(yextPickupTypes);
        StoreLocation storeLocation = new StoreLocation(mYextLocation, null);
        Assert.assertEquals(1, storeLocation.getPickupTypes().size());
        Assert.assertFalse(storeLocation.getPickupTypes().contains(YextPickupType.Curbside));
        Assert.assertFalse(storeLocation.getPickupTypes().contains(YextPickupType.WalkIn));
        Assert.assertTrue(storeLocation.getPickupTypes().contains(YextPickupType.DriveThru));
    }

    @Test
    public void testParsingEmptyPickUpTypes () {
        List<YextPickupType> yextPickupTypes = new ArrayList<>();
        mYextLocation.setOrderPickUp(yextPickupTypes);
        StoreLocation storeLocation = new StoreLocation(mYextLocation, null);
        //By default it would be walkIn and will not show the pickup type screen
        Assert.assertEquals(0, storeLocation.getPickupTypes().size());
        Assert.assertFalse(storeLocation.getPickupTypes().contains(YextPickupType.Curbside));
        Assert.assertFalse(storeLocation.getPickupTypes().contains(YextPickupType.WalkIn));
        Assert.assertFalse(storeLocation.getPickupTypes().contains(YextPickupType.DriveThru));
    }


}
