package caribouapp.caribou.com.cariboucoffee.cybersource;

import androidx.annotation.NonNull;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Singleton;

import caribouapp.caribou.com.cariboucoffee.BuildConfig;
import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.common.ResultCallback;
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.CardEnum;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import caribouapp.caribou.com.cariboucoffee.util.LogErrorException;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by andressegurola on 11/24/17.
 * <p>
 * Service to access Cybersource services. Particularly payment token creation.
 * This is based on the "CyberSource Secure Acceptance Silent Order POST" API document from Cybersource.
 */
@Singleton
public class CybersourceService {

    public static final int ERROR_CODE_REQUEST_NOT_APROOVED = 10;
    public static final int ERROR_CODE_INVALIDE_SIGNATURE = 11;
    private static final String TAG = CybersourceService.class.getSimpleName();
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final Set<Character> APPROVED_AVS_CODES = new HashSet<>(Arrays.asList('D', 'H', 'J', 'M', 'Q', 'V', 'W', 'X', 'Y', 'Z'));
    private final OkHttpClient mHttpClient;

    private final Base64Encoder mBase64Encoder;
    private final String mSecretKey;
    private final Clock mClock;
    private final UUIDGenerator mUUIDGenerator;

    public CybersourceService(OkHttpClient httpClient, Base64Encoder base64Encoder, String secretKey, Clock clock, UUIDGenerator uuidGenerator) {
        mHttpClient = httpClient;
        mBase64Encoder = base64Encoder;
        mSecretKey = secretKey;
        mClock = clock;
        mUUIDGenerator = uuidGenerator;
    }

    /**
     * Allows the creating of a new payment token from Cybersource
     *
     * @param cardData
     * @param billToData
     * @param callback
     */
    public void createToken(CardData cardData, BillToData billToData, ResultCallback<TokenResponse> callback) {
        TokenRequest tokenRequest = new TokenRequest(BuildConfig.CYBERSOURCE_ACCESS_KEY, BuildConfig.CYBERSOURCE_PROFILE_ID);
        tokenRequest.getSignedFields().put(CybersourceConstants.FIELD_TRANSACTION_TYPE, "authorization,create_payment_token");
        tokenRequest(tokenRequest, cardData, billToData, callback);
    }

    /**
     * Allows updating an existing payment token
     *
     * @param existingToken
     * @param cardData
     * @param billToData
     * @param callback
     */
    public void updateToken(String existingToken, CardData cardData, BillToData billToData, ResultCallback<TokenResponse> callback) {
        TokenRequest tokenRequest = new TokenRequest(BuildConfig.CYBERSOURCE_ACCESS_KEY, BuildConfig.CYBERSOURCE_PROFILE_ID);
        tokenRequest.getSignedFields().put(CybersourceConstants.FIELD_TRANSACTION_TYPE, "authorization,update_payment_token");
        tokenRequest.getSignedFields().put(CybersourceConstants.FIELD_PAYMENT_TOKEN, existingToken);
        tokenRequest(tokenRequest, cardData, billToData, callback);
    }

    /**
     * Prepares the fields to be used for the Cybersource request. Mainly setting up some reference
     * values and calculating the signature for the signed fields.
     *
     * @param tokenRequest
     * @param cardData
     * @param billToData
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     */
    public void prepRequest(TokenRequest tokenRequest, CardData cardData, BillToData billToData)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        tokenRequest.getUnsignedFields().putAll(cardData.asMap());
        tokenRequest.getSignedFields().putAll(billToData.asMap());

        FieldList signed = tokenRequest.getSignedFields();

        signed.put("transaction_uuid", mUUIDGenerator.createUUID());

        DateTime now = mClock.getCurrentDateTime();
        signed.put("reference_number", String.valueOf(now.getMillis()));

        // Iso date format without the milliseconds sections
        // TODO find another way to remove seconds from the date format
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime().withZoneUTC();
        signed.put("signed_date_time", fmt.print(now).replaceFirst("[.][0-9]*", ""));

