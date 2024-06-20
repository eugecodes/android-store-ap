package caribouapp.caribou.com.cariboucoffee.api.model.order.ncr;

import com.google.gson.annotations.SerializedName;

public class NcrValueType<T> {

    public NcrValueType() {
    }

    public NcrValueType(T value) {
        mValue = value;
    }

    @SerializedName(value = "Value", alternate = "value")
    private T mValue;

    public T getValue() {
        return mValue;
    }

    public void setValue(T value) {
        mValue = value;
    }
}
