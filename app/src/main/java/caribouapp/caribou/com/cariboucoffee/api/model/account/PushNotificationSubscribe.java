package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

/**
 * Created by andressegurola on 12/29/17.
 */

public class PushNotificationSubscribe {
    @SerializedName("printedCardNumber")
    private String mPrintedCardNumber;

    @SerializedName("deviceToken")
    private String mDeviceToken;

    @SerializedName("deviceSystemName")
    private String mDeviceSystemName;

    @SerializedName("deviceSystemVersion")
    private String mDeviceSystemVersion;

    @SerializedName("deviceModel")
    private String mDeviceModel;

    public String getPrintedCardNumber() {
        return mPrintedCardNumber;
    }

    public void setPrintedCardNumber(String printedCardNumber) {
        mPrintedCardNumber = printedCardNumber;
    }

    public String getDeviceToken() {
        return mDeviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        mDeviceToken = deviceToken;
    }

    public String getDeviceSystemName() {
        return mDeviceSystemName;
    }

    public void setDeviceSystemName(String deviceSystemName) {
        mDeviceSystemName = deviceSystemName;
    }

    public String getDeviceSystemVersion() {
        return mDeviceSystemVersion;
    }

    public void setDeviceSystemVersion(String deviceSystemVersion) {
        mDeviceSystemVersion = deviceSystemVersion;
    }

    public String getDeviceModel() {
        return mDeviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        mDeviceModel = deviceModel;
    }
}
