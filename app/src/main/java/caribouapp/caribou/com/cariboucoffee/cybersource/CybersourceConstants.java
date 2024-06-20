package caribouapp.caribou.com.cariboucoffee.cybersource;

/**
 * Created by andressegurola on 11/30/17.
 */

public final class CybersourceConstants {

    private CybersourceConstants() {
    }

    public static final String FIELD_SIGNED_FIELD_NAMES = "signed_field_names";
    public static final String FIELD_UNSIGNED_FIELD_NAMES = "unsigned_field_names";

    public static final String FIELD_SIGNATURE = "signature";
    public static final String FIELD_TRANSACTION_TYPE = "transaction_type";
    public static final String FIELD_AUTH_AVS_CODE = "auth_avs_code";
    public static final String FIELD_DECISION = "decision";
    public static final String FIELD_DECISION_VALUE_ACCEPT = "ACCEPT";

    public static final String FIELD_PAYMENT_TOKEN = "payment_token";
    public static final String FIELD_REQ_PAYMENT_TOKEN = "req_payment_token";

    public static final String FIELD_CARD_TYPE = "req_card_type";
    public static final String FIELD_CARD_NUMBER = "req_card_number";

    public static final String CARD_TYPE_VISA = "001";
    public static final String CARD_TYPE_MASTER = "002";
    public static final String CARD_TYPE_AMEX = "003";
    public static final String CARD_TYPE_DISC = "004";
}
