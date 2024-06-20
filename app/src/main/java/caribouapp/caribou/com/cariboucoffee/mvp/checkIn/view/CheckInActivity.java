package caribouapp.caribou.com.cariboucoffee.mvp.checkIn.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import caribouapp.caribou.com.cariboucoffee.AppConstants;
import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.SourceApplication;
import caribouapp.caribou.com.cariboucoffee.analytics.AppScreen;
import caribouapp.caribou.com.cariboucoffee.analytics.EventLogger;
import caribouapp.caribou.com.cariboucoffee.common.BaseActivity;
import caribouapp.caribou.com.cariboucoffee.databinding.ActivityCheckInPayBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.SettingsServices;
import caribouapp.caribou.com.cariboucoffee.mvp.authentication.UserServices;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.CheckInContract;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.CheckInModel;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardItemModel;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.model.RewardModel;
import caribouapp.caribou.com.cariboucoffee.mvp.checkIn.presenter.CheckInPresenter;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.picklocation.view.PickLocationActivity;
import caribouapp.caribou.com.cariboucoffee.mvp.webflow.view.SourceWebActivity;
import caribouapp.caribou.com.cariboucoffee.util.DeeplinkUtil;
import icepick.Icepick;
import icepick.State;

public class CheckInActivity extends BaseActivity<ActivityCheckInPayBinding> implements CheckInContract.View {

    @Inject
    EventLogger mEventLogger;

    @Inject
    SettingsServices mSettingsServices;

    @Inject
    UserServices mUserServices;

    @State
    CheckInModel mModel;

    private CheckInContract.Presenter mPresenter;
    private RewardsCardAdapter mCardAdapter;
    private MenuItem mShareAPerkMenuItem;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_check_in_pay;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Icepick.restoreInstanceState(this, savedInstanceState);

        // Sets up toolbar
        setSupportActionBar(getBinding().tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getBinding().tb.inflateMenu(R.menu.check_in_pay_menu);

        if (mModel == null) {
            mModel = new CheckInModel();
        }

        SourceApplication.get(this).getComponent().inject(this);
        CheckInPresenter presenter = new CheckInPresenter(this, mModel);
        SourceApplication.get(this).getComponent().inject(presenter);

        mPresenter = presenter;

        getBinding().contentIncluded.setModel(mModel);

        getBinding().contentIncluded.ibAddFunds.setOnClickListener(view -> mPresenter.addFundsClicked());

        mCardAdapter = new RewardsCardAdapter(mPresenter);
        getBinding().contentIncluded.rvOffers.setLayoutManager(new LinearLayoutManager(this));
        getBinding().contentIncluded.rvOffers.setAdapter(mCardAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.loadData(false);
    }

    @Override
    public void setCards(List<RewardModel> rewards) {
        if (mModel.isShowMessageError()) {
            getBinding().contentIncluded.rvOffers.setVisibility(View.GONE);
            return;
        }
        getBinding().contentIncluded.rvOffers.setVisibility(View.VISIBLE);
        mCardAdapter.setList(rewards);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setMaxBrightness();
        mEventLogger.logOpenedCheckIn();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.check_in_pay_menu, menu);

        // Locate MenuItem with ShareActionProvider
        mShareAPerkMenuItem = menu.findItem(R.id.perk_share);
        mShareAPerkMenuItem.setOnMenuItemClickListener(item -> {
            startActivity(createShareAPerkIntent());
            return true;
        });

        mShareAPerkMenuItem.setVisible(mSettingsServices.isShareAPerkEnabled());

        if (!mSettingsServices.isShareAPerkEnabled()) {
            mUserServices.setShowShareAPerkOnboarding(true);
            return true;
        }

        if (mUserServices.isShowShareAPerkOnboarding()) {
            TapTargetView.showFor(this,
                    TapTarget.forToolbarMenuItem(getBinding().tb, R.id.perk_share, getString(R.string.make_someone_day),
                            getString(R.string.checkin_share_onboarding)
                    ));
            mUserServices.setShowShareAPerkOnboarding(false);
        }

        return true;
    }

