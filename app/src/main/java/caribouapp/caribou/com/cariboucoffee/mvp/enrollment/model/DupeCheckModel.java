package caribouapp.caribou.com.cariboucoffee.mvp.enrollment.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;

/**
 * Created by jmsmuy on 1/4/18.
 */

public class DupeCheckModel extends BaseObservable implements Serializable {

    private String mEmail;
    private String mTelephone;
    private Boolean mFirstSignInAttempt = true;
    private boolean mEnrolledViaGoogle = false;
    private boolean mPreEnrolledValidationPassed = false;

    @Bindable
    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getTelephone() {
        return mTelephone;
    }

    public void setTelephone(String telephone) {
        mTelephone = telephone;
        notifyPropertyChanged(BR.telephone);
    }

    public Boolean isFirstSignInAttempt() {
        return mFirstSignInAttempt;
    }

    public void setFirstSignInAttempt(Boolean firstSignInAttempt) {
        mFirstSignInAttempt = firstSignInAttempt;
    }

    @Bindable
    public boolean isEnrolledViaGoogle() {
        return mEnrolledViaGoogle;
    }

    public void setEnrolledViaGoogle(boolean enrolledViaGoogle) {
        mEnrolledViaGoogle = enrolledViaGoogle;
        notifyPropertyChanged(BR.enrolledViaGoogle);
    }

    @Bindable
    public boolean isPreEnrolledValidationPassed() {
        return mPreEnrolledValidationPassed;
    }

    public void setPreEnrolledValidationPassed(boolean preEnrolled) {
        mPreEnrolledValidationPassed = preEnrolled;
        notifyPropertyChanged(BR.preEnrolledValidationPassed);
    }
}
