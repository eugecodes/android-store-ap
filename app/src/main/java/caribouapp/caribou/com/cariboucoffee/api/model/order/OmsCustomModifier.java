package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asegurola on 3/28/18.
 */

public class OmsCustomModifier implements Serializable {

    @SerializedName("id")
    private long mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("entangled_modifiers")
    private List<OmsEntangledModifier> mEntangledModifier = new ArrayList<>();

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<OmsEntangledModifier> getEntangledModifier() {
        return mEntangledModifier;
    }

    public void setEntangledModifier(List<OmsEntangledModifier> entangledModifier) {
        mEntangledModifier = entangledModifier;
    }

    @Override
    public String toString() {
        return "OmsCustomModifier{"
                + "mId=" + mId
                + ", mName='" + mName + '\''
                + '}';
    }
}
