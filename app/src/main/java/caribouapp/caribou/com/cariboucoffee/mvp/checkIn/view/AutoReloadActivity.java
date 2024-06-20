package caribouapp.caribou.com.cariboucoffee.mvp.checkIn.view;

import android.app.Activity;
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
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.common.CCInformationActivity;
import caribouapp.caribou.com.cariboucoffee.common.OptionFactory;
import caribouapp.caribou.com.cariboucoffee.common.OptionItem;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityAutoreloadBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.AutoReloadContract;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.AutoReloadModel;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.presenter.AutoReloadPresenter;
import caribouapp.caribou.com.cariboucoffee.util.UIUtil;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by jmsmuy on 11/30/17.
 */

public class AutoReloadActivity extends BaseActivity<ActivityAutoreloadBinding> implements AutoReloadContract.View,
        CompoundButton.OnCheckedChangeListener {

    public static Intent createIntent(Context context) {
        return new Intent(context, AutoReloadActivity.class);
    }

    private static final String TAG = AutoReloadActivity.class.getSimpleName();

    private AutoReloadContract.Presenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_autoreload;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AutoReloadPresenter presenter = new AutoReloadPresenter(this);
        SourceApplication.get(this).getComponent().inject(presenter);
        mPresenter = presenter;

        AutoReloadModel model = new AutoReloadModel();
        mPresenter.setModel(model);
        getBinding().contentIncluded.setModel(model);

        // Sets up toolbar
        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Sets up the buttons for payment origin selection
        setupRadioButtons();

        getBinding().contentIncluded.mcAddAmount.setListener(moneyAmount -> mPresenter.setMoneyAddValue((BigDecimal) moneyAmount.getValue()));
        getBinding().contentIncluded.mcThreshold.setListener(moneyAmount -> mPresenter.setMoneyThresholdValue((BigDecimal) moneyAmount.getValue()));

        // Set up link for t&c and listener for checkbox
        getBinding().contentIncluded.tvGoToTNC.setOnClickListener(view -> mPresenter.termsAndConditionsClicked());
        getBinding().contentIncluded.cbTNC.setOnCheckedChangeListener((compoundButton, b) -> mPresenter.setTermsAndConditions(b));

        // Set up buttons for Add Payment and Add Funds
        getBinding().contentIncluded.btnAddPayment.setOnClickListener(view -> mPresenter.navigateToAddPayment());
        getBinding().contentIncluded.btnApplySettings.setOnClickListener(view -> mPresenter.configureAutoreload());
        getBinding().contentIncluded.btnClose.setOnClickListener(view -> finish());

        mPresenter.loadMoneyAmounts();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.REQUEST_CODE_ADD_PAYMENT && resultCode != Activity.RESULT_CANCELED) {
            // Here we update the onscreen layout
            mPresenter.updateModel();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void setupMoneyAmounts(List<BigDecimal> moneyToAddAmounts, List<BigDecimal> moneyToAddDropdownAmounts,
                                  List<BigDecimal> moneyThresholdAddAmounts, List<BigDecimal> moneyThresholdDropdownAmounts) {
        getBinding().contentIncluded.mcAddAmount.setOptions(createOptions(moneyToAddAmounts), createOptions(moneyToAddDropdownAmounts));
        getBinding().contentIncluded.mcThreshold.setOptions(createOptions(moneyThresholdAddAmounts), createOptions(moneyThresholdDropdownAmounts));
    }


    private List<OptionItem> createOptions(List<BigDecimal> amounts) {
        return StreamSupport.stream(amounts).map(amount -> OptionFactory.getInstance(this).createMoneyOption(amount)).collect(Collectors.toList());
    }

    @Override
    public void setupMoneyAmountsSelected(BigDecimal selectedMoneyToAdd, BigDecimal selectedThresholdValue) {
        getBinding().contentIncluded.mcAddAmount.setSelectedValue(OptionFactory.getInstance(this).createMoneyOption(selectedMoneyToAdd));
        getBinding().contentIncluded.mcThreshold.setSelectedValue(OptionFactory.getInstance(this).createMoneyOption(selectedThresholdValue));
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
    public boolean tnCErrorEnabled(boolean enabled) {
        getBinding().contentIncluded.tvSeeTNCError.setVisibility(enabled ? View.VISIBLE : View.GONE);
        getBinding().contentIncluded.sv.fullScroll(View.FOCUS_DOWN);
        return enabled;
    }

    @Override
    public boolean addThisAmountErrorEnabled(boolean enabled) {
        getBinding().contentIncluded.tvAddAmountError.setVisibility(enabled ? View.VISIBLE : View.GONE);
        return enabled;
    }

    @Override
    public boolean thresholdAmountErrorEnabled(boolean enabled) {
        getBinding().contentIncluded.tvThresholdError.setVisibility(enabled ? View.VISIBLE : View.GONE);
        return enabled;
    }

    @Override
    public void settingsApplied(BigDecimal threshold, BigDecimal amountToAdd) {
        Intent data = new Intent();
        data.putExtra(AppConstants.EXTRA_AUTO_RELOAD_ADD_AMOUNT, amountToAdd);
        data.putExtra(AppConstants.EXTRA_AUTO_THRESHOLD, threshold);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void goToAddPayment(String token) {
        startActivityForResult(
                CCInformationActivity
                        .createIntent(this, CCInformationActivity.AddPaymentOrigin.AUTORELOAD, false, token, false),
                AppConstants.REQUEST_CODE_ADD_PAYMENT);
    }

    @Override
    public void goToTermsAndConditions() {
        startActivity(new Intent(AutoReloadActivity.this, TermsAndConditionsActivity.class));
    }

    @Override
    public void showAddPayment(boolean show) {
        UIUtil.setOnlyChildVisible(show ? getBinding().contentIncluded.btnAddPayment : getBinding().contentIncluded.btnApplySettings);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
