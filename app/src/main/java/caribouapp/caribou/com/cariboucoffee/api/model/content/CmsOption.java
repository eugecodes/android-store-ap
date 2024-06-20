package caribouapp.caribou.com.cariboucoffee.api.model.content;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmsmuy on 10/13/17.
 */

public class CmsOption {

    @SerializedName("Title")
    private String mTitle;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
