package caribouapp.caribou.com.cariboucoffee;


import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import caribouapp.caribou.com.cariboucoffee.common.Clock;
import caribouapp.caribou.com.cariboucoffee.cybersource.BillToData;
import caribouapp.caribou.com.cariboucoffee.cybersource.CardData;
import caribouapp.caribou.com.cariboucoffee.cybersource.CybersourceConstants;
import caribouapp.caribou.com.cariboucoffee.cybersource.CybersourceService;
import caribouapp.caribou.com.cariboucoffee.cybersource.TokenRequest;
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.CardEnum;

/**
 * Created by andressegurola on 12/5/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class CybersourceServiceTest {

    private static final String SECRET_KEY = "893fedb118fd449cafbd769382ddf2bf814074b243b549fdb6cf3517273341f8c88cde7a0818474899604d8f25cbec59006cd19c9347482381968310d39a0910790b84e4708242ac8d6404e9fc0a41f656386d608fc044bc82b8dbb58012a0c5ef0d937e2442468ba8fdb76d9b424da6e151e366b9854f70becaf6caddd13c65";
    private static final String ACCESS_KEY = "07aafde2f1fa391fa0ce51bb75f64e36";
    private static final String PROFILE_ID = "SOP0001";

    private CybersourceService mCybersourceService;

    @Before
    public void init() {
        mCybersourceService = new CybersourceService(null, data -> Base64.getEncoder().encodeToString(data), SECRET_KEY, new Clock() {
            @Override
            public LocalTime getCurrentTime() {
                return new LocalTime(10, 20);
            }

            @Override
            public DateTime getCurrentDateTime() {
                return new DateTime(2017, 11, 20, 10, 20, 0, 0);
            }
        }, () -> "670e15ac-b06a-4d26-bdc8-67b818fb177b");
    }

    @Test
    public void testDigitalSignature() throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        String signature = mCybersourceService.sign("This is a test string to the digital signature algorithm. This uses a HMAC SHA256 algorithm to generate a signature in Base64 format.", SECRET_KEY);
        Assert.assertEquals("Kqv11kS490M8/2QbIqMPNC662enLR8zKAgnyfK6Ikv8=", signature);
    }

    /**
     * Tests the signature algorithm for the signed request fields
     *
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     */
    @Test
    public void testPrepareRequest() throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {

        TokenRequest tokenRequest = new TokenRequest(ACCESS_KEY, PROFILE_ID);
        tokenRequest.getSignedFields().put(CybersourceConstants.FIELD_TRANSACTION_TYPE, "authorization,create_payment_token");

        CardData cardData = new CardData();
        cardData.setNumber("4111111111111111");
        cardData.setExpireDate("12-2022");
        cardData.setCvn("005");
        cardData.setCardType(CardEnum.VISA);

        BillToData billToData = new BillToData();
        billToData.setFirstName("John");
        billToData.setLastName("Doe");
        billToData.setEmail("null@cybersource.com");
        billToData.setPhone("02890888888");
        billToData.setAddressLine1("1 Card Lane");
        billToData.setAddressCity("My City");
        billToData.setAddressState("CA");
        billToData.setAddressCountry("US");
        billToData.setAddressPostalCode("94043");

        mCybersourceService.prepRequest(tokenRequest, cardData, billToData);
        String signature = tokenRequest.getFieldMap().get(CybersourceConstants.FIELD_SIGNATURE);
        Assert.assertEquals("7NhZK5JKFWSMKbpUQa8cl1qV968ssKeVLnn/wdvcnjE=", signature);
    }

    /**
     * Tests the signature verification on a server response. This makes sure that the signed field were not temper with.
     */
    @Test
    public void testVerifyResponseSignature() {
        String testData = "utf8=&#x2713;\n" +
                "req_card_number=411111xxxxxx1111\n" +
                "req_locale=en\n" +
                "signature=ixP348B1XKhMLRuydsMRGKApBHtjQym7g8bnCYtBjtY=\n" +
                "auth_trans_ref_no=79635679IAMVC038\n" +
                "payment_token=5124823643406189604107\n" +
                "req_bill_to_surname=Doe\n" +
                "req_bill_to_address_city=My City\n" +
                "req_card_expiry_date=12-2022\n" +
                "req_bill_to_address_postal_code=94043\n" +
                "req_bill_to_phone=02890888888\n" +
                "reason_code=100\n" +
                "auth_amount=1.00\n" +
                "auth_response=100\n" +
                "req_bill_to_forename=John\n" +
                "req_payment_method=card\n" +
                "request_token=Ahj/7wSTFdE3DzLVFi0LESDdy2ZtWzdzJgza0NgzcJceHPNmYClx4c82Z6QHyOVcMmkmW6QHLxnwJyYrom4eZaosWhYA7AQE\n" +
                "auth_time=2017-12-05T135924Z\n" +
                "req_amount=1.00\n" +
                "req_bill_to_email=null@cybersource.com\n" +
                "auth_avs_code_raw=I1\n" +
                "transaction_id=5124823643406189604107\n" +
                "req_currency=USD\n" +
                "req_card_type=001\n" +
                "decision=ACCEPT\n" +
                "message=Request was processed successfully.\n" +
                "signed_field_names=transaction_id,decision,req_access_key,req_profile_id,req_transaction_uuid,req_transaction_type,req_reference_number,req_amount,req_currency,req_locale,req_payment_method,req_bill_to_forename,req_bill_to_surname,req_bill_to_email,req_bill_to_phone,req_bill_to_address_line1,req_bill_to_address_city,req_bill_to_address_state,req_bill_to_address_country,req_bill_to_address_postal_code,req_card_number,req_card_type,req_card_expiry_date,message,reason_code,auth_avs_code,auth_avs_code_raw,auth_response,auth_amount,auth_code,auth_trans_ref_no,auth_time,request_token,payment_token,signed_field_names,signed_date_time\n" +
                "req_transaction_uuid=670e15ac-b06a-4d26-bdc8-67b818fb177b\n" +
                "auth_avs_code=X\n" +
                "auth_code=888888\n" +
                "req_bill_to_address_country=US\n" +
                "req_transaction_type=authorization,create_payment_token\n" +
                "req_access_key=07aafde2f1fa391fa0ce51bb75f64e36\n" +
                "req_profile_id=SOP0001\n" +
                "req_reference_number=1512482361829\n" +
                "req_bill_to_address_state=CA\n" +
                "signed_date_time=2017-12-05T13:59:24Z\n" +
                "req_bill_to_address_line1=1 Card Lane\n" +
                "commit=Continue";

        String[] fieldValues = testData.split("\n");
        Map<String, String> fields = new HashMap<>();
        for (String fieldValue : fieldValues) {
            String[] parts = fieldValue.split("[=]");
            fields.put(parts[0], fieldValue.substring(parts[0].length() + 1));
        }

        Assert.assertTrue(mCybersourceService.validateResponseSignature(fields));
    }
}
