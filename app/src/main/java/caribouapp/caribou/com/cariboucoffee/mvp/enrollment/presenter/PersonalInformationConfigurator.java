package caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter;

import java.io.Serializable;

import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.model.PersonalInformationModel;

public interface PersonalInformationConfigurator extends Serializable {

    String getSubmitTitle();

    boolean shouldShowPassword();

    boolean shouldShowBirthday();

    boolean shouldShowZipCode();

    boolean shouldShowEmail();

    boolean shouldShowPhoneNumber();

    boolean shouldShowFirstName();

    boolean shouldShowLastName();

    boolean shouldShowMidSectionText();

    boolean someFieldShouldBeShown();

    void onPersonalInformationModelAvailable(PersonalInformationModel model);
}
