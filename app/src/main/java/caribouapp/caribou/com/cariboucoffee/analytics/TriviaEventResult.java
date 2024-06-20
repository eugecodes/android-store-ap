package caribouapp.caribou.com.cariboucoffee.analytics;

public enum TriviaEventResult {
    CORRECT_ANSWER("correct_answer"),
    WRONG_ANSWER("wrong_answer"),
    TIMEOUT("timeout");

    private String mAnalyticsName;

    TriviaEventResult(String analyticsName) {
        mAnalyticsName = analyticsName;
    }

    public String getAnalyticsName() {
        return mAnalyticsName;
    }
}
