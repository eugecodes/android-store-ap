package caribouapp.caribou.com.cariboucoffee.api.model.content;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CmsMenu implements Serializable {

    @SerializedName("nep-enterprise-unit")
    private String mNepEnterpriseUnit;

    @SerializedName("menu")
    private List<CmsMenuCategory> mCategories;

    public List<CmsMenuCategory> getCategories() {
        return mCategories;
    }

    public void setCategories(List<CmsMenuCategory> categories) {
        mCategories = categories;
    }

    public String getNepEnterpriseUnit() {
        return mNepEnterpriseUnit;
    }

    public void setNepEnterpriseUnit(String nepEnterpriseUnit) {
        mNepEnterpriseUnit = nepEnterpriseUnit;
    }
}
