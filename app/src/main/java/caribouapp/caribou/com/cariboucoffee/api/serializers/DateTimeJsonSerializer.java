package caribouapp.caribou.com.cariboucoffee.api.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.lang.reflect.Type;

import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;

/**
 * Created by jmsmuy on 11/10/17.
 */

public class DateTimeJsonSerializer implements JsonDeserializer<DateTime>, JsonSerializer<DateTime> {


    //FIXME if you add a new date pattern don't modify the current index of the array
    private static final String[] DATE_PATTERNS = {
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyyMMdd'T'HHmmss",
            "yyyy-MM-dd' 'HH:mm:ss' UTC'",
            "YYYY-MM-dd'T'HH:mm:ssZZ"
    };
    private static final String TAG = DateTimeJsonSerializer.class.getSimpleName();

    @Override
    public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        boolean failed = true;
        DateTime result = null;
        for (String pattern : DATE_PATTERNS) {
            try {
                result = DateTimeFormat.forPattern(pattern).parseDateTime(json.getAsString());
                failed = false;
                break;
            } catch (IllegalArgumentException ex) {
                // NO-OP
            }
        }
        if (failed) {
            Log.e(TAG, new LogErrorException("Error des-serializing " + DateTime.class.getSimpleName() + " " + json.getAsString()));
        }
        return result;

    }

    @Override
    public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
        // TODO did this for the time being since the only serialization we are doing is on the OMS place order endpoint
        return new JsonPrimitive(DateTimeFormat.forPattern(DATE_PATTERNS[3]).print(src));
    }
}
