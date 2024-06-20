package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asegurola on 4/13/18.
 */

public class OmsItemDetails implements Serializable {

    @SerializedName("groups")
    private List<OmsOrderGroupModifier> mGroups = new ArrayList<>();

    public List<OmsOrderGroupModifier> getGroups() {
        return mGroups;
    }

    public void setGroups(List<OmsOrderGroupModifier> groups) {
        mGroups = groups;
    }
}
