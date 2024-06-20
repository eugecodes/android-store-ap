package caribouapp.caribou.com.cariboucoffee.cybersource;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by andressegurola on 11/24/17.
 */

public class TokenRequest {
    private FieldList signedFields = new FieldList();
    private FieldList unsignedFields = new FieldList();

    public TokenRequest(String accessKey, String profileId) {
        // Load defaults
        signedFields.put("currency", "USD");
        signedFields.put("amount", "1.00");
        signedFields.put("locale", "en");
        signedFields.put("access_key", accessKey);
        signedFields.put("profile_id", profileId);
        signedFields.put("payment_method", "card");
        signedFields.put("signed_field_names", null);
    }

    public FieldList getSignedFields() {
        return signedFields;
    }

    public void setSignedFields(FieldList signedFields) {
        this.signedFields = signedFields;
    }

    public FieldList getUnsignedFields() {
        return unsignedFields;
    }

    public void setUnsignedFields(FieldList unsignedFields) {
        this.unsignedFields = unsignedFields;
    }

    public Map<String, String> getFieldMap() {
        Map<String, String> allFields = new HashMap<>();
        allFields.putAll(signedFields.getData());
        allFields.putAll(unsignedFields.getData());
        return allFields;
    }
}
