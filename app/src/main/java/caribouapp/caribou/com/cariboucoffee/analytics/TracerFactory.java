package caribouapp.caribou.com.cariboucoffee.analytics;

public class TracerFactory {

    private final MetricsLogger mMetricsLogger;

    public TracerFactory(MetricsLogger metricsLogger) {
        mMetricsLogger = metricsLogger;
    }

    public Tracer createTracer(String metricName) {
        return new Tracer(mMetricsLogger, metricName);
    }
}
