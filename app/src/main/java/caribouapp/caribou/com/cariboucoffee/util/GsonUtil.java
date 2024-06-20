package caribouapp.caribou.com.cariboucoffee.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.api.StringWithNullAdapter;
import caribouapp.caribou.com.cariboucoffee.api.model.StringWithNull;
import caribouapp.caribou.com.cariboucoffee.api.serializers.DateTimeJsonSerializer;
import caribouapp.caribou.com.cariboucoffee.api.serializers.LocalDateJsonSerializer;
import caribouapp.caribou.com.cariboucoffee.api.serializers.LocalTimeJsonSerializer;

/**
 * Created by andressegurola on 1/9/18.
 */

public final class GsonUtil {

    private GsonUtil() {
    }

    public static final String TAG = GsonUtil.class.getSimpleName();

    private static Gson mGsonInstance;

    /**
     * Returns default Gson
     *
     * @return
     */
    public static Gson defaultGson() {
        if (mGsonInstance == null) {
            TypeAdapter<Boolean> booleanTypeAdapter = new TypeAdapter<Boolean>() {
                @Override
                public void write(JsonWriter out, Boolean value) throws IOException {
                    out.value(value);
                }

                @Override
                public Boolean read(JsonReader in) throws IOException {
                    if (in.peek().equals(JsonToken.STRING)) {
                        String value = in.nextString();
                        return "1".equals(value) || "true".equals(value);
                    } else if (in.peek().equals(JsonToken.BOOLEAN)) {
                        return in.nextBoolean();
                    }
                    in.skipValue();
                    return false;
                }
            };

            mGsonInstance = new GsonBuilder()
                    .registerTypeAdapter(StringWithNull.class, new StringWithNullAdapter())
                    .registerTypeAdapter(DateTime.class, new DateTimeJsonSerializer())
                    .registerTypeAdapter(LocalTime.class, new LocalTimeJsonSerializer())
                    .registerTypeAdapter(boolean.class, booleanTypeAdapter)
                    .registerTypeAdapter(Boolean.class, booleanTypeAdapter)
                    .registerTypeAdapter(LocalDate.class, new LocalDateJsonSerializer())
                    .create();
        }
        return mGsonInstance;
    }

    public static void saveObjectToFile(File outputFile, Object object) {
        try {
            FileWriter writer = new FileWriter(outputFile.getAbsoluteFile());
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(GsonUtil.defaultGson().toJson(object));
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readObjectFromFile(File inputFile, Class<T> classType) {
        try {
            return GsonUtil.defaultGson().fromJson(new FileReader(inputFile), classType);
        } catch (Exception e) {
            Log.e(TAG, new LogErrorException("Error deserializing data", e));
            throw new RuntimeException(e);
        }
    }

    public static <T> T readObjectFromClasspath(String fileName, Type type) {
        return GsonUtil.defaultGson().fromJson(new InputStreamReader(GsonUtil.class.getResourceAsStream(fileName)), type);
    }

    public static <T> List<T> readArrayFromClasspath(String fileName, Type typeOfT) {
        return GsonUtil.defaultGson().fromJson(new InputStreamReader(GsonUtil.class.getResourceAsStream(fileName)), typeOfT);
    }

    public static <T> List<T> readArrayFromClasspath(Reader reader, Type typeOfT) {
        return GsonUtil.defaultGson().fromJson(reader, typeOfT);
    }
}
