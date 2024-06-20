package caribouapp.caribou.com.cariboucoffee.common;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import caribouapp.caribou.com.cariboucoffee.mvp.MvpView;

public class PeriodicUITask {

    private ScheduledExecutorService mExecutorService;
    private long mPeriod;
    private TimeUnit mTimeUnit;
    private WeakReference<MvpView> mViewRef;
    private Runnable mTask;

    public PeriodicUITask(MvpView view, long period, TimeUnit periodTimeUnit, Runnable uiTask) {
        mViewRef = new WeakReference<>(view);
        mPeriod = period;
        mTimeUnit = periodTimeUnit;
        mTask = uiTask;
    }

    public void setEnabled(boolean enabled) {
        if (enabled && mExecutorService == null) {
            mExecutorService = Executors.newSingleThreadScheduledExecutor();
            mExecutorService.scheduleAtFixedRate(() -> {
                if (mViewRef == null || mViewRef.get() == null) {
                    return;
                }
                mViewRef.get().runOnUiThread(() -> mTask.run());
            }, 0, mPeriod, mTimeUnit);
        } else if (!enabled && mExecutorService != null) {
            mExecutorService.shutdownNow();
            mExecutorService = null;
        }
    }
}
