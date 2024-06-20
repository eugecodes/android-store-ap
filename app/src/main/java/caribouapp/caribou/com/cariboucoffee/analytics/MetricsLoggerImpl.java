package caribouapp.caribou.com.cariboucoffee.analytics;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.newrelic.agent.android.NewRelic;

import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;

public class MetricsLoggerImpl implements MetricsLogger {

    private static final String TAG = MetricsLoggerImpl.class.getSimpleName();

    private static final String NEW_RELIC_METRIC_CATEGORY_CUSTOM = "Custom";
    private static final String FIREBASE_ANALYTICS_EVENT_PARAM_DURATION = "duration";

    private final FirebaseAnalytics mFirebaseAnalytics;

    public MetricsLoggerImpl(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @Override
    public void logDuration(String metricName, long duration) {
        logDurationNewRelic(metricName, duration);
        logDurationFirebase(metricName, duration);
    }

    private void logDurationNewRelic(String metricName, long duration) {
        try {
            if (!NewRelic.isStarted()) {
                Log.d(TAG, "Ignoring NewRelic custom metric: " + metricName);
                return;
            }
            NewRelic.recordMetric(metricName, NEW_RELIC_METRIC_CATEGORY_CUSTOM, duration);
        } catch (RuntimeException e) {
            Log.e(TAG, new LogErrorException("Problems logging NewRelic duration for metric: " + metricName, e));
        }
    }

    private void logDurationFirebase(String metricName, long duration) {
        try {
            Bundle bundle = new Bundle();
            bundle.putLong(FIREBASE_ANALYTICS_EVENT_PARAM_DURATION, duration);
            mFirebaseAnalytics.logEvent(metricName, bundle);
        } catch (RuntimeException e) {
            Log.e(TAG, new LogErrorException("Problems logging Firebase Analytics duration for metric: " + metricName, e));
        }
    }
}
