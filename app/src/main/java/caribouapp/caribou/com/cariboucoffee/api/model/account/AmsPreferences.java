package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

import caribouapp.caribou.com.cariboucoffee.common.BrandEnum;

/**
 * Created by jmsmuy on 11/2/17.
 */

public class AmsPreferences {

    @SerializedName("ecomMarketingEmail")
    private Boolean mEcomMarketingEmail;

    @SerializedName("mbcEmail")
    private Boolean mMbcEmail;

    @SerializedName("ecomProgram")
    private Boolean mEcomProgram;

    //Cbou brand preferences
    @SerializedName("perksProgram")
    private Boolean mPerksProgram;

    @SerializedName("perksEmail")
    private Boolean mPerksEmail;

    @SerializedName("cbouMarketingEmail")
    private Boolean mCbouMarketingEmail;

    //Ebb brand preferences
    @SerializedName("ebbProgram")
    private Boolean mEbbProgram;

    @SerializedName("ebbEmail")
    private Boolean mEbbEmail;

    @SerializedName("cateringProgram")
    private Boolean mEbbCateringProgram;

    @SerializedName("cateringEmail")
    private Boolean mEbbCateringEmail;

    //Nnyb brand preferences
    @SerializedName("nnybProgram")
    private Boolean mNnybProgram;

    @SerializedName("nnybEmail")
    private Boolean mNnybEmail;

    @SerializedName("nnybCateringProgram")
    private Boolean mNnybCateringProgram;

    @SerializedName("nnybCateringEmail")
    private Boolean mNnybCateringEmail;

    //Bru brand preferences
    @SerializedName("bruProgram")
    private Boolean mBruProgram;

    @SerializedName("bruEmail")
    private Boolean mBruEmail;

    @SerializedName("bruCateringEmail")
    private Boolean mBruCateringEmail;

    @SerializedName("bruCateringProgram")
    private Boolean mBruCateringProgram;


    public Boolean getPerksProgram() {
        return mPerksProgram;
    }

    public void setPerksProgram(Boolean perksProgram) {
        mPerksProgram = perksProgram;
    }

    public Boolean getPerksEmail() {
        return mPerksEmail;
    }

    public void setPerksEmail(Boolean perksEmail) {
        mPerksEmail = perksEmail;
    }

    public Boolean getEbbEmail() {
        return mEbbEmail;
    }

    public void setEbbEmail(Boolean ebbEmail) {
        mEbbEmail = ebbEmail;
    }

    public Boolean getNnybProgram() {
        return mNnybProgram;
    }

    public void setNnybProgram(Boolean nnybProgram) {
        mNnybProgram = nnybProgram;
    }

    public Boolean getNnybEmail() {
        return mNnybEmail;
    }

    public void setNnybEmail(Boolean nnybEmail) {
        mNnybEmail = nnybEmail;
    }

    public Boolean getNnybCateringProgram() {
        return mNnybCateringProgram;
    }

    public void setNnybCateringProgram(Boolean nnybCateringProgram) {
        mNnybCateringProgram = nnybCateringProgram;
    }

    public Boolean getNnybCateringEmail() {
        return mNnybCateringEmail;
    }

    public void setNnybCateringEmail(Boolean nnybCateringEmail) {
        mNnybCateringEmail = nnybCateringEmail;
    }

    public Boolean getCbouMarketingEmail() {
        return mCbouMarketingEmail;
    }

    public void setCbouMarketingEmail(Boolean cbouMarketingEmail) {
        mCbouMarketingEmail = cbouMarketingEmail;
    }

    public Boolean getEcomMarketingEmail() {
        return mEcomMarketingEmail;
    }

    public void setEcomMarketingEmail(Boolean ecomMarketingEmail) {
        mEcomMarketingEmail = ecomMarketingEmail;
    }

    public Boolean getMbcEmail() {
        return mMbcEmail;
    }

    public void setMbcEmail(Boolean mbcEmail) {
        mMbcEmail = mbcEmail;
    }

    public Boolean getEbbProgram() {
        return mEbbProgram;
    }

    public void setEbbProgram(Boolean ebbProgram) {
        mEbbProgram = ebbProgram;
    }

    public Boolean getEcomProgram() {
        return mEcomProgram;
    }

    public void setEcomProgram(Boolean ecomProgram) {
        mEcomProgram = ecomProgram;
    }

    public Boolean getBruProgram() {
        return mBruProgram;
    }

    public void setBruProgram(Boolean bruProgram) {
        mBruProgram = bruProgram;
    }

    public Boolean getBruEmail() {
        return mBruEmail;
    }

    public void setBruEmail(Boolean bruEmail) {
        mBruEmail = bruEmail;
    }

    public Boolean getBruCateringEmail() {
        return mBruCateringEmail;
    }

    public void setBruCateringEmail(Boolean bruCateringEmail) {
        mBruCateringEmail = bruCateringEmail;
    }

    public Boolean getBruCateringProgram() {
        return mBruCateringProgram;
    }

    public void setBruCateringProgram(Boolean bruCateringProgram) {
        mBruCateringProgram = bruCateringProgram;
    }

    public Boolean getEbbCateringProgram() {
        return mEbbCateringProgram;
    }

    public void setEbbCateringProgram(Boolean ebbCateringProgram) {
        mEbbCateringProgram = ebbCateringProgram;
    }

    public Boolean getEbbCateringEmail() {
        return mEbbCateringEmail;
    }

    public void setEbbCateringEmail(Boolean ebbCateringEmail) {
        mEbbCateringEmail = ebbCateringEmail;
    }

    public void setAmsPreferencesBrandSpecific(BrandEnum brand, boolean marketingEmailSubscription, boolean cateringEmailSubscription) {
        switch (brand) {
            case CBOU_BRAND:
            case POLAR_BRAND:
                setPerksProgram(true);
                setPerksEmail(marketingEmailSubscription);
                break;

            case BRU_BRAND:
                setBruProgram(true);
                setBruEmail(marketingEmailSubscription);
                setBruCateringEmail(cateringEmailSubscription);
                break;

            case NNYB_BRAND:
                setNnybProgram(true);
                setNnybEmail(marketingEmailSubscription);
                setNnybCateringEmail(cateringEmailSubscription);
                break;

            case EBB_BRAND:
                setEbbProgram(true);
                setEbbEmail(marketingEmailSubscription);
                setEbbCateringEmail(cateringEmailSubscription);
                break;
        }
    }


}
