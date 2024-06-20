package caribouapp.caribou.com.cariboucoffee.mvp.account.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.common.MaskEmailTransformationMethod;
import caribouapp.caribou.com.cariboucoffee.databinding.FragmentProfileBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.BaseFragment;
import caribouapp.caribou.com.cariboucoffee.mvp.account.AccountContract;
import caribouapp.caribou.com.cariboucoffee.mvp.account.presenter.ProfilePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.model.PersonalInformationModel;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.view.PersonalInformationActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.view.SetPasswordActivity;
import icepick.State;

/**
 * Created by jmsmuy on 11/21/17.
 */
@FragmentWithArgs
public class ProfileFragment extends BaseFragment<FragmentProfileBinding> implements AccountContract.ProfileView {

    @State
    PersonalInformationModel mProfileModel;

    private ProfilePresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_profile;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (mProfileModel == null) {
            mProfileModel = new PersonalInformationModel();
        }
        getBinding().tvEmail.setTransformationMethod(new MaskEmailTransformationMethod());
        getBinding().tvEditProfile.setOnClickListener(v -> goEditProfile());
        getBinding().tvChangePassword.setOnClickListener(v -> goChangePassword());
        getBinding().tvDeleteAccount.setOnClickListener(v -> mPresenter.showMyAccountDeleteDialog(getActivity()));
        ProfilePresenter profilePresenter = new ProfilePresenter(this, mProfileModel);
        SourceApplication.get(getContext()).getComponent().inject(profilePresenter);
        mPresenter = profilePresenter;
        getBinding().setModel(mProfileModel);
        mPresenter.enabledDeleteAccountButton();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.REQUEST_CODE_EDIT_PROFILE && resultCode == Activity.RESULT_OK) {
            getBinding().setModel((PersonalInformationModel) data.getSerializableExtra(AppConstants.EXTRA_PERSONAL_INFORMATION_MODEL));
            showMessage(R.string.account_data_updated);
        } else if (requestCode == AppConstants.REQUEST_CODE_CHANGE_PASSWORD && resultCode == Activity.RESULT_OK) {
            showMessage(R.string.account_password_updated);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void goEditProfile() {
        startActivityForResult(
                PersonalInformationActivity.createEditPersonalInformationIntent(getContext(), true),
                AppConstants.REQUEST_CODE_EDIT_PROFILE);
    }

    private void goChangePassword() {
        startActivityForResult(
                SetPasswordActivity.createChangePasswordIntent(getContext(), true),
                AppConstants.REQUEST_CODE_CHANGE_PASSWORD);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public void showDeleteAccountFunctionality() {
        getBinding().tvDeleteAccount.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideDeleteAccountFunctionality() {
        getBinding().tvDeleteAccount.setVisibility(View.GONE);
    }
}
