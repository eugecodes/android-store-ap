package caribouapp.caribou.com.cariboucoffee.analytics;

import caribouapp.caribou.com.cariboucoffee.AppDataStorage;
import caribouapp.caribou.com.cariboucoffee.order.Order;
import caribouapp.caribou.com.cariboucoffee.order.OrderDropOffListener;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;

public class OrderDropOffListenerImpl implements OrderDropOffListener {

    private static final String TAG = OrderDropOffListenerImpl.class.getSimpleName();

    private AppDataStorage mAppDataStorage;
    private EventLogger mEventLogger;

    public OrderDropOffListenerImpl(AppDataStorage appDataStorage, EventLogger eventLogger) {
        mAppDataStorage = appDataStorage;
        mEventLogger = eventLogger;
    }

    @Override
    public void orderDropOff(Order order) {
        try {
            mEventLogger.logOrderDiscarded(mAppDataStorage.getOrderLastScreen(), order.isFromReorder());
        } catch (RuntimeException e) {
            Log.e(TAG, new LogErrorException("Error sending orderDropOff event", e));
        }
    }
}
