package caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter;

import android.text.TextUtils;

import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.model.PersonalInformationModel;

public class WelcomePersonalInformationConfigurator implements PersonalInformationConfigurator {

    private static final int EMPTY_DATE_YEAR = 1900;

    private final String mSubmitTitle;
    private boolean mShouldShowBirthday;
    private boolean mShouldShowZipCode;
    private boolean mShouldShowEmail;
    private boolean mShouldShowPhoneNumber;
    private boolean mShouldShowFirstName;
    private boolean mShouldShowLastName;

    public WelcomePersonalInformationConfigurator(String submitTitle) {
        mSubmitTitle = submitTitle;
    }

    @Override
    public String getSubmitTitle() {
        return mSubmitTitle;
    }

    @Override
    public boolean shouldShowPassword() {
        return false;
    }

    @Override
    public boolean shouldShowBirthday() {
        return mShouldShowBirthday;
    }

    @Override
    public boolean shouldShowZipCode() {
        return mShouldShowZipCode;
    }

    @Override
    public boolean shouldShowEmail() {
        return mShouldShowEmail;
    }

    @Override
    public boolean shouldShowPhoneNumber() {
        return mShouldShowPhoneNumber;
    }

    @Override
    public boolean shouldShowFirstName() {
        return mShouldShowFirstName;
    }

    @Override
    public boolean shouldShowLastName() {
        return mShouldShowLastName;
    }

    @Override
    public boolean shouldShowMidSectionText() {
        return false;
    }

    @Override
    public boolean someFieldShouldBeShown() {
        return mShouldShowBirthday || mShouldShowZipCode || mShouldShowEmail
                || mShouldShowPhoneNumber || mShouldShowFirstName || mShouldShowLastName;
    }

    @Override
    public void onPersonalInformationModelAvailable(PersonalInformationModel model) {
        mShouldShowBirthday = model.getBirthday() == null || model.getBirthday().year().get() == EMPTY_DATE_YEAR;
        mShouldShowZipCode = TextUtils.isEmpty(model.getZipCode()) || TextUtils.isEmpty(model.getCity()) || model.getState() == null;
        mShouldShowEmail = TextUtils.isEmpty(model.getEmail());
        mShouldShowPhoneNumber = TextUtils.isEmpty(model.getPhoneNumber());

        boolean nameMissing = TextUtils.isEmpty(model.getFirstName()) || TextUtils.isEmpty(model.getLastName());
        mShouldShowFirstName = nameMissing;
        mShouldShowLastName = nameMissing;
    }
}