    @Override
    protected AppScreen getScreenName() {
        return AppScreen.CHECK_IN;
    }

    private Intent createShareAPerkIntent() {
        return SourceWebActivity
                .createIntentFromPath(this, getString(R.string.share_a_perk_title),
                        getString(R.string.share_a_perk_path),
                        DeeplinkUtil.buildSourceAppFinishActivity(this),
                        true, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    private void setMaxBrightness() {
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        float settingScreenBrightness = (float) mSettingsServices.getBarcodeScreenBrightness();
        layoutParams.screenBrightness = settingScreenBrightness > layoutParams.screenBrightness
                ? settingScreenBrightness : layoutParams.screenBrightness;
        window.setAttributes(layoutParams);
    }

    @Override
    public void setBarcode(Bitmap barcodeBitmap) {
        getBinding().contentIncluded.ivBarcode.setImageBitmap(barcodeBitmap);
    }

    @Override
    public void goToAddFunds() {
        startActivityForResult(AddFundsActivity.createIntent(this), AppConstants.REQUEST_CODE_ADD_FUNDS);
    }

    @Override
    public void showSuccessOnRedeem() {
        showMessage(R.string.redeem_successful);
    }

    @Override
    public void goToStartNewOrder(RewardItemModel rewardItemModel) {
        startActivity(PickLocationActivity.createIntent(this, rewardItemModel.isAutoApply() ? null : rewardItemModel));
        finish();
    }

    @Override
    public void showRedeemConfirmation(RewardItemModel rewardItemModel) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setCancelable(true)
                .setMessage(R.string.confirmation_redeem_offer)
                .setNegativeButton(R.string.not_redeem_offer, null)
                .setPositiveButton(R.string.redeem_offer, (dialogInterface, i) -> mPresenter.redeemReward(rewardItemModel))
                .create().show();
    }

    @Override
    public void showStartOrderAfterRedeeming(RewardItemModel rewardItemModel) {
        AlertDialog goToStartOrderDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.you_did_it)
                .setMessage(getString(R.string.want_to_use_on_new_order))
                .setNegativeButton(R.string.no_thanks, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.yes_start_mobile_order, (dialog, which) -> goToStartNewOrder(rewardItemModel))
                .create();
        goToStartOrderDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.REQUEST_CODE_ADD_FUNDS && resultCode == RESULT_OK) {
            // Update balance
            BigDecimal newBalance = (BigDecimal) data.getSerializableExtra(AppConstants.EXTRA_NEW_BALANCE);
            BigDecimal addFundsAmount = (BigDecimal) data.getSerializableExtra(AppConstants.EXTRA_ADD_FUNDS_AMOUNT);
            mPresenter.setBalance(newBalance);
            showAddFundsDialog(addFundsAmount);
        } else if (requestCode == AppConstants.REQUEST_CODE_AUTO_RELOAD && resultCode == RESULT_OK) {
            mPresenter.loadData(true);
            showMessage(R.string.auto_reload_applied);
        } else if (requestCode == AppConstants.REQUEST_CODE_CARIBOU_WEBSITE && resultCode == RESULT_OK) {
            // Handle response from manage card mobile website. Updating the balance and card number as needed.
            Uri dataUri = data.getData();

            String newBalance = dataUri.getQueryParameter("newCardBalance");
            if (newBalance != null) {
                mPresenter.setBalance(new BigDecimal(newBalance));
            }

            String newCardNumber = dataUri.getQueryParameter("newCardNumber");
            if (newCardNumber != null) {
                mPresenter.setCardNumber(newCardNumber, true);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showAddFundsDialog(BigDecimal addFundsAmount) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.congratulations)
                .setMessage(getString(R.string.add_funds_confirmation, addFundsAmount))
                .setPositiveButton(R.string.dialog_close, (dialog, which) -> dialog.dismiss()).create();
        alertDialog
                .show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setContentDescription(getString(R.string.dialog_close));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
