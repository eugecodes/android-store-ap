package caribouapp.caribou.com.cariboucoffee.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmsmuy on 11/2/17.
 * Updated by Swapnil on 23/6/22
 */

public class ResponseHeader {

    public static final String SUCCESS_STATUS = "Success";

    public static final String FAILURE_STATUS = "Failure";

    public static final String DELETE_ALREADY_EXISTS = "409";

    public static final String DELETE_NON_ZERO_BALANCE = "400";

    public static final String SUCCESS_DELETE_ACCOUNT = "200";


    @SerializedName("status")
    private String mStatus;
    @SerializedName("errorCode")
    private String mErrorCode;
    @SerializedName("msg")
    private Object mMsg;
    @SerializedName("statusCode")
    private String mStatusCode;
    @SerializedName("message")
    private String mMessage;

    public String getmStatusCode() {
        return mStatusCode;
    }

    public void setmStatusCode(String mStatusCode) {
        this.mStatusCode = mStatusCode;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(String errorCode) {
        mErrorCode = errorCode;
    }

    public Object getMsg() {
        return mMsg;
    }

    public void setMsg(String msg) {
        mMsg = msg;
    }
}
