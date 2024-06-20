package caribouapp.caribou.com.cariboucoffee.analytics;

public interface MetricsLogger {
    void logDuration(String metricName, long duration);
}
