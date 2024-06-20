package caribouapp.caribou.com.cariboucoffee.mvp.oos.recentOrders.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.mvp.menu.model.SizeEnum;

public class RecentOrderItem implements Serializable {

    private String mProductName;
    private BigDecimal mQuantity;
    private List<String> mCustomizations = new ArrayList<>();
    private SizeEnum mSizeEnum;

    public String getProductName() {
        return mProductName;
    }

    public void setProductName(String productName) {
        mProductName = productName;
    }

    public BigDecimal getQuantity() {
        return mQuantity;
    }

    public void setQuantity(BigDecimal quantity) {
        mQuantity = quantity;
    }

    public List<String> getCustomizations() {
        return mCustomizations;
    }

    public void setCustomizations(List<String> customizations) {
        mCustomizations = customizations;
    }

    public SizeEnum getSizeEnum() {
        return mSizeEnum;
    }

    public void setSizeEnum(SizeEnum sizeEnum) {
        mSizeEnum = sizeEnum;
    }
}
