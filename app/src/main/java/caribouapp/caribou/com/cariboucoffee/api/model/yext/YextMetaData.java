package caribouapp.caribou.com.cariboucoffee.api.model.yext;

import com.google.gson.annotations.SerializedName;

public class YextMetaData {

    @SerializedName("id")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
