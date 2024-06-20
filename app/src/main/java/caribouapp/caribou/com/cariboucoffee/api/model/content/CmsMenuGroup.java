package caribouapp.caribou.com.cariboucoffee.api.model.content;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CmsMenuGroup implements Serializable {

    @SerializedName("group")
    private String mName;

    @SerializedName("products")
    private List<CmsMenuProduct> mProducts = new ArrayList<>();

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<CmsMenuProduct> getProducts() {
        return mProducts;
    }

    public void setProducts(List<CmsMenuProduct> products) {
        mProducts = products;
    }
}
