package caribouapp.caribou.com.cariboucoffee.mvp.oos.cart;

import java.math.BigDecimal;

import caribouapp.caribou.com.cariboucoffee.mvp.itemcustomization.model.OrderItem;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.OOSFlowContract;
import caribouapp.caribou.com.cariboucoffee.order.Order;

/**
 * Created by asegurola on 4/6/18.
 */

public interface CartContract {
    interface View extends OOSFlowContract.View {

        void displayOrder(Order order);

        void showErrorOrderNotComplete();

        void updateItem(OrderItem orderItem);

        void updateItemRemoved(OrderItem orderItem);

        void updateSubtotal(BigDecimal newSubtotal);

        void goToPreviousScreen();

        void goToCheckout();

        void showBulkOrderDialog();

        void showNoItemsMessage();

        void showQuantityLimitDialog();

        void showStoreNearClosingForBulk();

        void showMaxQuantityHasChangedDialog();

        void showFreeItemsOnlyNotAllowedDialog(String message);
    }

    interface Presenter extends OOSFlowContract.Presenter {

        void init();

        void loadData();

        void updateItemQuantity(OrderItem orderItem, int newQuantity);

        void removeItem(OrderItem orderItem);

        void checkout();

        void orderTimeoutEnabled(boolean enabled);

        void discardOrder();

        void cancelOrder();

        void removeReward();
    }
}
