package caribouapp.caribou.com.cariboucoffee.api.model.content;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public class CmsWebContentSection {

    @SerializedName("id")
    private BigDecimal mId;

    @SerializedName("category")
    private String mCategory;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("type")
    private String mType;

    @SerializedName("bodyRendered")
    private String mBodyRendered;

    @SerializedName("updatedAt")
    private DateTime mUpdatedAt;

    @SerializedName("effectiveDate")
    private DateTime mEffectiveDate;

    @SerializedName("archiveDate")
    private DateTime mArchiveDate;

    @SerializedName("showInProduction")
    private boolean mShowInProduction;

    @SerializedName("priority")
    private BigDecimal mPriority;

    public BigDecimal getId() {
        return mId;
    }

    public void setId(BigDecimal id) {
        mId = id;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getBodyRendered() {
        return mBodyRendered;
    }

    public void setBodyRendered(String bodyRendered) {
        mBodyRendered = bodyRendered;
    }

    public DateTime getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(DateTime updatedAt) {
        mUpdatedAt = updatedAt;
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

    public boolean isShowInProduction() {
        return mShowInProduction;
    }

    public void setShowInProduction(boolean showInProduction) {
        mShowInProduction = showInProduction;
    }

    public BigDecimal getPriority() {
        return mPriority;
    }

    public void setPriority(BigDecimal priority) {
        mPriority = priority;
    }
}
