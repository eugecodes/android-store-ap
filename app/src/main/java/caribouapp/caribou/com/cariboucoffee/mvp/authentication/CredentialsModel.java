package caribouapp.caribou.com.cariboucoffee.mvp.authentication;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;

/**
 * Created by jmsmuy on 10/18/17.
 */

public class CredentialsModel extends BaseObservable implements Serializable {

    private String mEmail;
    private String mPassword;
    private String mPasswordConfirm;
    private String mCurrentPassword;
    private boolean mChangePassword;
    private boolean mNewPassword;

    @Bindable
    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
        notifyPropertyChanged(BR.password);
    }

    @Bindable
    public String getPasswordConfirm() {
        return mPasswordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        mPasswordConfirm = passwordConfirm;
        notifyPropertyChanged(BR.passwordConfirm);
    }

    public boolean isChangePassword() {
        return mChangePassword;
    }

    public void setChangePassword(boolean changePassword) {
        mChangePassword = changePassword;
    }

    @Bindable
    public String getCurrentPassword() {
        return mCurrentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        mCurrentPassword = currentPassword;
        notifyPropertyChanged(BR.currentPassword);
    }

    @Bindable
    public boolean isNewPassword() {
        return mNewPassword;
    }

    public void setNewPassword(boolean newPassword) {
        mNewPassword = newPassword;
        notifyPropertyChanged(BR.newPassword);
    }
}
