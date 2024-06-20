package caribouapp.caribou.com.cariboucoffee.api.model.content;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrOmsData;

public class CmsMenuProduct implements Serializable {

    @SerializedName("title")
    private String mTitle;

    @SerializedName("effectiveDate")
    private DateTime mEffectiveDate;

    @SerializedName("archiveDate")
    private DateTime mArchiveDate;

    @SerializedName("nutritionId")
    private String mNutritionId;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("image")
    private String mImage;

    @SerializedName("imageThumb")
    private String mImageThumb;

    @SerializedName("allergens")
    private List<String> mAllergens = new ArrayList<>();

    @SerializedName("orderAheadOnly")
    private boolean mOrderAhead;

    @SerializedName("omsIds")
    private List<String> mOmsIds = new ArrayList<>();

    @SerializedName("servings")
    private List<CmsServingNutritionalData> mServings;

    @SerializedName("omsData")
    private NcrOmsData mOmsData;

    @SerializedName("prepTime")
    private Integer mPrepTimeInMins;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public DateTime getEffectiveDate() {
        return mEffectiveDate;
    }

    public void setEffectiveDate(DateTime effectiveDate) {
        mEffectiveDate = effectiveDate;
    }

    public DateTime getArchiveDate() {
        return mArchiveDate;
    }

    public void setArchiveDate(DateTime archiveDate) {
        mArchiveDate = archiveDate;
    }

    public String getNutritionId() {
        return mNutritionId;
    }

    public void setNutritionId(String nutritionId) {
        mNutritionId = nutritionId;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public String getImageThumb() {
        return mImageThumb;
    }

    public void setImageThumb(String imageThumb) {
        mImageThumb = imageThumb;
    }

    public List<String> getAllergens() {
        return mAllergens;
    }

    public void setAllergens(List<String> allergens) {
        mAllergens = allergens;
    }

    public boolean isOrderAhead() {
        return mOrderAhead;
    }

    public void setOrderAhead(boolean orderAhead) {
        mOrderAhead = orderAhead;
    }

    public List<String> getOmsIds() {
        return mOmsIds;
    }

    public void setOmsIds(List<String> omsIds) {
        mOmsIds = omsIds;
    }

    public List<CmsServingNutritionalData> getServings() {
        return mServings;
    }

    public void setServings(List<CmsServingNutritionalData> servings) {
        mServings = servings;
    }

    public NcrOmsData getOmsData() {
        return mOmsData;
    }

    public void setOmsData(NcrOmsData omsData) {
        mOmsData = omsData;
    }

    public Integer getPrepTimeInMins() {
        return mPrepTimeInMins;
    }

    public void setPrepTimeInMins(Integer prepTimeInMins) {
        mPrepTimeInMins = prepTimeInMins;
    }
}
