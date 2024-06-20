package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asegurola on 4/27/18.
 */

public class OmsOrderMetadata implements Serializable {

    @SerializedName("store_location")
    private OmsStoreLocationExtraData mOmsStoreLocationExtraData;

    @SerializedName("custom_products")
    private List<OmsCustomProduct> mOmsCustomProductList = new ArrayList<>();

    @SerializedName("last_activity")
    private DateTime mLastActivity;

    public OmsStoreLocationExtraData getOmsStoreLocationExtraData() {
        return mOmsStoreLocationExtraData;
    }

    public void setOmsStoreLocationExtraData(OmsStoreLocationExtraData omsStoreLocationExtraData) {
        mOmsStoreLocationExtraData = omsStoreLocationExtraData;
    }

    public List<OmsCustomProduct> getOmsCustomProductList() {
        return mOmsCustomProductList;
    }

    public void setOmsCustomProductList(List<OmsCustomProduct> omsCustomProductList) {
        mOmsCustomProductList = omsCustomProductList;
    }

    public DateTime getLastActivity() {
        return mLastActivity;
    }

    public void setLastActivity(DateTime lastActivity) {
        mLastActivity = lastActivity;
    }
}
