package caribouapp.caribou.com.cariboucoffee.common;

/**
 * Created by fernando on 9/30/20.
 */
public class OptionItem<T> {

    private T mValue;
    private String mDescription;
    private String mContentDescription;

    public OptionItem(T value, String description, String contentDescription) {
        mValue = value;
        mDescription = description;
        mContentDescription = contentDescription;
    }

    public T getValue() {
        return mValue;
    }

    public void setValue(T value) {
        mValue = value;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getContentDescription() {
        return mContentDescription;
    }

    public void setContentDescription(String contentDescription) {
        mContentDescription = contentDescription;
    }
}
