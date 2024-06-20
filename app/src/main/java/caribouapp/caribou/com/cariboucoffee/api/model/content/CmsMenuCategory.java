package caribouapp.caribou.com.cariboucoffee.api.model.content;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CmsMenuCategory implements Serializable {

    @SerializedName("category")
    private String mName;

    @SerializedName("groups")
    private List<CmsMenuGroup> mGroups = new ArrayList<>();

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<CmsMenuGroup> getGroups() {
        return mGroups;
    }

    public void setGroups(List<CmsMenuGroup> groups) {
        mGroups = groups;
    }
}
