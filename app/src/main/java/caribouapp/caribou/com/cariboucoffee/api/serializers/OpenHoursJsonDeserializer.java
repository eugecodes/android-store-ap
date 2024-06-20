package caribouapp.caribou.com.cariboucoffee.api.serializers;

import android.text.TextUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.HashMap;

import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextOpenHours;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;

/**
 * Created by jmsmuy on 5/2/18.
 */

public class OpenHoursJsonDeserializer implements JsonDeserializer<YextOpenHours> {

    private static final String TAG = OpenHoursJsonDeserializer.class.getSimpleName();

    @Override
    public YextOpenHours deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            String value = json.getAsString();
            if (TextUtils.isEmpty(value)) {
                return null;
            }
            YextOpenHours yextOpenHours = new YextOpenHours();
            yextOpenHours.setOpenHours(new HashMap<>());
            String[] periods = value.split(",");
            for (String period : periods) {
                yextOpenHours.getOpenHours().put(getIndex(period), PeriodUtil.getTimePeriodFromString(getTimePeriodString(period)));
            }
            if (yextOpenHours.getOpenHours() == null || yextOpenHours.getOpenHours().size() == 0) {
                Log.e(TAG, new LogErrorException("Error des-serializing " + TAG + " " + value));
            }
            return yextOpenHours;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, new LogErrorException("Error des-serializing " + TAG, e));
            return null;
        }
    }

    private String getTimePeriodString(String period) {
        int position = period.indexOf(":");
        if (position < 0) {
            return null;
        }
        return period.substring(position + 1, period.length());
    }

    private Integer getIndex(String period) {
        String[] splitPeriod = period.split(":");
        if (splitPeriod.length != 5) {
            return null;
        }
        return Integer.valueOf(splitPeriod[0]);
    }
}
