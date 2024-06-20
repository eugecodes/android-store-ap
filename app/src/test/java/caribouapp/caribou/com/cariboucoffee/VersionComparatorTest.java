package caribouapp.caribou.com.cariboucoffee;

import org.junit.Test;

import caribouapp.caribou.com.cariboucoffee.util.VersionUtil;

import static org.junit.Assert.assertEquals;

/**
 * Created by jmsmuy on 2/2/18.
 */

public class VersionComparatorTest {

    @Test
    public void testVersionLower() {
        assertEquals(VersionUtil.compareVersions("1.1.0", "1.2"), -1);
    }

    @Test
    public void testVersionHigher() {
        assertEquals(VersionUtil.compareVersions("1.1.0", "1.0.0"), 1);
    }

    @Test
    public void testVersionSame() {
        assertEquals(VersionUtil.compareVersions("9.111.0", "9.111.0"), 0);
    }

    @Test
    public void testVersionSnapshot() {
        assertEquals(VersionUtil.compareVersions("9.111.0", "9.111.0-SNAPSHOT"), 0);
    }

    @Test
    public void testVersionsDifferentLength() {
        assertEquals(VersionUtil.compareVersions("9.111", "9.111.0.0"), -1);
    }

    @Test
    public void testVersionsDifferenceInString() {
        assertEquals(VersionUtil.compareVersions("9.111.0", "9.20.0"), 1);
    }

    @Test
    public void testVersionsDifferentBaseNumber() {
        assertEquals(VersionUtil.compareVersions("2.0", "1.0.2.4"), 1);
    }

    @Test
    public void testVersionsSmallDifference() {
        assertEquals(VersionUtil.compareVersions("1.1.1", "1.1"), 1);
    }

    @Test
    public void testVersionsBiggerIndexLowerVersion() {
        assertEquals(VersionUtil.compareVersions("1.1.1", "1.2"), -1);
    }

}
