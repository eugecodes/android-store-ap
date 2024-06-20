package caribouapp.caribou.com.cariboucoffee.analytics;

public enum JoinLoginType {
    EMAIL("email"),
    GOOGLE("google");

    private String mLoginTypeValue;

    JoinLoginType(String loginTypeValue) {
        mLoginTypeValue = loginTypeValue;
    }

    public String getLoginTypeValue() {
        return mLoginTypeValue;
    }
}
