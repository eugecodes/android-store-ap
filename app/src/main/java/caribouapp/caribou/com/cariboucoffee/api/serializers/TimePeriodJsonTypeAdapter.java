package caribouapp.caribou.com.cariboucoffee.api.serializers;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import caribouapp.caribou.com.cariboucoffee.api.model.yext.YextTimePeriod;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;

/**
 * Created by jmsmuy on 11/10/17.
 */

public class TimePeriodJsonTypeAdapter extends TypeAdapter<YextTimePeriod> {

    private static final String TAG = TimePeriodJsonTypeAdapter.class.getSimpleName();


    @Override
    public void write(JsonWriter out, YextTimePeriod value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.toString());
        }
    }

    @Override
    public YextTimePeriod read(JsonReader in) throws IOException {
        try {
            if (in.peek() == JsonToken.NULL) {
                return null;
            } else if (in.peek() != JsonToken.STRING) {
                Log.e(TAG, new LogErrorException("Error des-serializing " + YextTimePeriod.class.getSimpleName()));
                return null;
            }

            String value = in.nextString();
            YextTimePeriod period = PeriodUtil.getTimePeriodFromString(value);
            if (period == null) {
                Log.e(TAG, new LogErrorException("Error des-serializing " + YextTimePeriod.class.getSimpleName() + " " + value));
            }
            return period;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, new LogErrorException("Error des-serializing " + YextTimePeriod.class.getSimpleName()));
            return null;
        }
    }
}
