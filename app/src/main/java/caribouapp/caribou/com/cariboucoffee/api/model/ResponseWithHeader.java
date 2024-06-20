package caribouapp.caribou.com.cariboucoffee.api.model;

import com.google.gson.annotations.SerializedName;


/**
 * Created by andressegurola on 11/16/17.
 */

public class ResponseWithHeader {

    @SerializedName("header")
    private ResponseHeader mHeader;

    public ResponseHeader getHeader() {
        return mHeader;
    }

    public void setHeader(ResponseHeader header) {
        mHeader = header;
    }
}
