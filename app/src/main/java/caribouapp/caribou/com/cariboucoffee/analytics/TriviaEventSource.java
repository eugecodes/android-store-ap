package caribouapp.caribou.com.cariboucoffee.analytics;

public enum TriviaEventSource {
    Design1("design1"),
    Design2("design2"),
    Deeplink("deeplink");

    private String mAnalyticsName;

    TriviaEventSource(String analyticsName) {
        mAnalyticsName = analyticsName;
    }

    public String getAnalyticsName() {
        return mAnalyticsName;
    }
}
