package caribouapp.caribou.com.cariboucoffee.mvp.account.presenter;

import static caribouapp.caribou.com.cariboucoffee.api.model.ResponseHeader.DELETE_ALREADY_EXISTS;
import static caribouapp.caribou.com.cariboucoffee.api.model.ResponseHeader.DELETE_NON_ZERO_BALANCE;
import static caribouapp.caribou.com.cariboucoffee.api.model.ResponseHeader.SUCCESS_DELETE_ACCOUNT;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.api.AmsApi;
import caribouapp.caribou.com.cariboucoffee.api.model.ResponseHeader;
import caribouapp.caribou.com.cariboucoffee.api.model.ResponseWithHeader;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsRequestDeleteAccount;
import caribouapp.caribou.com.cariboucoffee.api.model.account.AmsResponse;
import caribouapp.caribou.com.cariboucoffee.common.BrandEnum;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.BaseViewRetrofitErrorMapperCallback;
import caribouapp.caribou.com.cariboucoffee.common.callbacks.ResultCallbackErrorMapper;
import caribouapp.caribou.com.cariboucoffee.messages.ErrorMessageMapper;
import caribouapp.caribou.com.cariboucoffee.mvp.BasePresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.account.AccountContract;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserAccountService;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.enrollment.model.PersonalInformationModel;
import caribouapp.caribou.com.cariboucoffee.util.AppUtils;
import caribouapp.caribou.com.cariboucoffee.util.Log;
import retrofit2.Response;

/**
 * Created by jmsmuy on 11/21/17.
 */

public class ProfilePresenter extends BasePresenter<AccountContract.ProfileView> implements AccountContract.ProfilePresenter {


    private final PersonalInformationModel mModel;
    @Inject
    UserAccountService mUserAccountService;
    @Inject
    UserServices mUserServices;
    @Inject
    ErrorMessageMapper mErrorMessageMapper;
    @Inject
    SettingsServices mSettingsServices;
    @Inject
    AmsApi mAmsApi;
    private Activity activity;

    public ProfilePresenter(AccountContract.ProfileView view, PersonalInformationModel profileModel) {
        super(view);
        mModel = profileModel;
    }


    @Override
    public void loadData() {
        mUserAccountService.getProfileData(new ResultCallbackErrorMapper<AmsResponse>(getView()) {

            @Override
            protected void onSuccessViewUpdates(AmsResponse data) {
                mModel.setCity(mUserServices.getCity());
                mModel.setEmail(mUserServices.getEmail());
                mModel.setFirstName(mUserServices.getFirstName());
                mModel.setLastName(mUserServices.getLastName());
                mModel.setState(mUserServices.getState());
                mModel.setZipCode(mUserServices.getZip());
                mModel.setPhoneNumber(mUserServices.getPhoneNumber());
            }
        });
    }

    @Override
    public void showMyAccountDeleteDialog(Activity activityContext) {
        this.activity = activityContext;
        new AlertDialog.Builder(activity)
                .setTitle(mSettingsServices.getDeleteAccountTitle())
                .setCancelable(false)
                .setMessage(mSettingsServices.getDeleteAccountMessage())
                .setPositiveButton(R.string.yes, (dialog, which) -> callDeleteMyAccountAPI())
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void enabledDeleteAccountButton() {
        if (mSettingsServices.isDeleteAccountFeatureEnabled()) {
            getView().showDeleteAccountFunctionality();
        } else {
            getView().hideDeleteAccountFunctionality();
        }
    }

    private void callDeleteMyAccountAPI() {
        mAmsApi.requestDeletion(new AmsRequestDeleteAccount(mUserServices.getUid()))
                .enqueue(new BaseViewRetrofitErrorMapperCallback<ResponseWithHeader>(getView()) {
                    @Override
                    protected void onSuccessViewUpdates(Response<ResponseWithHeader> response) {
                        ResponseWithHeader responseWithHeader = response.body();
                        if (responseWithHeader != null) {
                            if (responseWithHeader.getHeader().getmStatusCode() != null
                                    && responseWithHeader.getHeader().getmStatusCode().equals(DELETE_NON_ZERO_BALANCE)) {
                                showNonZeroAccountBalanceDialog();
                            } else if (responseWithHeader.getHeader().getmStatusCode() != null
                                    && responseWithHeader.getHeader().getmStatusCode().equals(DELETE_ALREADY_EXISTS)) {
                                showDeleteAccountAlreadyDialog(responseWithHeader.getHeader());
                            } else if (responseWithHeader.getHeader().getmStatusCode() != null
                                    && responseWithHeader.getHeader().getmStatusCode().equals(SUCCESS_DELETE_ACCOUNT)) {
                                showDeleteAccountSuccessDialog();
                            }
                        }
                    }
                });
    }

    private void showDeleteAccountSuccessDialog() {
        new AlertDialog.Builder(activity)
                .setTitle(mSettingsServices.getConfirmationTitle())
                .setCancelable(false)
                .setMessage(mSettingsServices.getConfirmationMessage())
                .setPositiveButton(R.string.dialog_close, (dialog, which) -> goToSignIn())
                .show();
    }

    private void showDeleteAccountAlreadyDialog(ResponseHeader header) {
        new AlertDialog.Builder(activity)
                .setTitle(mSettingsServices.getDeleteAccountTitle())
                .setCancelable(false)
                .setMessage(header.getmMessage())
                .setPositiveButton(R.string.dialog_close, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showNonZeroAccountBalanceDialog() {
        new AlertDialog.Builder(activity)
                .setTitle(mSettingsServices.getAccountBalanceTitle())
                .setCancelable(false)
                .setMessage(mSettingsServices.getAccountBalanceMessage())
                .setPositiveButton(R.string.dialog_close, (dialog, which) -> dialog.dismiss())
                .setNegativeButton(R.string.contact, (dialog, which) -> redirectToContactPage())
                .show();
    }

    private void goToSignIn() {
        Log.i("ProfilePresenter", "Forced logout");
        mUserServices.signOut();
        AppUtils.restartAppSignInActivity(activity);
    }

    private void redirectToContactPage() {
        Uri uri = Uri.parse(mSettingsServices.getEBBContactUsURL());
        if (AppUtils.getBrand() == BrandEnum.EBB_BRAND) {
            uri = Uri.parse(mSettingsServices.getEBBContactUsURL());
        } else if (AppUtils.getBrand() == BrandEnum.NNYB_BRAND) {
            uri = Uri.parse(mSettingsServices.getNoahContactUsURL());
        } else if (AppUtils.getBrand() == BrandEnum.BRU_BRAND) {
            uri = Uri.parse(mSettingsServices.getBRUEGGERSContactUsURL());
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }
}
