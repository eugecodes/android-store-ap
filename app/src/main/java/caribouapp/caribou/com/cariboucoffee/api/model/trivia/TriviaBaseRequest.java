package caribouapp.caribou.com.cariboucoffee.api.model.trivia;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TriviaBaseRequest implements Serializable {

    @SerializedName("uid")
    private String mUuid;

    public TriviaBaseRequest(String uid) {
        mUuid = uid;
    }

    public String getUuid() {
        return mUuid;
    }

    public void setUuid(String uuid) {
        mUuid = uuid;
    }

}
