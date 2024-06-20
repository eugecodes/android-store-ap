package caribouapp.caribou.com.cariboucoffee.api.model.content;

import com.google.gson.annotations.SerializedName;


public class CmsBanner {

    @SerializedName("id")
    private String mId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("image")
    private String mImageUrl;

    @SerializedName("destination")
    private String mBannerUrlDestination;

    public CmsBanner(String id, String title, String imageUrl, String bannerUrlDestination) {
        mId = id;
        mTitle = title;
        mImageUrl = imageUrl;
        mBannerUrlDestination = bannerUrlDestination;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getBannerUrlDestination() {
        return mBannerUrlDestination;
    }

    public void setBannerUrlDestination(String bannerUrlDestination) {
        mBannerUrlDestination = bannerUrlDestination;
    }
}
