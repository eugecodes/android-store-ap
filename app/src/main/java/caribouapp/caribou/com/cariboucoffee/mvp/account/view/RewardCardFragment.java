package caribouapp.caribou.com.cariboucoffee.mvp.account.view;


import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.databinding.FragmentRewardsCardBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.BaseFragment;
import caribouapp.caribou.com.cariboucoffee.mvp.account.AccountContract;
import caribouapp.caribou.com.cariboucoffee.mvp.account.model.RewardsCardModel;
import caribouapp.caribou.com.cariboucoffee.mvp.account.presenter.RewardsCardPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.presenter.AutoReloadPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.view.AddFundsActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.view.AutoReloadActivity;

import static android.app.Activity.RESULT_OK;
import static caribouapp.caribou.com.cariboucoffee.AppConstants.EXTRA_AUTO_RELOAD_ADD_AMOUNT;
import static caribouapp.caribou.com.cariboucoffee.AppConstants.EXTRA_AUTO_THRESHOLD;
import static caribouapp.caribou.com.cariboucoffee.AppConstants.REQUEST_CODE_ADD_FUNDS;
import static caribouapp.caribou.com.cariboucoffee.AppConstants.REQUEST_CODE_AUTO_RELOAD;

/**
 * Created by gonzalogelos on 9/3/18.
 */

public class RewardCardFragment extends BaseFragment<FragmentRewardsCardBinding> implements AccountContract.RewardsCardView {

    private static final String TAG = AutoReloadPresenter.class.getSimpleName();
    private AccountContract.RewardsCardPresenter mPresenter;
    private RewardsCardModel mModel;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rewards_card;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (mModel == null) {
            mModel = new RewardsCardModel();
        }
        RewardsCardPresenter rewardCardPresenter = new RewardsCardPresenter(this, mModel);
        SourceApplication.get(getContext()).getComponent().inject(rewardCardPresenter);
        mPresenter = rewardCardPresenter;
        mPresenter.loadData();
        getBinding().setModel(mModel);

        getBinding().swAutoReloadSettings.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isPressed()) {
                Log.d(TAG, "Switch state changed: " + b);
                mPresenter.autoReloadClicked(b);
            }
        });
        getBinding().tvEditAutoReloadOptions.setOnClickListener(v -> goToAutoReloadSetting());
        getBinding().btnAddFunds.setOnClickListener(v -> goToAddFunds());
        return view;
    }

    @Override
    public void setAutoReload(boolean enabled, BigDecimal thresholdAmount, BigDecimal incrementAmount) {
        getBinding().swAutoReloadSettings.setChecked(enabled);
        getBinding().tvAutoRealoadText.setText(enabled
                ? getString(R.string.auto_reload_on_hint, incrementAmount.setScale(0, RoundingMode.DOWN),
                thresholdAmount.setScale(0, RoundingMode.DOWN)) : getString(R.string.turn_on_auto_reload_hint));
    }


    @Override
    public void setCardNumber(String cardNumber) {
        String maskCardNumber;
        maskCardNumber = cardNumber == null ? getString(R.string.rewards_card_masking, getString(R.string.default_rewards_card_number))
                : getString(R.string.rewards_card_masking, cardNumber);
        getBinding().tvCardNumber.setText(maskCardNumber);
    }

    @Override
    public void goToAutoReloadSetting() {
        startActivityForResult(AutoReloadActivity.createIntent(this.getContext()), REQUEST_CODE_AUTO_RELOAD);
    }

    private void goToAddFunds() {
        startActivityForResult(AddFundsActivity.createIntent(this.getContext()), REQUEST_CODE_ADD_FUNDS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_AUTO_RELOAD) {
            if (resultCode == RESULT_OK) {
                mPresenter.setAutoReload(true);
                setAutoReload(true, (BigDecimal) data.getSerializableExtra(EXTRA_AUTO_THRESHOLD),
                        (BigDecimal) data.getSerializableExtra(EXTRA_AUTO_RELOAD_ADD_AMOUNT));
                showMessage(R.string.auto_reload_applied);
            } else {
                getBinding().swAutoReloadSettings.setChecked(mModel.getAutoReloadEnabled());
            }
        } else if (requestCode == REQUEST_CODE_ADD_FUNDS && resultCode == RESULT_OK) {
            BigDecimal newBalance = (BigDecimal) data.getSerializableExtra(AppConstants.EXTRA_NEW_BALANCE);
            BigDecimal addFundsAmount = (BigDecimal) data.getSerializableExtra(AppConstants.EXTRA_ADD_FUNDS_AMOUNT);
            mPresenter.setBalance(newBalance);
            showAddFundsDialog(addFundsAmount);
        } else if (requestCode == AppConstants.REQUEST_CODE_CARIBOU_WEBSITE && resultCode == RESULT_OK) {
            // Handle response from manage card mobile website. Updating the balance and card number as needed.
            Uri dataUri = data.getData();

            String newBalance = dataUri.getQueryParameter("newCardBalance");
            if (newBalance != null) {
                mPresenter.setBalance(new BigDecimal(newBalance));
            }

            String newCardNumber = dataUri.getQueryParameter("newCardNumber");
            if (newCardNumber != null) {
                mPresenter.updateCardNumber();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showAddFundsDialog(BigDecimal addFundsAmount) {
        new AlertDialog.Builder(this.getContext())
                .setTitle(R.string.congratulations)
                .setMessage(getString(R.string.add_funds_confirmation, addFundsAmount))
                .setPositiveButton(R.string.dialog_close, (dialog, which) -> dialog.dismiss())
                .show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }
}
