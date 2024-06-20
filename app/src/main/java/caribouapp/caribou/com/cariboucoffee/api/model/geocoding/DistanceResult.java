package caribouapp.caribou.com.cariboucoffee.api.model.geocoding;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DistanceResult implements Serializable {

    @SerializedName("status")
    private StatusCode mStatus;

    @SerializedName("rows")
    private List<DistanceRow> mRows;

    public StatusCode getStatus() {
        return mStatus;
    }

    public void setStatus(StatusCode status) {
        mStatus = status;
    }

    public List<DistanceRow> getRows() {
        return mRows;
    }

    public void setRows(List<DistanceRow> rows) {
        mRows = rows;
    }

    public enum StatusCode {
        OK,
        INVALID_REQUEST,
        MAX_ELEMENTS_EXCEEDED,
        OVER_DAILY_LIMIT,
        OVER_QUERY_LIMIT,
        REQUEST_DENIED,
        UNKNOWN_ERROR,
    }
}
