package caribouapp.caribou.com.cariboucoffee.api.model.order;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.common.EntityWithId;
import caribouapp.caribou.com.cariboucoffee.order.ProductCustomizationData;

/**
 * Created by asegurola on 3/28/18.
 */

public class OmsCustomProduct extends EntityWithId<Long> implements ProductCustomizationData {

    @SerializedName("uuid")
    private String mUuid;

    @SerializedName("name")
    private String mName;

    @SerializedName("custom_groups")
    private List<OmsGroupModifier> mOmsGroupModifiers = new ArrayList<>();

    @SerializedName("default_modifiers")
    private List<Long> mDefaultModifiers = new ArrayList<>();

    @SerializedName("active")
    private boolean mIsActive;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<OmsGroupModifier> getOmsGroupModifiers() {
        return mOmsGroupModifiers;
    }

    public void setOmsGroupModifiers(List<OmsGroupModifier> omsGroupModifiers) {
        mOmsGroupModifiers = omsGroupModifiers;
    }

    public List<Long> getDefaultModifiers() {
        return mDefaultModifiers;
    }

    public void setDefaultModifiers(List<Long> defaultModifiers) {
        mDefaultModifiers = defaultModifiers;
    }

    public String getUuid() {
        return mUuid;
    }

    public void setUuid(String uuid) {
        mUuid = uuid;
    }

    public boolean isActive() {
        return mIsActive;
    }

    public void setActive(boolean active) {
        mIsActive = active;
    }

    public OmsGroupModifier getGroupById(long groupId) {
        for (OmsGroupModifier omsGroupModifier : getOmsGroupModifiers()) {
            if (omsGroupModifier.getId() == groupId) {
                return omsGroupModifier;
            }
        }
        return null;
    }
}
