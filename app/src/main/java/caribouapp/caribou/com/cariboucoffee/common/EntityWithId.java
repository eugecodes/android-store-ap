package caribouapp.caribou.com.cariboucoffee.common;

import androidx.databinding.BaseObservable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by asegurola on 3/28/18.
 */

public class EntityWithId<IdType> extends BaseObservable implements Serializable {

    @SerializedName("id")
    private IdType mId;

    public IdType getId() {
        return mId;
    }

    public void setId(IdType id) {
        mId = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EntityWithId)) {
            return false;
        }

        EntityWithId itemOption = (EntityWithId) obj;
        return mId.equals(itemOption.mId);
    }

    @Override
    public int hashCode() {
        if (mId == null) {
            return super.hashCode();
        }
        return mId.hashCode();
    }
}
