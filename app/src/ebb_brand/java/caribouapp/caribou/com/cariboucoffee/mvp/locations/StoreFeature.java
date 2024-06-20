package caribouapp.caribou.com.cariboucoffee.mvp.locations;

import androidx.annotation.StringRes;

import java.io.Serializable;

import caribouapp.caribou.com.cariboucoffee.R;

/**
 * Created by andressegurola on 10/12/17.
 */

public enum StoreFeature implements Serializable {
    WIFI(R.string.wifi, "WIFI"),
    DRIVE_THRU(R.string.drive_thru, "DRIVE-THRU"),
    CATERING(R.string.catering, "CATERING"),
    CARIBOU_COFFEE_AT_EINSTEIN_BROS_BAGELS(R.string.caribou_coffee_at_einstein, "Caribou Coffee at Einstein Bros. Bagels"),
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
