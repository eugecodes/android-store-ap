package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by asegurola on 4/5/18.
 */

public class OmsEntityRef implements Serializable {
    @SerializedName("id")
    private Long mId;

    @SerializedName("uuid")
    private String mUuid;

    @SerializedName("name")
    private String mName;

    public OmsEntityRef(Long id, String uuid, String name) {
        mId = id;
        mUuid = uuid;
        mName = name;
    }

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getUuid() {
        return mUuid;
    }

    public void setUuid(String uuid) {
        mUuid = uuid;
    }

}
