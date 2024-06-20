package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

import caribouapp.caribou.com.cariboucoffee.BuildConfig;


/**
 * Created by jmsmuy on 11/7/17.
 */

public abstract class AmsRequest {

    // TODO: Remove mSourceApp param when service stops using it
    @SerializedName("sourceApp")
    private String mSourceApp = BuildConfig.SOURCE_APP;

    public String getSourceApp() {
        return mSourceApp;
    }

    public void setSourceApp(String sourceApp) {
        mSourceApp = sourceApp;
    }

}
