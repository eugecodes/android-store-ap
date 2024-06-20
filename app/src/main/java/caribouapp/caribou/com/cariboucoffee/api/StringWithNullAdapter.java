package caribouapp.caribou.com.cariboucoffee.api;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import caribouapp.caribou.com.cariboucoffee.api.model.StringWithNull;

/**
 * Created by andressegurola on 12/6/17.
 */

public class StringWithNullAdapter extends TypeAdapter<StringWithNull> {

    @Override
    public void write(JsonWriter out, StringWithNull value) throws IOException {
        if (value.getString() == null) {
            boolean serializeNulls = out.getSerializeNulls();
            out.setSerializeNulls(true);
            out.nullValue();
            out.setSerializeNulls(serializeNulls);
        } else {
            out.value(value.getString());
        }
    }

    @Override
    public StringWithNull read(JsonReader in) throws IOException {
        return new StringWithNull(in.peek() == JsonToken.NULL ? null : in.nextString());
    }
}