        signed.put(CybersourceConstants.FIELD_UNSIGNED_FIELD_NAMES, tokenRequest.getUnsignedFields().getValidFieldList());
        signed.put(CybersourceConstants.FIELD_SIGNED_FIELD_NAMES, tokenRequest.getSignedFields().getValidFieldList());
        signed.put(CybersourceConstants.FIELD_SIGNATURE, sign(tokenRequest.getSignedFields().getData()));
        Log.d(TAG, "Request signature: " + signed.getData().get(CybersourceConstants.FIELD_SIGNATURE));
    }

    /**
     * Executes the actual Cybersource API Call.
     *
     * @param tokenRequest
     * @param cardData
     * @param billToData
     * @param callback
     */
    private void tokenRequest(TokenRequest tokenRequest, CardData cardData, BillToData billToData, ResultCallback<TokenResponse> callback) {

        try {
            prepRequest(tokenRequest, cardData, billToData);
        } catch (Exception e) {
            throw new RuntimeException("Problems preparing Cybersource request data", e);
        }

        // Create request body
        FormBody.Builder formBody = new FormBody.Builder();
        List<Map.Entry<String, String>> bodyParams = new ArrayList<>(tokenRequest.getFieldMap().entrySet());
        Collections.sort(bodyParams, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
        for (Map.Entry<String, String> field : bodyParams) {
            formBody.add(field.getKey(), field.getValue() == null ? "" : field.getValue());
        }

        Request request = new Request.Builder()
                .url(BuildConfig.CYBERSOURCE_URL)
                .post(formBody.build())
                .header("connection", "keep-alive")
                .header("X-Requested-With", "")
                .header("accept", "text/html")
                .build();

        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, new LogErrorException("Http Request Failed", e));
                callback.onError(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                processTokenResponse(response, callback);
            }
        });
    }

    private void processTokenResponse(Response response, ResultCallback<TokenResponse> callback) throws IOException {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Response Headers: " + response.headers().toString());
            Log.d(TAG, "Response Code: " + response.code());
            Log.d(TAG, "Response Message: " + response.message());
        }

        if (!response.isSuccessful()) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Response Body: " + response.body().string());
            }
            callback.onFail(response.code(), response.message());
            return;
        }

        Map<String, String> fields = processResponse(response);

        boolean validResponse = validateResponseSignature(fields);
        if (!validResponse) {
            // Validation of the response data failed
            callback.onFail(ERROR_CODE_INVALIDE_SIGNATURE, StringUtils.format("Invalid response signature\n%s", StringUtils.mapToString(fields)));
            return;
        }

        // Check Cybersource decision
        String decisionValue = fields.get(CybersourceConstants.FIELD_DECISION);
        if (decisionValue == null || !decisionValue.equalsIgnoreCase(CybersourceConstants.FIELD_DECISION_VALUE_ACCEPT)) {
            // The token request was not approved by Cybersource
            callback.onFail(ERROR_CODE_REQUEST_NOT_APROOVED,
                    StringUtils.format("Auth not accepted\n%s", StringUtils.mapToString(fields)));
            return;
        }

        // Check AVS code
        String avsCode = fields.get(CybersourceConstants.FIELD_AUTH_AVS_CODE);
        if (avsCode == null || !APPROVED_AVS_CODES.contains(avsCode.charAt(0))) {
            // The token request was approved by Cybersource but we decide to discard it becouse of the AVS code returned.
            callback.onFail(ERROR_CODE_REQUEST_NOT_APROOVED,
                    StringUtils.format("Token request not approved. AVSCODE: %s\n%s", avsCode, StringUtils.mapToString(fields)));
            return;
        }

        // Build token result
        TokenResponse tokenResponse = new TokenResponse();
        String paymentToken = fields.get(CybersourceConstants.FIELD_PAYMENT_TOKEN);
        if (paymentToken == null) {
            paymentToken = fields.get(CybersourceConstants.FIELD_REQ_PAYMENT_TOKEN);
        }
        tokenResponse.setPaymentToken(paymentToken);
        tokenResponse.setMaskedCardNumber(fields.get(CybersourceConstants.FIELD_CARD_NUMBER));
        tokenResponse.setCardType(CardEnum.fromCybersourceCode(fields.get(CybersourceConstants.FIELD_CARD_TYPE)));
        callback.onSuccess(tokenResponse);
    }

    /**
     * Validate the signature of a set of fields
     *
     * @param originalFields
     * @return
     */
    public boolean validateResponseSignature(Map<String, String> originalFields) {
        try {
            Map<String, String> signedFields = new HashMap<>();
            String[] signedFieldNames = originalFields.get(CybersourceConstants.FIELD_SIGNED_FIELD_NAMES).split("[,]");
            for (String signedFieldName : signedFieldNames) {
                signedFields.put(signedFieldName, originalFields.get(signedFieldName));
            }

            String calculatedSignature = sign(signedFields);
            String responseSignature = originalFields.get(CybersourceConstants.FIELD_SIGNATURE);

            Log.d(TAG, "Comparing signatures calculated: " + calculatedSignature + " | response: " + responseSignature);
            return calculatedSignature.equals(responseSignature);
        } catch (Exception e) {
            Log.e(TAG, new LogErrorException("Error validating response", e));
            return false;
        }
    }

    /**
     * Parses out the field (name,value) pairs from the http response from Cybersource
     *
     * @param response
     * @return
     * @throws IOException
     */
    public Map<String, String> processResponse(Response response) throws IOException {
        Map<String, String> fields = new HashMap<>();

        String replyBody = response.body().string();

        // This pattern will match with input xml nodes in the response.
        Pattern pattern = Pattern.compile("[<]input.+name[=][\"]([^\"]*)[\"].+value=[\"]([^\"]*)[\"](.*)");

        Matcher matcher = pattern.matcher(replyBody);

        while (matcher.find()) {
            String fieldName = matcher.group(1);
            String fieldValue = matcher.group(2);
            fields.put(fieldName, fieldValue);
            Log.d(TAG, "Response Field " + fieldName + "=" + fieldValue);
        }

        return fields;
    }

    private String sign(Map<String, String> params) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        return sign(buildDataToSign(params), mSecretKey);
    }

    public String sign(String data, String secretKey) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), HMAC_SHA256);
        Mac mac = Mac.getInstance(HMAC_SHA256);
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes("UTF-8"));
        return mBase64Encoder.toBase64(rawHmac).replaceAll("\n", "");
    }

    private String buildDataToSign(Map<String, String> params) {
        String[] signedFieldNames = params.get(CybersourceConstants.FIELD_SIGNED_FIELD_NAMES).split(",");
        List<String> dataToSign = new ArrayList<>();
        for (String signedFieldName : signedFieldNames) {
            dataToSign.add(signedFieldName + "=" + String.valueOf(params.get(signedFieldName)));
        }
        return StringUtils.joinWith(dataToSign, ",");
    }
}
