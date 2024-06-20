package caribouapp.caribou.com.cariboucoffee.mvp.enrollment;

import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.model.PersonalInformationModel;

public interface PersonalInformationContract {
    interface View extends SaveSignInDataContract.View {

        boolean firstNameErrorEnabled(boolean enabled);

        boolean lastNameErrorEnabled(boolean enabled);

        boolean zipErrorEnabled(boolean enabled);

        boolean telephoneErrorEnabled(boolean value);

        boolean emailErrorEnabled(boolean value);

        boolean tNCErrorEnabled(boolean enabled);

        boolean confirmPasswordErrorEnabled(boolean enabled);

        boolean ageRequirementErrorEnable(boolean enable);

        boolean birthdayErrorEnabled(boolean enabled);

        void setUpdateUserInformationResult(PersonalInformationModel model);

        String getPassword();

        String getPasswordConfirm();
    }

    interface Presenter extends SaveSignInDataContract.Presenter {

        void loadExistingData();

        void createNewUser();

        void updateUser();

        void logEnrollError(String errorMessage);
    }
}
