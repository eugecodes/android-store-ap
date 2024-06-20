package caribouapp.caribou.com.cariboucoffee.api.model;

/**
 * Created by andressegurola on 12/6/17.
 * This class is used with Gson to allow the serialization of null values for specific fields.
 */
public class StringWithNull {
    private String mString;

    public StringWithNull(String string) {
        mString = string;
    }

    public String getString() {
        return mString;
    }

    public void setString(String string) {
        mString = string;
    }
}
