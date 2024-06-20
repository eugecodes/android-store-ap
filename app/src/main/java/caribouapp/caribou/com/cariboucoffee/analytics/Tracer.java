package caribouapp.caribou.com.cariboucoffee.analytics;

import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;

public class Tracer {

    private static final String TAG = Tracer.class.getSimpleName();

    private MetricsLogger mMetricsLogger;

    private String mMetricName;

    private long mStartTime = -1;

    public Tracer(MetricsLogger metricsLogger, String metricName) {
        mMetricsLogger = metricsLogger;
        mMetricName = metricName;
    }

    public Tracer start() {
        if (mStartTime != -1) {
            Log.e(TAG, new LogErrorException("start() called more than once for metric: " + mMetricName));
            return this;
        }
        mStartTime = System.currentTimeMillis();
        return this;
    }

    public void end() {
        if (mStartTime == -1) {
            Log.e(TAG, new LogErrorException("end() called without start() for metric: " + mMetricName));
            return;
        }
        mMetricsLogger.logDuration(mMetricName, System.currentTimeMillis() - mStartTime);
    }
}
