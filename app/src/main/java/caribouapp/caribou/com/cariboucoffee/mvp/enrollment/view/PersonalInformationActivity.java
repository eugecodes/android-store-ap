package caribouapp.caribou.com.cariboucoffee.mvp.enrollment.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityPersonalInformationBinding;
import icepick.Icepick;

/**
 * Created by jmsmuy on 1/8/18.
 */

public class PersonalInformationActivity extends BaseActivity<ActivityPersonalInformationBinding>
        implements PersonalInfoFragment.PersonalInfoListener {


    private PersonalInfoFragment mPersonalInfoFragment;

    public static Intent createEditPersonalInformationIntent(Context context, boolean isEdit) {
        Intent intent = new Intent(context, PersonalInformationActivity.class);
        intent.putExtra(AppConstants.EXTRA_IS_EDIT_PERSONAL_INFORMATION, isEdit);
        return intent;
    }

    public static Intent createSignUpRewardsProgram(Context context, boolean isSignUpInRewardProgram) {
        Intent intent = new Intent(context, PersonalInformationActivity.class);
        intent.putExtra(AppConstants.EXTRA_SIGN_UP_REWARDS_PROGRAM, isSignUpInRewardProgram);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal_information;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mPersonalInfoFragment = PersonalInfoFragmentBuilder.newPersonalInfoFragment(PersonalInfoFragment.FormType.EDIT);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.personal_info_fragment_container, mPersonalInfoFragment
                )
                .commit();


    }

    @Override
    protected AppScreen getScreenName() {
        return isEditPersonalInformation() ? AppScreen.EDIT_PROFILE : null;
    }

    private boolean isEditPersonalInformation() {
        return getIntent().getBooleanExtra(AppConstants.EXTRA_IS_EDIT_PERSONAL_INFORMATION, false);
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
