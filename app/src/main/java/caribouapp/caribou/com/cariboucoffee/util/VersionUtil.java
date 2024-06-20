package caribouapp.caribou.com.cariboucoffee.util;

/**
 * Created by jmsmuy on 2/2/18.
 */

public final class VersionUtil {

    private VersionUtil() {
    }

    /**
     * Compares 2 version numbers
     * if versionA > versionB returns 1
     * if versionA = versionB returns 0
     * if versionA < versionB returns -1
     *
     * @param versionA
     * @param versionB
     * @return
     */
    public static int compareVersions(String versionA, String versionB) {

        // We first remove any "-SNAPSHOT" or similar subversion indication
        versionA = versionA.split("-")[0];
        versionB = versionB.split("-")[0];

        // Now we split by dots
        String[] aArray = versionA.split("\\.");
        String[] bArray = versionB.split("\\.");

        int aLength = aArray.length;
        int bLength = bArray.length;
        int i = 0;

        // We find in which index they differ
        while (i < aLength && i < bLength && aArray[i].equals(bArray[i])) {
            i++;
        }
        // If the index is smaller than the the length of either of the versions, or if the index
        // is identical to both lengths, then we detected a difference for sure
        if (i < aLength && i < bLength || aLength == i && bLength == i) {
            // Now we have to check with which condition we entered with
            if (aLength == i) { // It's unnecessary to check for bLength
                i--;
            }
            return Integer.compare(Integer.valueOf(aArray[i]).compareTo(Integer.valueOf(bArray[i])), 0);
        } else { // If the index is equal to one of the arrays length, but not to the other, then the larger one wins
            return aLength > bLength ? 1 : -1;
        }
    }
}
