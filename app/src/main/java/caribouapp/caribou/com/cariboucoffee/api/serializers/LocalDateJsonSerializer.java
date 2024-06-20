package caribouapp.caribou.com.cariboucoffee.api.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.lang.reflect.Type;
import java.util.Locale;

import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;

/**
 * Created by jmsmuy on 11/10/17.
 */

public class LocalDateJsonSerializer implements JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {

    private static final String TAG = LocalDateJsonSerializer.class.getSimpleName();

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(json.getAsString()).toLocalDate();
            } catch (IllegalArgumentException e) {
                Log.e(TAG, new LogErrorException("Error des-serializing " + LocalDate.class.getSimpleName() + " " + json.getAsString()));
                return null;
            }
    }

    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive((DateTimeFormat.forPattern("yyyy-MM-dd")).withLocale(Locale.US).print(src));
    }
}
