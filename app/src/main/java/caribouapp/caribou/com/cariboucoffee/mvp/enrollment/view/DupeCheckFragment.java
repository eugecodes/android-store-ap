package caribouapp.caribou.com.cariboucoffee.mvp.enrollment.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.databinding.FragmentDupeCheckInfoBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.BaseFragment;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SignInActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.dashboard.MainActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.DupeCheckContract;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.model.DupeCheckModel;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.presenter.DupeCheckPresenter;
import icepick.Icepick;
import icepick.State;

@FragmentWithArgs
public class DupeCheckFragment extends BaseFragment<FragmentDupeCheckInfoBinding> implements DupeCheckContract.View {

    public interface DupeCheckListener {
        void dupeCheckPassed(boolean enrolledViaGoogle, String email, String telephone, boolean userHasBirthday);
    }

    @State
    DupeCheckModel mModel;

    @Arg
    boolean mEnrolledViaGoogle;

    @Arg
    String mGoogleEmail;

    private DupeCheckPresenter mPresenter;
    private DupeCheckListener mListener;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dupe_check_info;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        FragmentArgs.inject(this); // read @Arg fields

        if (mModel == null) {
            mModel = new DupeCheckModel();
            mModel.setEnrolledViaGoogle(mEnrolledViaGoogle);
            mModel.setEmail(mGoogleEmail);
        }

        if (getActivity() instanceof DupeCheckListener) {
            mListener = (DupeCheckListener) getActivity();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getBinding().setModel(mModel);

        DupeCheckPresenter dupeCheckPresenter = new DupeCheckPresenter(this, mModel);
        SourceApplication.get(getContext()).getComponent().inject(dupeCheckPresenter);
        mPresenter = dupeCheckPresenter;


        initPhoneNumberField();

        getBinding().btnNext.setOnClickListener(v -> mPresenter.dupeCheck());
    }

    private void initPhoneNumberField() {
        getBinding().etPhoneNumber.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                mPresenter.dupeCheck();
            }
            return false;
        });
    }

    @Override
    public void showWarning(String message) {
        super.showWarning(message);
        mPresenter.logError(message);
    }

    @Override
    public boolean emailErrorEnabled(boolean value) {
        if (value) {
            String errorMessage = getString(R.string.email_not_valid);
            mPresenter.logError(errorMessage);
            getBinding().tilEmail.setError(errorMessage);
        } else {
            getBinding().tilEmail.setErrorEnabled(false);
        }
        return value;
    }

    @Override
    public boolean telephoneErrorEnabled(boolean value) {
        if (value) {
            String errorMessage = getString(R.string.phone_not_valid);
            mPresenter.logError(errorMessage);
            getBinding().tilPhoneNumber.setError(errorMessage);
        } else {
            getBinding().tilPhoneNumber.setErrorEnabled(false);
        }
        return value;
    }

    @Override
    public void showEmailAlreadyRegistered(@StringRes int messageStringId, boolean enrolledViaGoogle) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.oops)
                .setMessage(getString(messageStringId))
                .setPositiveButton(R.string.sign_up_with_different_email, null)
                .setNeutralButton(R.string.sign_up_log_in_with_my_account, (dialog, which) -> goToSignIn())
                .setNegativeButton(R.string.sign_up_go_back_to_homescreen, (dialog, which) -> goToDashboard())
                .show();

        if (enrolledViaGoogle) {
            alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
        }
    }

    private void goToSignIn() {
        getActivity().finish();
        startActivity(SignInActivity.createIntent(getContext()));
    }

    private void goToDashboard() {
        Intent intent = MainActivity.createIntent(requireContext(), null);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(AppConstants.EXTRA_IS_FROM_SIGN_IN, true);
        startActivity(intent);
    }

    @Override
    public void goToPersonalInformation(boolean enrolledViaGoogle, String email, String telephone, boolean userHasBirthday) {
        if (mListener != null) {
            mListener.dupeCheckPassed(enrolledViaGoogle, email, telephone, userHasBirthday);
        }
    }

    @Override
    public void showPhoneAlreadyRegistered(int messageStringId, String maskedEmail) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.sorry)
                .setMessage(getString(messageStringId, maskedEmail))
                .setPositiveButton(R.string.sign_up_with_different_phone, null)
                .setNeutralButton(R.string.sign_up_log_in_with_my_account, (dialog, which) -> goToSignIn())
                .setNegativeButton(R.string.sign_up_go_back_to_homescreen, (dialog, which) -> requireActivity().finish())
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    public boolean onBackPressed() {
        if (mModel.isPreEnrolledValidationPassed()) {
            mModel.setPreEnrolledValidationPassed(false);
            return true;
        }
        return false;
    }
}
