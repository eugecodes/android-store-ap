package caribouapp.caribou.com.cariboucoffee.common;

import android.text.TextUtils;

public enum ScaleTypeEnum {
    CENTER_CROP("center_crop"), FIT_XY("fit_xy"), FIT_CENTER("fit_center");

    private String mScaleType;

    ScaleTypeEnum(String scaleType) {
        mScaleType = scaleType;
    }

    public String getScaleType() {
        return mScaleType;
    }

    public static ScaleTypeEnum getFromName(String scaleTypeString) {
        if (TextUtils.isEmpty(scaleTypeString)) {
            return null;
        }
        for (ScaleTypeEnum scaleType : ScaleTypeEnum.values()) {
            if (scaleType.getScaleType().equalsIgnoreCase(scaleTypeString)) {
                return scaleType;
            }
        }
        return null;
    }
}
