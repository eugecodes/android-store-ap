package caribouapp.caribou.com.cariboucoffee.mvp.enrollment.view;

import android.os.Bundle;

import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityWelcomeBackBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter.PersonalInformationConfigurator;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter.WelcomePersonalInformationConfigurator;
import icepick.Icepick;

public class WelcomeBackActivity extends BaseActivity<ActivityWelcomeBackBinding> implements PersonalInfoFragment.PersonalInfoListener {

    private PersonalInfoFragment mPersonalInfoFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome_back;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        PersonalInformationConfigurator configurator = new WelcomePersonalInformationConfigurator(getString(R.string.start_earning_rewards));

        getIntent().putExtra(AppConstants.EXTRA_SIGN_UP_REWARDS_PROGRAM, true);
        getIntent().putExtra(AppConstants.PERSONAL_INFORMATION_CONFIGURATOR, configurator);

        super.onCreate(savedInstanceState);

        Icepick.restoreInstanceState(this, savedInstanceState);

        getBinding().contentIncluded.setConfigurator(configurator);

        mPersonalInfoFragment = PersonalInfoFragmentBuilder.newPersonalInfoFragment(PersonalInfoFragment.FormType.WELCOME_BACK);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.form_container, mPersonalInfoFragment
                )
                .commit();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onPersonalInfoSubmit() {
        mPersonalInfoFragment.getPersonalInfoPresenter().updateUser();
    }
}
