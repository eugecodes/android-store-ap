package caribouapp.caribou.com.cariboucoffee.api.model.geocoding;

import com.google.gson.annotations.SerializedName;

public class DistanceElement {

    @SerializedName("status")
    private StatusCode mStatus;

    @SerializedName("duration")
    private Duration mDuration;

    @SerializedName("distance")
    private Distance mDistance;

    public StatusCode getStatus() {
        return  mStatus;
    }

    public void setStatus(StatusCode status) {
        mStatus = status;
    }

    public Duration getDuration() {
        return mDuration;
    }

    public void setDuration(Duration duration) {
        mDuration = duration;
    }

    public Distance getDistance() {
        return mDistance;
    }

    public void setDistance(Distance distance) {
        mDistance = distance;
    }

    public enum StatusCode {
        OK,
        NOT_FOUND,
        ZERO_RESULTS,
        MAX_ROUTE_LENGTH_EXCEEDED
    }
}
