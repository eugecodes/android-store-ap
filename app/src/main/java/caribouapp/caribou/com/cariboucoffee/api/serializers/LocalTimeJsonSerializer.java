package caribouapp.caribou.com.cariboucoffee.api.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.lang.reflect.Type;

import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;

/**
 * Created by jmsmuy on 11/10/17.
 */

public class LocalTimeJsonSerializer implements JsonDeserializer<LocalTime>, JsonSerializer<LocalTime> {

    private static final String[] TIME_PATTERNS = {
            "HH:mm:ss.SSS",
            "HH:mm"
    };
    private static final String TAG = LocalTimeJsonSerializer.class.getSimpleName();

    @Override
    public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        boolean failed = true;
        LocalTime result = null;
        for (String pattern : TIME_PATTERNS) {
            try {
                result = DateTimeFormat.forPattern(pattern).parseLocalTime(json.getAsString());
                failed = false;
                break;
            } catch (IllegalArgumentException ex) {
                // NO-OP
            }
        }
        if (failed) {
            Log.e(TAG, new LogErrorException("Error des-serializing " + LocalTime.class.getSimpleName() + " " + json.getAsString()));
        }
        return result;

    }

    @Override
    public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(DateTimeFormat.forPattern(TIME_PATTERNS[0]).print(src));
    }
}
