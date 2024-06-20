package caribouapp.caribou.com.cariboucoffee.mvp.menu.model;

import androidx.databinding.Bindable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import caribouapp.caribou.com.cariboucoffee.api.model.content.ncr.NcrOmsData;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.util.SitecoreUtil;

/**
 * Created by jmsmuy on 10/3/17.
 */
public class MenuCardItemModel extends MenuCardModel implements Serializable {

    private static final int SITECORE_THUMBNAIL_MAX_HEIGHT = 480;
    private static final int SITECORE_IMAGE_MAX_HEIGHT = 2048;

    private String mThumbnailImage;
    private String mAltThumbnailImage;
    private String mImage;
    private String mAltImage;
    private String mName;
    private Set<String> mOmsProdIdSet = new HashSet<>();
    private String mOmsProdIdForCurrentLocation;
    private Integer mPrepTimeInMins;
    private boolean mAvailableForCurrentLocation = true;

    private String mDescription;
    private NutritionalProductModel mNutritionalProductModel;
    private List<String> mAllergens;
    private boolean mIsOrderAheadMenuOnly;

    private NcrOmsData mNcrOmsData;
    private Integer mBulkPrepTime;

    public MenuCardItemModel(SettingsServices settingsServices) {
        mAllergens = new ArrayList<>();
        mBulkPrepTime = settingsServices.getBulkPrepTimeInMins();
    }

    @Bindable
    public String getThumbnailImage() {
        return SitecoreUtil.getImageUrlForMaxHeight(mThumbnailImage, SITECORE_THUMBNAIL_MAX_HEIGHT);
    }

    public void setThumbnailImage(String image) {
        mThumbnailImage = image;
    }

    @Bindable
    public String getAltThumbnailImage() {
        return SitecoreUtil.getImageUrlForMaxHeight(mAltThumbnailImage, SITECORE_THUMBNAIL_MAX_HEIGHT);
    }

    public void setAltThumbnailImage(String altThumbnailImage) {
        mAltThumbnailImage = altThumbnailImage;
    }

    @Bindable
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getImage() {
        return SitecoreUtil.getImageUrlForMaxHeight(mImage, SITECORE_IMAGE_MAX_HEIGHT);
    }

    public void setImage(String image) {
        mImage = image;
    }

    public String getAltImage() {
        return SitecoreUtil.getImageUrlForMaxHeight(mAltImage, SITECORE_IMAGE_MAX_HEIGHT);
    }

    public void setAltImage(String altImage) {
        mAltImage = altImage;
    }

    /**
     * Returns default nutritionalInfoModel
     *
     * @return
     */
    public NutritionalItemModel getDefaultNutritionalItemModel() {
        if (mNutritionalProductModel == null) {
            return null;
        }
        return mNutritionalProductModel.getDefaultNutritionalItemModel();
    }

    public Map<SizeEnum, NutritionalItemModel> getNutritionalItemModels() {
        if (mNutritionalProductModel == null) {
            return new HashMap<>();
        }

        return mNutritionalProductModel.getNutritionalItemModels();
    }

    public void setNutritionalProductModel(NutritionalProductModel nutritionalProductModel) {
        mNutritionalProductModel = nutritionalProductModel;
    }

    public List<String> getAllergens() {
        return mAllergens;
    }

    public void setAllergens(List<String> allergens) {
        mAllergens = allergens;
    }


    public Set<String> getOmsProdIdSet() {
        return mOmsProdIdSet;
    }

    public String getOmsProdIdForCurrentLocation() {
        return mOmsProdIdForCurrentLocation;
    }

    public void setOmsProdIdForCurrentLocation(String omsProdIdForCurrentLocation) {
        mOmsProdIdForCurrentLocation = omsProdIdForCurrentLocation;
    }

    public boolean isOrderAheadMenuOnly() {
        return mIsOrderAheadMenuOnly;
    }

    public void setOrderAheadMenuOnly(boolean orderAheadMenuOnly) {
        mIsOrderAheadMenuOnly = orderAheadMenuOnly;
    }

    public NcrOmsData getNcrOmsData() {
        return mNcrOmsData;
    }

    public void setNcrOmsData(NcrOmsData ncrOmsData) {
        mNcrOmsData = ncrOmsData;
    }

    public Integer getPrepTimeInMins() {
        return mPrepTimeInMins;
    }

    public void setPrepTimeInMins(Integer prepTimeInMins) {
        mPrepTimeInMins = prepTimeInMins;
    }

    public boolean isBulk() {
        return mPrepTimeInMins != null && mBulkPrepTime != null && mPrepTimeInMins.compareTo(mBulkPrepTime) >= 0;
    }

    public boolean isAvailableForCurrentLocation() {
        return mAvailableForCurrentLocation;
    }

    public void setAvailableForCurrentLocation(boolean availableForCurrentLocation) {
        mAvailableForCurrentLocation = availableForCurrentLocation;
    }
}
