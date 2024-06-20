package caribouapp.caribou.com.cariboucoffee.common;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jmsmuy on 6/18/18.
 */

public enum EnumPreEnrollmentResponse {

    @SerializedName("EnrolledInDifferentProgram")
    ENROLLED_IN_DIFFERENT_PROGRAM,

    @SerializedName("EnrolledInThisProgram")
    ENROLLED_IN_THIS_PROGRAM,

    @SerializedName("PhoneNumberRegisteredToDifferentEmail")
    PHONE_REGISTERED_DIFFERENT_EMAIL,

    @SerializedName("AutomaticPasswordReset")
    AUTOMATIC_PASSWORD_RESET,

    @SerializedName("NotEnrolled")
    NOT_ENROLLED
}
