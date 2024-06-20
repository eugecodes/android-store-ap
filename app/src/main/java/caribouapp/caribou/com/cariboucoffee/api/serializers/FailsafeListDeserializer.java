package caribouapp.caribou.com.cariboucoffee.api.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;

/**
 * Created by jmsmuy on 11/10/17.
 */

public class FailsafeListDeserializer<T> implements JsonDeserializer<List<T>> {

    private static final String TAG = FailsafeListDeserializer.class.getSimpleName();

    @Override
    public List<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonArray()) {
            return context.deserialize(json, typeOfT);
        }
        Log.e(TAG, new LogErrorException("Error des-serializing fail safe list"));
        return null;
    }

}
