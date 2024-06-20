package caribouapp.caribou.com.cariboucoffee.api.model.content;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by jmsmuy on 10/31/17.
 */

public class CmsPromoOrPerk implements Serializable {

    @SerializedName("Image")
    private String mImage;

    @SerializedName("Headline")
    private String mHeading;

    @SerializedName("Description")
    private String mDescription;

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public String getHeading() {
        return mHeading;
    }

    public void setTitle(String title) {
        mHeading = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

}
