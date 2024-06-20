package caribouapp.caribou.com.cariboucoffee.order.ncr;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrLocation;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderApiData;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderLine;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderStatus;
import caribouapp.caribou.com.cariboucoffee.api.model.order.ncr.NcrOrderWrappedData;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.ncr.NcrOrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.locations.StoreLocation;
import caribouapp.caribou.com.cariboucoffee.order.Order;

public class NcrOrder extends Order<NcrOrderItem> {

    @SerializedName("ncrOrderId")
    private String mNcrOrderId;

    @SerializedName("status")
    private NcrOrderStatus mStatus;

    public NcrOrder(StoreLocation storeLocation) {
        super(storeLocation);
    }

    @Override
    public boolean isFinished() {
        // TODO
        return false;
    }

    @Override
    public boolean isNew() {
        return getNcrOrderId() == null;
    }

    /**
     * @param selectedOrderItem Order Item what has a newQuantity to add
     * @param newQuantity       the new quantity to be added
     * @return T/F Checks if order did not pass max product quantity limit.
     */
    @Override
    public boolean orderItemPassesMaxQuantityCheck(OrderItem selectedOrderItem, int newQuantity) {
        return calculateRemainingOrderItemQuantity(selectedOrderItem, newQuantity) < 0;
    }

    /**
     * @param selectedOrderItem Order Item what has a newQuantity to add
     * @param newQuantity       the new quantity to be added
     * @return Calculated remaining order items quantity
     */
    public int calculateRemainingOrderItemQuantity(OrderItem selectedOrderItem, int newQuantity) {
        int orderQuantityLimit = selectedOrderItem.getMaxQuantity();
        int orderProductQuantity = 0;
        for (NcrOrderItem orderItem : getItems()) {
            if (orderItem.getMenuData().getName().equals(selectedOrderItem.getMenuData().getName())) {
                if (!orderItem.getId().equals(selectedOrderItem.getId())) {
                    orderProductQuantity += orderItem.getQuantity();
                }
                // If there is another item with same name, I'll pick smaller quantity limit
                if (orderItem.getMaxQuantity() < selectedOrderItem.getMaxQuantity()) {
                    orderQuantityLimit = orderItem.getMaxQuantity();
                }
            }
        }
        return orderQuantityLimit - (newQuantity + orderProductQuantity);
    }

    /**
     * @return T/F if any item quantity is greater than quantity limit.
     */
    @Override
    public boolean validateOrderItemQuantity() {
        HashMap<String, Integer> orderQuantities = new HashMap<>();
        HashMap<String, Integer> orderLimits = new HashMap<>();
        for (NcrOrderItem orderItem : getItems()) {
            String name = orderItem.getMenuData().getName();
            if (name != null) {
                if (orderQuantities.containsKey(name)) {
                    orderQuantities.put(name, orderQuantities.get(name) + orderItem.getQuantity());
                    if (orderLimits.get(name) > orderItem.getMaxQuantity()) {
                        orderLimits.put(name, orderItem.getMaxQuantity());
                    }
                } else {
                    orderQuantities.put(name, orderItem.getQuantity());
                    orderLimits.put(name, orderItem.getMaxQuantity());
                }
            }
        }
        for (String name : orderQuantities.keySet()) {
            if (orderQuantities.get(name) > orderLimits.get(name)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return T/F if order has at least one item with more than zero cost.
     */
    @Override
    public boolean validateOrderNotOnlyFreeItems() {
        for (NcrOrderItem orderItem : getItems()) {
            if (orderItem.getQuantity() > 0 && BigDecimal.ZERO.compareTo(orderItem.getPrice()) < 0) {
                return true;
            }
        }
        return false;
    }

    public String getNcrOrderId() {
        return mNcrOrderId;
    }

    public void setNcrOrderId(String ncrOrderId) {
        mNcrOrderId = ncrOrderId;
    }

    public NcrOrderStatus getStatus() {
        return mStatus;
    }

    public void setStatus(NcrOrderStatus status) {
        mStatus = status;
    }

    public NcrOrderWrappedData toServerData(boolean includePrecalculatedSubtotal) {
        NcrOrderWrappedData ncrOrderWrappedData = new NcrOrderWrappedData();

        NcrOrderApiData ncrOrderApiData = new NcrOrderApiData();
        ncrOrderWrappedData.setDeliveryData(toServerDeliveryData());


        // Load items
        List<NcrOrderLine> lines = new ArrayList<>();
        for (NcrOrderItem ncrOrderItem : getItems()) {
            lines.addAll(ncrOrderItem.toServerData());
        }
        ncrOrderApiData.setNcrOrderLines(lines);

        ncrOrderWrappedData.setNcrOrderApiData(ncrOrderApiData);

        // Add location data
        NcrLocation ncrLocation = new NcrLocation();
        StoreLocation storeLocation = getStoreLocation();
        ncrLocation.setName(storeLocation.getName());
        ncrLocation.setNumber(storeLocation.getId());
        ncrOrderWrappedData.setNcrLocation(ncrLocation);

        if (includePrecalculatedSubtotal) {
            ncrOrderWrappedData.setPrecalculatedSubtotal(getPrecalculatedSubtotal());
        }

        return ncrOrderWrappedData;
    }
}
