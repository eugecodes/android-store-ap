package caribouapp.caribou.com.cariboucoffee.mvp.menu.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jmsmuy on 3/15/18.
 */

public class MenuCategory implements Serializable {

    private List<MenuCategory> mSubCategories = new ArrayList<>();
    private String mName;
    private transient MenuCategory mParent = null;
    private List<MenuCardItemModel> mProducts = new ArrayList<>();
    private int mAmountProducts;

    public MenuCategory(String name) {
        mName = name;
    }

    public List<MenuCategory> getSubCategories() {
        return mSubCategories;
    }

    public void setSubCategories(List<MenuCategory> subCategories) {
        mSubCategories = subCategories;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public MenuCategory getParent() {
        return mParent;
    }

    public void setParent(MenuCategory parent) {
        mParent = parent;
    }

    public void addProduct(MenuCardItemModel product) {
        mProducts.add(product);
    }

    public List<MenuCardItemModel> getProducts() {
        return mProducts;
    }

    public void setAmountProducts(int amountProducts) {
        mAmountProducts = amountProducts;
    }

    public int getAmountProducts() {
        return mAmountProducts;
    }

    public void setProducts(List<MenuCardItemModel> products) {
        mProducts = products;
    }
}
