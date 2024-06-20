package caribouapp.caribou.com.cariboucoffee.mvp.dashboard;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.urbanairship.messagecenter.Inbox;
import com.urbanairship.messagecenter.InboxListener;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class UAirshipInboxUnreadCounterObserver implements LifecycleObserver {

    private static final long FETCH_MESSAGES_INTERVAL_IN_MILLIS = TimeUnit.SECONDS.toMillis(10);

    private Inbox mRichPushInbox;
    private OnNewInboxUnreadCountAvailableListener mOnNewInboxUnreadCountAvailableListener;
    private InboxListener mInboxListener = () -> mOnNewInboxUnreadCountAvailableListener
            .onNewInboxUnreadCountAvailable(mRichPushInbox.getUnreadCount());
    private Timer mTimer;

    public UAirshipInboxUnreadCounterObserver(Inbox richPushInbox,
                                              OnNewInboxUnreadCountAvailableListener onNewInboxUnreadCountAvailableListener) {
        mRichPushInbox = richPushInbox;
        mOnNewInboxUnreadCountAvailableListener = onNewInboxUnreadCountAvailableListener;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void subscribe() {
        mOnNewInboxUnreadCountAvailableListener.onNewInboxUnreadCountAvailable(mRichPushInbox.getUnreadCount());
        mRichPushInbox.addListener(mInboxListener);
        mTimer = new Timer();
        mTimer.schedule(new UATimerTask(mRichPushInbox), 0, FETCH_MESSAGES_INTERVAL_IN_MILLIS);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void unsubscribe() {
        mTimer.cancel();
        mRichPushInbox.removeListener(mInboxListener);
    }

    public interface OnNewInboxUnreadCountAvailableListener {
        void onNewInboxUnreadCountAvailable(int count);
    }

    private static class UATimerTask extends TimerTask {
        private Inbox mRichPushInbox;

        UATimerTask(Inbox richPushInbox) {
            mRichPushInbox = richPushInbox;
        }

        @Override
        public void run() {
            mRichPushInbox.fetchMessages();
        }
    }
}
