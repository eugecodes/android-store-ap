package caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model;

import java.io.Serializable;
import java.math.BigDecimal;

import caribouapp.caribou.com.cariboucoffee.order.Order;

public abstract class TippingOption implements Serializable {

    public abstract BigDecimal calculateTip(Order order);

    public abstract String getDescription();
}
