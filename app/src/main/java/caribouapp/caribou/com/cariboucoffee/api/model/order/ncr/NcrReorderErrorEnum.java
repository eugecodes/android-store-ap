package caribouapp.caribou.com.cariboucoffee.api.model.order.ncr;

public enum NcrReorderErrorEnum {
    PRODUCT_MISSING("product_missing"), MODIFIERS_MISSING("modifier_missing");

    String mNcrReorderErrorType;

    NcrReorderErrorEnum(String brand) {
        mNcrReorderErrorType = brand;
    }

    @Override
    public String toString() {
        return mNcrReorderErrorType;
    }
}
