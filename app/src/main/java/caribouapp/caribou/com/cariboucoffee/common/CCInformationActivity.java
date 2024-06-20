package caribouapp.caribou.com.cariboucoffee.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;

import java.util.Arrays;
import java.util.Objects;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.cybersource.TokenResponse;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityAddPaymentBinding;
import caribouapp.caribou.com.cariboucoffee.util.StringUtils;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;
import icepick.Icepick;
import icepick.State;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

/**
 * Created by jmsmuy on 11/28/17.
 */

public class CCInformationActivity extends BaseActivity<ActivityAddPaymentBinding> implements CCInformationContract.View {

    private static final String EXTRA_ADD_PAYMENT_RESULT = "add_payment_result";
    private static final String EXTRA_OPTIONAL_REPLACE_CARD_ON_FILE = "optional_replace_card_on_file";
    private static final String EXTRA_ADD_PAYMENT_ORIGIN = "add_payment_origin";
    private static final int MY_SCAN_REQUEST_CODE = 1;
    private static final String TAG = CCInformationActivity.class.getSimpleName();

    @State
    CCInformationModel mModel;
    private CCInformationContract.Presenter mPresenter;
    private AppScreen mAppScreen;
    private Observable.OnPropertyChangedCallback mOnModelPropertyChangedCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (propertyId == BR.state) {
                getBinding().contentIncluded.spnState.setSelection(mModel.getState() == null ? 0 : mModel.getState().ordinal() + 1);
            }
        }
    };

    /**
     * Creates intent with required extra value
     *
     * @param context
     * @return
     */
    public static Intent createIntent(Context context,
                                      AddPaymentOrigin addPaymentOrigin, boolean optionalReplace,
                                      String token, boolean isCheckoutAddFunds) {
        Intent intent = new Intent(context, CCInformationActivity.class);
        intent.putExtra(EXTRA_ADD_PAYMENT_ORIGIN, addPaymentOrigin);
        intent.putExtra(EXTRA_OPTIONAL_REPLACE_CARD_ON_FILE, optionalReplace);
        intent.putExtra(AppConstants.EXTRA_CYBERSOURCE_TOKEN, token);
        intent.putExtra(AppConstants.EXTRA_ADD_FUNDS_FROM_CHECKOUT, isCheckoutAddFunds);
        return intent;
    }

    /**
     * Returns the cybersource token from the intent
     *
     * @param intent
     * @return
     */
    public static TokenResponse getTokenFromIntent(Intent intent) {
        return intent != null
                && intent.getExtras() != null
                ? (TokenResponse) intent.getExtras().getSerializable(AppConstants.EXTRA_CYBERSOURCE_TOKEN) : null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_payment;
    }


    /**
     * Returns the token if it exists
     *
     * @return
     */
    public String getTokenFromIntent() {
        return getIntent().getStringExtra(AppConstants.EXTRA_CYBERSOURCE_TOKEN);
    }

    /**
     * Returns which was the origin of this activity
     *
     * @return
     */
    public AddPaymentOrigin getOrigin() {
        return (AddPaymentOrigin) getIntent().getSerializableExtra(EXTRA_ADD_PAYMENT_ORIGIN);
    }

    @Override
    public boolean hasOptionalReplaceCard() {
        return getIntent().getBooleanExtra(EXTRA_OPTIONAL_REPLACE_CARD_ON_FILE, false);
    }

    private boolean isCheckoutAddFunds() {
        return getIntent().getBooleanExtra(AppConstants.EXTRA_ADD_FUNDS_FROM_CHECKOUT, false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Icepick.restoreInstanceState(this, savedInstanceState);
        SourceApplication.get(this).getComponent().inject(this);
        CCInformationPresenter presenter = new CCInformationPresenter(this);
        SourceApplication.get(this).getComponent().inject(presenter);
        mPresenter = presenter;

        if (mModel == null) {
            mModel = new CCInformationModel();
        }
        mPresenter.setModel(mModel);
        mPresenter.updateLocalBillingData();
        switch (getOrigin()) {
            case EDIT_CARD:
                mPresenter.setUpForEditing();
                getBinding().tbTitle.setText(R.string.edit_card);
                mAppScreen = AppScreen.EDIT_CARD;
                getBinding().contentIncluded.btnAddFunds.setText(R.string.save_card);
                break;
            case AUTORELOAD:
                getBinding().tbTitle.setText(R.string.auto_reload_settings);
                getBinding().contentIncluded.btnAddFunds.setText(R.string.apply_auto_reload);
                break;
            case ADD_CARD:
                getBinding().tbTitle.setText(R.string.new_card);
                getBinding().contentIncluded.btnAddFunds.setText(R.string.add_new_card);
                break;
            case ADD_FUNDS:
                getBinding().tbTitle.setText(
                        isCheckoutAddFunds() ? R.string.finish_and_pay : R.string.add_payment);
                getBinding().contentIncluded.btnAddFunds.setText(
                        isCheckoutAddFunds() ? R.string.finish_and_pay : R.string.add_funds);
                mPresenter.fillModel();
                mAppScreen = isCheckoutAddFunds() ? AppScreen.ADD_PAYMENT_OA : AppScreen.ADD_PAYMENT;
                break;
        }
        getBinding().tbTitle.setContentDescription(getString(R.string.heading_cd, getBinding().tbTitle.getText()));

        getBinding().contentIncluded.setModel(mModel);

        // Sets up toolbar
        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // We fill the states spinner
        initStatesSpinner();

        getBinding().contentIncluded.btnAddFunds.setOnClickListener(view -> {
            UIUtil.hideKeyboard(this);
            mModel.setState((StateEnum) getBinding().contentIncluded.spnState.getSelectedItem());
            mPresenter.addOrUpdateCard();
        });

        getBinding().contentIncluded.etZipCode.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO && !mModel.isReplaceCardEnabled()) {
                getBinding().contentIncluded.btnAddFunds.performClick();
            }
            return false;
        });

        getBinding().contentIncluded.etCity.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                getBinding().contentIncluded.spnState.performClick();
                return true;
            }
            return false;
        });

        mPresenter.setToken(getTokenFromIntent());
        getBinding().contentIncluded.btnAddCardFromCamera.setOnClickListener((v) -> {
                    Intent scanIntent = new Intent(this, CardIOActivity.class);
                    scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
                    scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true);
                    scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, true);
                    scanIntent.putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false);
                    scanIntent.putExtra(CardIOActivity.EXTRA_GUIDE_COLOR, getResources().getColor(R.color.primaryColor));
                    startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_SCAN_REQUEST_CODE) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                Objects.requireNonNull(getBinding().contentIncluded).etCcNumber.setText(scanResult.cardNumber);
                if (scanResult.isExpiryValid()) {
                    getBinding().contentIncluded.etExpirationMonth.setText(String.valueOf(scanResult.expiryMonth));
                    getBinding().contentIncluded.etExpirationYear.setText(String.valueOf(scanResult.expiryYear));
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (isLoading()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected AppScreen getScreenName() {
        return mAppScreen;
    }

    private void initStatesSpinner() {
        getBinding().contentIncluded.spnState
                .setAdapter(new StateEnumSpinnerAdapter<>(this, getString(R.string.select_a_state), StateEnum.values()));
        if (mModel.getState() != null) {
            //Plus one for the header of the spinner
            getBinding().contentIncluded.spnState.setSelection(Arrays.asList(StateEnum.values()).indexOf(mModel.getState()) + 1);
        }
        mModel.addOnPropertyChangedCallback(mOnModelPropertyChangedCallback);
        getBinding().contentIncluded.spnState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    getBinding().contentIncluded.etZipCode.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // NO-OP
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mModel.removeOnPropertyChangedCallback(mOnModelPropertyChangedCallback);
        mPresenter.detachView();
    }

    @Override
    public void finishAndSendResult(CCInformationModel model, TokenResponse tokenResponse, boolean cardOnFile, boolean newCard) {
        Intent intent = new Intent();
        model.setCcNumber(StringUtils.maskCC(model.getCcNumber()));
        intent.putExtra(EXTRA_ADD_PAYMENT_RESULT, model);
        intent.putExtra(AppConstants.EXTRA_CYBERSOURCE_TOKEN, tokenResponse);
        setResult(cardOnFile ? AppConstants.RESULT_CODE_ADD_FUNDS_FROM_FILE : AppConstants.RESULT_CODE_ONE_TIME_ADD_FUNDS, intent);
        finish();
    }

    ////////////// ERRORS /////////////

    @Override
    public boolean cardTypeUnknownErrorEnabled(boolean enabled) {
        if (enabled) {
            getBinding().contentIncluded.tilCcNumber.setError(getString(R.string.cc_unknown_card_type_error));
            cvvUnknownErrorEnabled(false); // We shouldn't show cvv errors while we still have CC number errors
        } else {
            getBinding().contentIncluded.tilCcNumber.setErrorEnabled(false);
        }
        return enabled;
    }

    @Override
    public boolean cvvUnknownErrorEnabled(boolean enabled) {
        if (enabled) {
            getBinding().contentIncluded.tilCvv.setError(getString(R.string.cvv_not_recognized));
        } else {
            getBinding().contentIncluded.tilCvv.setErrorEnabled(false);
        }
        return enabled;
    }

    @Override
    public boolean cardNumberErrorEnabled(boolean enabled) {
        if (enabled) {
            getBinding().contentIncluded.tilCcNumber.setError(getString(R.string.cc_number_error));
        } else {
            getBinding().contentIncluded.tilCcNumber.setErrorEnabled(false);
        }
        return enabled;
    }

    @Override
    public boolean validCardNumberErrorEnabled(boolean enabled) {
        if (enabled) {
            getBinding().contentIncluded.tilCcNumber.setError(getString(R.string.cc_number_error_special_char));
        } else {
            getBinding().contentIncluded.tilCcNumber.setErrorEnabled(false);
        }
        return enabled;
    }

    @Override
    public boolean firstNameErrorEnabled(boolean enabled) {
        if (enabled) {
            getBinding().contentIncluded.tilFirstName.setError(getString(R.string.first_name_error));
        } else {
            getBinding().contentIncluded.tilFirstName.setErrorEnabled(false);
        }
        return enabled;
    }

    @Override
    public boolean lastNameErrorEnabled(boolean enabled) {
        if (enabled) {
            getBinding().contentIncluded.tilLastName.setError(getString(R.string.last_name_error));
        } else {
            getBinding().contentIncluded.tilLastName.setErrorEnabled(false);
        }
        return enabled;
    }

    @Override
    public boolean expirationMonthErrorEnabled(boolean enabled) {
        if (enabled) {
            getBinding().contentIncluded.tilExpirationMonth.setError(getString(R.string.expiring_month_error));
        } else {
            getBinding().contentIncluded.tilExpirationMonth.setErrorEnabled(false);
        }
        return enabled;
    }

    @Override
    public boolean expirationYearErrorEnabled(boolean enabled) {
        if (enabled) {
            getBinding().contentIncluded.tilExpirationYear.setError(getString(R.string.expiring_year_error));
        } else {
            getBinding().contentIncluded.tilExpirationYear.setErrorEnabled(false);
        }
        return enabled;
    }

    @Override
    public boolean cvvErrorEnabled(boolean enabled) {
        if (enabled) {
            getBinding().contentIncluded.tilCvv.setError(getString(R.string.cvv_error));
        } else {
            getBinding().contentIncluded.tilCvv.setErrorEnabled(false);
        }
        return enabled;
    }

    @Override
    public boolean validCvvErrorEnabled(boolean enabled) {
        if (enabled) {
            getBinding().contentIncluded.tilCvv.setError(getString(R.string.cvv_error_special_char));
        } else {
            getBinding().contentIncluded.tilCvv.setErrorEnabled(false);
        }
        return enabled;
    }

    @Override
    public boolean billingAddressErrorEnabled(boolean enabled) {
        if (enabled) {
            getBinding().contentIncluded.tilBillingAddress1.setError(getString(R.string.billing_address_error));
        } else {
            getBinding().contentIncluded.tilBillingAddress1.setErrorEnabled(false);
        }
        return enabled;
    }

    @Override
    public boolean zipErrorEnabled(boolean enabled) {
        if (enabled) {
            getBinding().contentIncluded.tilZipCode.setError(getString(R.string.zip_error));
        } else {
            getBinding().contentIncluded.tilZipCode.setErrorEnabled(false);
        }
        return enabled;
    }

    @Override
    public boolean cityErrorEnabled(boolean enabled) {
        if (enabled) {
            getBinding().contentIncluded.tilCity.setError(getString(R.string.city_error));
        } else {
            getBinding().contentIncluded.tilCity.setErrorEnabled(false);
        }
        return enabled;
    }

    @Override
    public boolean stateErrorEnabled(boolean enabled) {
        getBinding().contentIncluded.tvSelectStateError.setVisibility(enabled ? View.VISIBLE : View.GONE);
        return enabled;
    }

    public enum AddPaymentOrigin {
        EDIT_CARD, AUTORELOAD, ADD_FUNDS, ADD_CARD
    }

    ///////////////////////////////////

}
