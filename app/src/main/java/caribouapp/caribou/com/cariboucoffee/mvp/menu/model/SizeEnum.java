package caribouapp.caribou.com.cariboucoffee.mvp.menu.model;

import java.util.Locale;

/**
 * Created by jmsmuy on 11/20/17.
 */

public enum SizeEnum {
    EXTRA_LARGE("extra", "Extra"),
    SMALL("small", "Small"), MEDIUM("medium", "Medium"),
    LARGE("large", "Large"), ONE_SIZE(null, "One Size");

    private String mCsvNutritionDataName;
    private final String mOmsOrderingSizeName;

    SizeEnum(String omsNutritionDataName, String omsOrderingSizeName) {
        mCsvNutritionDataName = omsNutritionDataName;
        mOmsOrderingSizeName = omsOrderingSizeName;
    }

    public String getCsvNutritionDataName() {
        return mCsvNutritionDataName;
    }

    public String getOmsOrderingSizeName() {
        return mOmsOrderingSizeName;
    }

    public static SizeEnum fromOmsOrderName(String omsOrderingSizeName) {
        for (SizeEnum sizeEnum : SizeEnum.values()) {
            if (sizeEnum.getOmsOrderingSizeName().trim().equalsIgnoreCase(omsOrderingSizeName.trim())) {
                return sizeEnum;
            }
        }
        return ONE_SIZE;
    }

    public static SizeEnum fromCSV(String csvSizeString) {
        for (SizeEnum sizeEnum : SizeEnum.values()) {
            if (sizeEnum.getCsvNutritionDataName() != null
                    && csvSizeString.trim().toUpperCase(Locale.US)
                    .contains(sizeEnum.getCsvNutritionDataName().trim().toUpperCase(Locale.US))) {
                return sizeEnum;
            }
        }
        return ONE_SIZE;
    }
}
