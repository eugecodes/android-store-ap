package caribouapp.caribou.com.cariboucoffee.mvp.enrollment.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivitySignUpPersonalInfoBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.CredentialsModel;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.model.PersonalInformationModel;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;

/**
 * Created by jmsmuy on 1/4/18.
 */

public class SignUpPersonalInfoActivity extends BaseActivity<ActivitySignUpPersonalInfoBinding>
        implements DupeCheckFragment.DupeCheckListener, PersonalInfoFragment.PersonalInfoListener {

    public static Intent createIntent(Context context, boolean enrolledViaGoogle, String googleEmail) {
        Intent intent = new Intent(context, SignUpPersonalInfoActivity.class);
        if (enrolledViaGoogle) {
            intent.putExtra(AppConstants.EXTRA_ENROLLED_VIA_GOOGLE, true);
            intent.putExtra(AppConstants.EXTRA_SET_EMAIL, googleEmail);
        }
        return intent;
    }

    private DupeCheckFragment mDupeCheckFragment;

    private PersonalInfoFragment mPersonalInfoFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign_up_personal_info;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getBinding().tb.getNavigationIcon()
                .setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.primaryMidDarkColor), PorterDuff.Mode.SRC_ATOP);

        mDupeCheckFragment = DupeCheckFragmentBuilder.newDupeCheckFragment(isEnrolledViaGoogle(), getGoogleEmail());

        mPersonalInfoFragment = PersonalInfoFragmentBuilder.newPersonalInfoFragment(PersonalInfoFragment.FormType.CREATE);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.dupe_check_fragment_container, mDupeCheckFragment)
                .replace(R.id.personal_info_fragment_container, mPersonalInfoFragment)
                .commit();
    }

    private String getGoogleEmail() {
        return getIntent().getStringExtra(AppConstants.EXTRA_SET_EMAIL);
    }

    private boolean isEnrolledViaGoogle() {
        return getIntent().getBooleanExtra(AppConstants.EXTRA_ENROLLED_VIA_GOOGLE, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBinding().setDupeCheckModel(mDupeCheckFragment.mModel);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDupeCheckFragment.onBackPressed()) {
                return true;
            }
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected AppScreen getScreenName() {
        return AppScreen.SIGN_UP_2;
    }

    @Override
    public void onBackPressed() {
        if (mDupeCheckFragment.onBackPressed()) {
            return;
        }
        finish();
    }

    @Override
    public void dupeCheckPassed(boolean enrolledViaGoogle, String email, String telephone, boolean userHasBirthday) {
        UIUtil.hideKeyboard(this);

        PersonalInformationModel personalInformationModel = mPersonalInfoFragment.mPersonalInformationModel;
        personalInformationModel.setEnrolledViaGoogle(enrolledViaGoogle);
        personalInformationModel.setEmail(email);
        personalInformationModel.setBirthdayAlreadyDefined(userHasBirthday);
        personalInformationModel.setPhoneNumber(telephone);

        CredentialsModel credentialsModel = mPersonalInfoFragment.mCredentialsModel;
        credentialsModel.setNewPassword(!enrolledViaGoogle);

    }

    @Override
    public void onPersonalInfoSubmit() {
        mPersonalInfoFragment.getPersonalInfoPresenter().createNewUser();
    }
}
