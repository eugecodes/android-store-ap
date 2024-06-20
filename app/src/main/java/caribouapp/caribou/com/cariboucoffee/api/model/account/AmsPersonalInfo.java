package caribouapp.caribou.com.cariboucoffee.api.model.account;

import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalDate;

import java.io.Serializable;

import caribouapp.caribou.com.cariboucoffee.common.StateEnum;

/**
 * Created by jmsmuy on 11/2/17.
 */

public class AmsPersonalInfo implements Serializable {

    @SerializedName("firstName")
    private String mFirstName;

    @SerializedName("lastName")
    private String mLastName;

    @SerializedName("gender")
    private String mGender = "PreferNotToAnswer";

    @SerializedName("dateOfBirth")
    private LocalDate mDateOfBirth;

    @SerializedName("emailAddress")
    private String mEmailAddress;

    @SerializedName("memberPhone")
    private String mMemberPhone;

    @SerializedName("address")
    private AmsAddress mAddress;

    public AmsPersonalInfo(String firstName, String lastName, StateEnum state, String city,
                           String zipCode, String telephone, String email, LocalDate birthday) {
        mFirstName = firstName;
        mLastName = lastName;
        mEmailAddress = email;
        mMemberPhone = telephone;
        mDateOfBirth = birthday;
        mAddress = new AmsAddress(null, null, city, state, zipCode, null);
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String gender) {
        mGender = gender;
    }

    public LocalDate getDateOfBirth() {
        return mDateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        mDateOfBirth = dateOfBirth;
    }

    public String getEmailAddress() {
        return mEmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        mEmailAddress = emailAddress;
    }

    public String getMemberPhone() {
        return mMemberPhone;
    }

    public void setMemberPhone(String memberPhone) {
        mMemberPhone = memberPhone;
    }

    public AmsAddress getAddress() {
        return mAddress;
    }

    public void setAddress(AmsAddress address) {
        mAddress = address;
    }

}
