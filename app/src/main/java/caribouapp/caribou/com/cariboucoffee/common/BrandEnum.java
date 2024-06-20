package caribouapp.caribou.com.cariboucoffee.common;

import android.text.TextUtils;

/**
 * Created by gonzalogelos on 6/13/18.
 */

public enum BrandEnum {
    EBB_BRAND("ebb_brand"), BRU_BRAND("bru_brand"), POLAR_BRAND("polar_bear_brand"), NNYB_BRAND("nnyb_brand"), CBOU_BRAND("caribou_brand");

    String mBrandFlavorName;

    BrandEnum(String brand) {
        mBrandFlavorName = brand;
    }

    public static BrandEnum getFromBrand(String brand) {
        if (TextUtils.isEmpty(brand)) {
            return null;
        }
        for (BrandEnum brandEnum : BrandEnum.values()) {
            if (brandEnum.toString().equalsIgnoreCase(brand)) {
                return brandEnum;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return mBrandFlavorName;
    }
}
