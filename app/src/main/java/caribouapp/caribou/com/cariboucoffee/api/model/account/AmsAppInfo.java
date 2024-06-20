package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmsmuy on 2/23/18.
 */

public class AmsAppInfo {

    @SerializedName("brand")
    private String mBrand;

    @SerializedName("version")
    private String mVersion;

    @SerializedName("os")
    private String mOs = "Android";

    @SerializedName("osVersion")
    private String mOsVersion;

    public String getBrand() {
        return mBrand;
    }

    public void setBrand(String brand) {
        mBrand = brand;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public String getOs() {
        return mOs;
    }

    public void setOs(String os) {
        mOs = os;
    }

    public String getOsVersion() {
        return mOsVersion;
    }

    public void setOsVersion(String osVersion) {
        mOsVersion = osVersion;
    }
}
