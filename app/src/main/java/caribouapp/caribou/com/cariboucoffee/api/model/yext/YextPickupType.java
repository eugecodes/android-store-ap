package caribouapp.caribou.com.cariboucoffee.api.model.yext;

import androidx.annotation.StringRes;

import com.google.gson.annotations.SerializedName;

import caribouapp.caribou.com.cariboucoffee.R;

public enum YextPickupType {
    @SerializedName("WALK-IN")
    WalkIn(R.string.pickup_walkin, "walk_in"),

    @SerializedName("DRIVE-THRU")
    DriveThru(R.string.pickup_drivethru, "drive_thru"),

    @SerializedName("CURBSIDE")
    Curbside(R.string.pickup_curbside, "curbside"),

    @SerializedName("DELIVERY")
    Delivery(R.string.pickup_delivery, "delivery");

    private int mDisplayNameStringId;

    private String mServerName;

    YextPickupType(@StringRes int displayNameStringId, String serverName) {
        mServerName = serverName;
        mDisplayNameStringId = displayNameStringId;
    }

    public String getServerName() {
        return mServerName;
    }

    public static YextPickupType fromServerName(String serverName) {
        for (YextPickupType type : values()) {
            if (type.getServerName().equals(serverName)) {
                return type;
            }
        }
        return null;
    }

    @StringRes
    public int getDisplayNameStringId() {
        return mDisplayNameStringId;
    }
}
