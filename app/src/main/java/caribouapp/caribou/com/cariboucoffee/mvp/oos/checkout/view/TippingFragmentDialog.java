package caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.view;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hannesdorfmann.fragmentargs.FragmentArgs;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import caribouapp.caribou.com.cariboucoffee.R;
import caribouapp.caribou.com.cariboucoffee.databinding.DialogCustomTippingBinding;
import caribouapp.caribou.com.cariboucoffee.mvp.BaseDialogFragment;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.CustomTipContract;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.TippingListener;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.CustomTipOption;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.PercentageTipOption;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.RoundUpTipOption;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.model.TippingOption;
import caribouapp.caribou.com.cariboucoffee.mvp.oos.checkout.presenter.CustomTipPresenter;
import caribouapp.caribou.com.cariboucoffee.order.Order;

@FragmentWithArgs
public class TippingFragmentDialog extends BaseDialogFragment<DialogCustomTippingBinding> implements CustomTipContract.View, TippingListener {


    private DialogCustomTippingBinding mBinding;
    private TippingOptionAdapter mOptionAdapter;
    @Arg
    Order mOrder;
    @Arg
    TippingOption mSelectedTippingOption;
    private TippingListener mTippingListener;
    private CustomTipContract.Presenter mTipPresenter;
    private List<PercentageTipOption> mPercentageTipOptions;
    private BigDecimal mOrderTotal;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext(), R.style.TippingDialogStyle)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dismiss())
                .setTitle(R.string.select_tip)
                .setView(createView()).create();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_custom_tipping;
    }

    private View createView() {
        mTippingListener = (TippingListener) getActivity();
        FragmentArgs.inject(this);
        mBinding = DialogCustomTippingBinding.inflate(LayoutInflater.from(getContext()));
        mTipPresenter = new CustomTipPresenter(this, mOrder);

        mBinding.setOrderTotal(mOrderTotal);
        mTipPresenter.loadTipOptions();
        mBinding.etCustomTippingAmount.setDecimals(true);
        mBinding.etCustomTippingAmount.setCurrency("$");
        mBinding.etCustomTippingAmount.setSeparator(".");
        mTipPresenter.selectedTippingOption(mSelectedTippingOption);
        mBinding.etCustomTippingAmount.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                deselectTipOptions();
            }
        });
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        AlertDialog alertDialog = (AlertDialog) getDialog();
        if (alertDialog == null) {
            return;
        }
        //Override onclick listener after dialog show so we avoid to dismiss dialog on positive button
        // and we can check the maximum tip amount and set error with out dismissing the dialog
        Button positiveButton = (Button) alertDialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            if (mSelectedTippingOption != null) {
                mTippingListener.setTipping(mSelectedTippingOption);
                dismiss();
                return;
            }
            Editable customTip = mBinding.etCustomTippingAmount.getText();
            if (customTip == null || customTip.toString().isEmpty()) {
                return;
            }

            BigDecimal customTipBigDecimal = new BigDecimal(customTip.toString().replaceAll("[,$]", ""));
            mTipPresenter.checkValidCustomTip(customTipBigDecimal);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTipPresenter.detachView();
    }

    @Override
    public void setTipping(TippingOption tippingOption) {
        mTippingListener.setTipping(tippingOption);
        getDialog().dismiss();
    }

    @Override
    public void setCustomTip(BigDecimal tippingValue) {
        mBinding.etCustomTippingAmount.setText(String.format(Locale.US, tippingValue.toString()));
    }

    @Override
    public void setRoundUpSelected() {
        mBinding.tippingRoundUp.llTippingContainer.setSelected(true);
    }

    @Override
    public void setPercentageOptionSelected(PercentageTipOption tippingOption) {
        mOptionAdapter.setSelectedOption(tippingOption);
    }

    @Override
    public void applyValidCustomTip(BigDecimal customTip) {
        mTippingListener.setTipping(new CustomTipOption(customTip));
        dismiss();
    }

    @Override
    public void setMaxValueTipError() {
        mBinding.tilCustomTip.setError(getString(R.string.custom_tip_max_amount_error));
    }

    @Override
    public void showPercentageOptions(List<PercentageTipOption> percentageTipOptions) {
        mPercentageTipOptions = new ArrayList<>(percentageTipOptions);
        mOptionAdapter = new TippingOptionAdapter(this, mOrder);
        mOptionAdapter.setItems(mPercentageTipOptions);
        RecyclerView rvTipping = mBinding.rvTipping;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        rvTipping.setLayoutManager(gridLayoutManager);
        rvTipping.setAdapter(mOptionAdapter);
    }

    @Override
    public void showRoundUpOption(RoundUpTipOption roundUpTipOption) {
        mBinding.tippingRoundUp.setAmountOfTip(roundUpTipOption.calculateTip(mOrder));
        mBinding.tippingRoundUp.llTippingContainer.setOnClickListener(onClick -> {
            mTippingListener.setTipping(roundUpTipOption);
            getDialog().dismiss();
        });
    }

    private void deselectTipOptions() {
        mSelectedTippingOption = null;
        mOptionAdapter.setSelectedOption(null);
        mBinding.tippingRoundUp.llTippingContainer.setSelected(false);
    }
}
