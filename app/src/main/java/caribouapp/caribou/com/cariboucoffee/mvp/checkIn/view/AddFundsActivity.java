package caribouapp.caribou.com.cariboucoffee.mvp.checkIn.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.util.List;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.common.CCInformationActivity;
import caribouapp.caribou.com.cariboucoffee.common.OptionChooserView;
import caribouapp.caribou.com.cariboucoffee.common.OptionFactory;
import caribouapp.caribou.com.cariboucoffee.common.OptionItem;
import caribouapp.caribou.com.cariboucoffee.cybersource.TokenResponse;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityAddFundsBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.AddFundsContract;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.AddFundsModel;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.presenter.AddFundsPresenter;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by jmsmuy on 11/24/17.
 */

public class AddFundsActivity extends BaseActivity<ActivityAddFundsBinding> implements AddFundsContract.View,
        CompoundButton.OnCheckedChangeListener, OptionChooserView.OptionChooserListener {

    private AddFundsContract.Presenter mPresenter;

    public static Intent createIntent(Context context) {
        return new Intent(context, AddFundsActivity.class);
    }

    public static Intent createIntentForCheckoutAddFunds(Context context, boolean isCheckoutAddFunds, BigDecimal fundsNeededToAdd) {
        Intent checkoutAddFund = new Intent(context, AddFundsActivity.class);
        checkoutAddFund.putExtra(AppConstants.EXTRA_ADD_FUNDS_FROM_CHECKOUT, isCheckoutAddFunds);
        checkoutAddFund.putExtra(AppConstants.EXTRA_FUNDS_NEEDED_TO_ADD, fundsNeededToAdd);
        return checkoutAddFund;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_funds;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AddFundsPresenter presenter = new AddFundsPresenter(this, isCheckoutAddFunds(), getFundsNeededToAdd());
        SourceApplication.get(this).getComponent().inject(presenter);
        mPresenter = presenter;

        AddFundsModel model = new AddFundsModel();
        mPresenter.setModel(model);
        mPresenter.init();
        getBinding().contentIncluded.setModel(model);

        // Sets up toolbar
        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mPresenter.loadMoneyItems();

        getBinding().contentIncluded.mcAddFunds.setListener(this);

        // Sets up the buttons for payment origin selection
        setupRadioButtons();

        // Set up buttons for Add Payment and Add Funds
        getBinding().contentIncluded.btnAddPayment.setOnClickListener(view -> mPresenter.navigateToAddPayment());
        getBinding().contentIncluded.btnAddFunds.setOnClickListener(view -> mPresenter.addFundsFromCardOnFile(""));

        getBinding().tbTitle.setText(
                isCheckoutAddFunds() ? R.string.finish_and_pay : R.string.add_payment);

        getBinding().contentIncluded.btnAddPayment.setText(
                isCheckoutAddFunds() ? R.string.finish_and_pay : R.string.add_payment);

        getBinding().contentIncluded.btnAddFunds.setText(
                isCheckoutAddFunds() ? R.string.finish_and_pay : R.string.add_funds);

        getBinding().tbTitle.setContentDescription(getString(R.string.heading_cd, getBinding().tbTitle.getText()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.REQUEST_CODE_ADD_PAYMENT && resultCode != RESULT_CANCELED) {
            TokenResponse tokenResponse = CCInformationActivity.getTokenFromIntent(data);
            if (resultCode == AppConstants.RESULT_CODE_ONE_TIME_ADD_FUNDS) {
                mPresenter.addFundsOneTime(tokenResponse.getPaymentToken());
            } else if (resultCode == AppConstants.RESULT_CODE_ADD_FUNDS_FROM_FILE) {
                mPresenter.addFundsFromCardOnFile(tokenResponse.getPaymentToken());
            }
        } else {
            super.onActivityResult(requestCode, requestCode, data);
        }
    }

    @Override
    protected AppScreen getScreenName() {
        return isCheckoutAddFunds() ? AppScreen.ADD_FUNDS_OA : AppScreen.ADD_FUNDS;
    }

    private boolean isCheckoutAddFunds() {
        return getIntent().getBooleanExtra(AppConstants.EXTRA_ADD_FUNDS_FROM_CHECKOUT, false);
    }

    private BigDecimal getFundsNeededToAdd() {
        return (BigDecimal) getIntent().getSerializableExtra(AppConstants.EXTRA_FUNDS_NEEDED_TO_ADD);
    }

    private void setupRadioButtons() {
        getBinding().contentIncluded.rbExistingCard.setOnCheckedChangeListener(this);
        getBinding().contentIncluded.rbNewCard.setOnCheckedChangeListener(this);

        // We set the default as using the existing card
        getBinding().contentIncluded.rbExistingCard.setChecked(true);
    }


    /**
     * Listener for monetary amount selection and monetary origin
     *
     * @param compoundButton
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (!isChecked) {
            return;
        }
        int compoundButtonId = compoundButton.getId();
        if (compoundButtonId == R.id.rb_existing_card) {
            mPresenter.setUseCardOnFile(true);
        } else if (compoundButtonId == R.id.rb_new_card) {
            mPresenter.setUseCardOnFile(false);
        }
    }

    @Override
    public void optionChosen(OptionItem option) {
        mPresenter.setMoneyValue((BigDecimal) option.getValue());
    }

    @Override
    public void onBackPressed() {
        if (isLoading()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean amountToAddErrorEnabled(boolean enabled) {
        getBinding().contentIncluded.tilAmountToAdd.setVisibility(enabled ? View.VISIBLE : View.GONE);
        if (enabled) {
            getBinding().contentIncluded.tilAmountToAdd.requestFocus();
        }
        return enabled;
    }

    @Override
    public void showAddPayment(boolean show) {
        UIUtil.setOnlyChildVisible(show ? getBinding().contentIncluded.btnAddPayment : getBinding().contentIncluded.btnAddFunds);
    }

    @Override
    public void finishedSuccessfulAddFunds(BigDecimal newBalance, BigDecimal addedAmount) {
        Intent data = new Intent();
        data.putExtra(AppConstants.EXTRA_NEW_BALANCE, newBalance);
        data.putExtra(AppConstants.EXTRA_ADD_FUNDS_AMOUNT, addedAmount);
        setResult(RESULT_OK, data);
        finish();
        if (isCheckoutAddFunds()) {
            // Disable slide animation when returning to caller activity for order checkout flow
            // From: https://stackoverflow.com/questions/2286315/disable-activity-slide-in-animation-when-launching-new-activity
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public void goToAddPayment(String token) {
        startActivityForResult(
                CCInformationActivity
                        .createIntent(this,
                                CCInformationActivity.AddPaymentOrigin.ADD_FUNDS, true, token, isCheckoutAddFunds()),
                AppConstants.REQUEST_CODE_ADD_PAYMENT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    public void showInsufficientAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.oops)
                .setMessage(getString(R.string.insufficient_amount_for_order))
                .setPositiveButton(R.string.okay, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void setupMoneyItems(List<BigDecimal> moneyAmounts, List<BigDecimal> dropdownMoneyAmounts) {
        getBinding().contentIncluded.mcAddFunds.setOptions(createOptions(moneyAmounts), createOptions(dropdownMoneyAmounts));
    }

    private List<OptionItem> createOptions(List<BigDecimal> amounts) {
        return amounts == null ? null
                : StreamSupport.stream(amounts).map(amount -> OptionFactory.getInstance(this).createMoneyOption(amount)).collect(Collectors.toList());
    }

}
