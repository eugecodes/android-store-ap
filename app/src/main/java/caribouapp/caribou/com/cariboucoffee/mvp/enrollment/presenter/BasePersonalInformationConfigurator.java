package caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter;

import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.model.PersonalInformationModel;

public class BasePersonalInformationConfigurator implements PersonalInformationConfigurator {

    @Override
    public String getSubmitTitle() {
        return null;
    }

    @Override
    public boolean shouldShowPassword() {
        return true;
    }

    @Override
    public boolean shouldShowBirthday() {
        return true;
    }

    @Override
    public boolean shouldShowZipCode() {
        return true;
    }

    @Override
    public boolean shouldShowEmail() {
        return true;
    }

    @Override
    public boolean shouldShowPhoneNumber() {
        return true;
    }

    @Override
    public boolean shouldShowFirstName() {
        return true;
    }

    @Override
    public boolean shouldShowLastName() {
        return true;
    }

    @Override
    public boolean shouldShowMidSectionText() {
        return true;
    }

    @Override
    public boolean someFieldShouldBeShown() {
        return true;
    }

    @Override
    public void onPersonalInformationModelAvailable(PersonalInformationModel model) {
        // NO-OP
    }
}
