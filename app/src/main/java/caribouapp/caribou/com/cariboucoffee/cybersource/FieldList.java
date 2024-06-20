package caribouapp.caribou.com.cariboucoffee.cybersource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.util.StringUtils;

/**
 * Created by andressegurola on 11/24/17.
 */

public class FieldList {

    private Map<String, String> mFields = new HashMap<>();

    public String getValidFieldList() {
        List<String> fields = new ArrayList<>(mFields.keySet());
        Collections.sort(fields);
        return StringUtils.joinWith(fields, ",");
    }

    public void putAll(Map<String, String> keyValuePairs) {
        mFields.putAll(keyValuePairs);
    }

    public void put(String key, String value) {
        mFields.put(key, value);
    }

    public Map<String, String> getData() {
        return mFields;
    }
}
