package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import androidx.annotation.StringRes;

import caribouapp.caribou.com.cariboucoffee.R;

/**
 * Created by andressegurola on 10/12/17.
 */

public enum StoreFeature {
    WIFI(R.string.wifi, "WIFI"),
    DRIVE_THRU(R.string.drive_thru, "DRIVE-THRU"),
    CATERING(R.string.catering, "CATERING"),
    ORDER_OUT_OF_STORE(R.string.order_out_of_store, null);

    @StringRes
    private int mName;

    private String mYextName;

    StoreFeature(@StringRes int name, String yextName) {
        mName = name;
        mYextName = yextName;
    }

    @StringRes
    public int getNameRes() {
        return mName;
    }

    public String getYextName() {
        return mYextName;
    }

    public static StoreFeature fromYextName(String yextName) {
        for (StoreFeature storeFeature : StoreFeature.values()) {
            if (yextName.equalsIgnoreCase(storeFeature.getYextName())) {
                return storeFeature;
            }
        }
        return null;
    }
}
