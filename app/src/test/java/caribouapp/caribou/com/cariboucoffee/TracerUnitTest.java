package caribouapp.caribou.com.cariboucoffee;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.longThat;
import static org.mockito.Mockito.verify;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import caribouapp.caribou.com.cariboucoffee.analytics.MetricsLogger;
import caribouapp.caribou.com.cariboucoffee.analytics.Tracer;

@RunWith(MockitoJUnitRunner.class)
public class TracerUnitTest {

    @Mock
    private MetricsLogger mMetricsLogger;

    @Test
    public void testReportDuration() {
        final String metricId1 = "MetricId1";
        Tracer tracer = new Tracer(mMetricsLogger, metricId1);
        tracer.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Assert.fail();
        }
        tracer.end();

        verify(mMetricsLogger).logDuration(eq(metricId1),
                longThat(argument -> {
                    long timeTracked = argument;
                    return timeTracked >= 500
                            && timeTracked <= 650;
                }));

    }
}
