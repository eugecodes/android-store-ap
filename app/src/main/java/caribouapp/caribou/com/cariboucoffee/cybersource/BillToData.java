package caribouapp.caribou.com.cariboucoffee.cybersource;

import java.util.Map;

/**
 * Created by andressegurola on 11/24/17.
 */

public class BillToData extends FieldList {

    public void setFirstName(String firstName) {
        getData().put("bill_to_forename", firstName);
    }

    public void setLastName(String lastName) {
        getData().put("bill_to_surname", lastName);
    }

    public void setEmail(String email) {
        getData().put("bill_to_email", email);
    }

    public void setPhone(String phone) {
        getData().put("bill_to_phone", phone);
    }

    public void setAddressLine1(String addressLine1) {
        getData().put("bill_to_address_line1", addressLine1);
    }

    public void setAddressLine2(String addressLine2) {
        getData().put("bill_to_address_line2", addressLine2);
    }

    public void setAddressCity(String addressCity) {
        getData().put("bill_to_address_city", addressCity);
    }

    public void setAddressState(String addressState) {
        getData().put("bill_to_address_state", addressState);
    }

    public void setAddressCountry(String addressCountry) {
        getData().put("bill_to_address_country", addressCountry);
    }

    public void setAddressPostalCode(String postalCode) {
        getData().put("bill_to_address_postal_code", postalCode);
    }

    public Map<String, String> asMap() {
        return getData();
    }
}
